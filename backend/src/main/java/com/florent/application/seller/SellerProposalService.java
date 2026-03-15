package com.florent.application.seller;

import com.florent.common.exception.BusinessException;
import com.florent.common.exception.ErrorCode;
import com.florent.domain.notification.SaveNotificationPort;
import com.florent.domain.proposal.GetSellerProposalDetailUseCase;
import com.florent.domain.proposal.GetSellerProposalListUseCase;
import com.florent.domain.proposal.Proposal;
import com.florent.domain.proposal.ProposalDetail;
import com.florent.domain.proposal.ProposalPage;
import com.florent.domain.proposal.ProposalRepository;
import com.florent.domain.proposal.SaveProposalCommand;
import com.florent.domain.proposal.SaveProposalResult;
import com.florent.domain.proposal.SaveProposalUseCase;
import com.florent.domain.proposal.SellerProposalListResult;
import com.florent.domain.proposal.SellerProposalSummaryResult;
import com.florent.domain.proposal.StartProposalCommand;
import com.florent.domain.proposal.StartProposalResult;
import com.florent.domain.proposal.StartProposalUseCase;
import com.florent.domain.proposal.SubmitProposalCommand;
import com.florent.domain.proposal.SubmitProposalResult;
import com.florent.domain.proposal.SubmitProposalUseCase;
import com.florent.domain.request.CurationRequest;
import com.florent.domain.request.CurationRequestRepository;
import com.florent.domain.shop.FlowerShop;
import com.florent.domain.shop.FlowerShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SellerProposalService implements StartProposalUseCase, SaveProposalUseCase,
        SubmitProposalUseCase, GetSellerProposalListUseCase, GetSellerProposalDetailUseCase {

    private final ProposalRepository proposalRepository;
    private final CurationRequestRepository requestRepository;
    private final FlowerShopRepository shopRepository;
    private final SaveNotificationPort saveNotificationPort;
    private final Clock clock;

    @Override
    public StartProposalResult start(StartProposalCommand command) {
        FlowerShop shop = findShopBySellerId(command.sellerId());

        CurationRequest request = requestRepository.findById(command.requestId())
                .orElseThrow(() -> new BusinessException(ErrorCode.REQUEST_NOT_FOUND));

        request.validateAcceptingProposals();

        if (proposalRepository.existsByRequestIdAndFlowerShopId(
                command.requestId(), shop.getId())) {
            throw new BusinessException(ErrorCode.PROPOSAL_ALREADY_EXISTS);
        }

        Proposal proposal = Proposal.create(command.requestId(), shop.getId(), clock);
        Proposal saved = proposalRepository.save(proposal);
        return StartProposalResult.from(saved);
    }

    @Override
    public SaveProposalResult save(SaveProposalCommand command) {
        FlowerShop shop = findShopBySellerId(command.sellerId());

        Proposal proposal = proposalRepository.findById(command.proposalId())
                .orElseThrow(() -> new BusinessException(ErrorCode.PROPOSAL_NOT_FOUND));

        verifyOwnership(proposal, shop);

        proposal.updateDraft(
                command.conceptTitle(), command.moodColors(),
                command.mainFlowers(), command.wrappingStyle(),
                command.allergyNote(), command.careTips(),
                command.description(), command.imageUrls(),
                command.availableSlotKind(), command.availableSlotValue(),
                command.price());

        Proposal saved = proposalRepository.save(proposal);
        return SaveProposalResult.from(saved);
    }

    @Override
    public SubmitProposalResult submit(SubmitProposalCommand command) {
        FlowerShop shop = findShopBySellerId(command.sellerId());

        Proposal proposal = proposalRepository.findById(command.proposalId())
                .orElseThrow(() -> new BusinessException(ErrorCode.PROPOSAL_NOT_FOUND));

        verifyOwnership(proposal, shop);

        CurationRequest request = requestRepository.findById(proposal.getRequestId())
                .orElseThrow(() -> new BusinessException(ErrorCode.REQUEST_NOT_FOUND));

        request.validateAcceptingProposals();

        proposal.submit(clock);
        Proposal saved = proposalRepository.save(proposal);

        saveNotificationPort.saveProposalArrived(
                request.getBuyerId(), saved.getId());

        return SubmitProposalResult.from(saved);
    }

    @Transactional(readOnly = true)
    @Override
    public SellerProposalListResult getMyProposals(Long sellerId, int page, int size) {
        FlowerShop shop = findShopBySellerId(sellerId);

        ProposalPage proposalPage = proposalRepository.findByFlowerShopId(
                shop.getId(), page, size);

        List<SellerProposalSummaryResult> summaries = proposalPage.content().stream()
                .map(SellerProposalSummaryResult::from)
                .toList();

        return new SellerProposalListResult(
                summaries, page, size,
                proposalPage.totalElements(),
                proposalPage.totalPages(),
                proposalPage.last());
    }

    @Transactional(readOnly = true)
    @Override
    public ProposalDetail getSellerProposalDetail(Long proposalId, Long sellerId) {
        FlowerShop shop = findShopBySellerId(sellerId);

        Proposal proposal = proposalRepository.findById(proposalId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROPOSAL_NOT_FOUND));

        verifyOwnership(proposal, shop);

        return toDetail(proposal, shop);
    }

    private ProposalDetail toDetail(Proposal proposal, FlowerShop shop) {
        return new ProposalDetail(
                proposal.getId(),
                proposal.getRequestId(),
                proposal.getStatus(),
                shop.getId(),
                shop.getShopName(),
                shop.getShopPhone(),
                shop.getShopAddress(),
                proposal.getConceptTitle(),
                proposal.getMoodColors(),
                proposal.getMainFlowers(),
                proposal.getWrappingStyle(),
                proposal.getAllergyNote(),
                proposal.getCareTips(),
                proposal.getDescription(),
                proposal.getImageUrls(),
                proposal.getAvailableSlotKind(),
                proposal.getAvailableSlotValue(),
                proposal.getExpiresAt(),
                proposal.getPrice()
        );
    }

    private FlowerShop findShopBySellerId(Long sellerId) {
        return shopRepository.findBySellerId(sellerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SHOP_NOT_FOUND));
    }

    private void verifyOwnership(Proposal proposal, FlowerShop shop) {
        if (!proposal.getFlowerShopId().equals(shop.getId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
    }
}
