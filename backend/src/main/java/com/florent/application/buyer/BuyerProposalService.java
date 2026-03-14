package com.florent.application.buyer;

import com.florent.common.exception.BusinessException;
import com.florent.common.exception.ErrorCode;
import com.florent.domain.proposal.GetProposalDetailUseCase;
import com.florent.domain.proposal.GetProposalListUseCase;
import com.florent.domain.proposal.Proposal;
import com.florent.domain.proposal.ProposalDetail;
import com.florent.domain.proposal.ProposalRepository;
import com.florent.domain.proposal.ProposalSummary;
import com.florent.domain.request.CurationRequest;
import com.florent.domain.request.CurationRequestRepository;
import com.florent.domain.shop.FlowerShop;
import com.florent.domain.shop.FlowerShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BuyerProposalService implements GetProposalListUseCase, GetProposalDetailUseCase {

    private final ProposalRepository proposalRepository;
    private final CurationRequestRepository requestRepository;
    private final FlowerShopRepository shopRepository;

    @Override
    public List<ProposalSummary> getProposalsByRequestId(Long requestId, Long buyerId) {
        CurationRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REQUEST_NOT_FOUND));

        if (!request.getBuyerId().equals(buyerId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        List<Proposal> visible = proposalRepository.findByRequestId(requestId).stream()
                .filter(Proposal::isVisibleToBuyer)
                .toList();

        List<Long> shopIds = visible.stream()
                .map(Proposal::getFlowerShopId)
                .distinct()
                .toList();

        Map<Long, FlowerShop> shopMap = shopRepository.findAllByIds(shopIds).stream()
                .collect(Collectors.toMap(FlowerShop::getId, Function.identity()));

        return visible.stream()
                .map(p -> toSummary(p, shopMap.get(p.getFlowerShopId())))
                .toList();
    }

    @Override
    public ProposalDetail getProposalDetail(Long proposalId, Long buyerId) {
        Proposal proposal = proposalRepository.findById(proposalId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROPOSAL_NOT_FOUND));

        CurationRequest request = requestRepository.findById(proposal.getRequestId())
                .orElseThrow(() -> new BusinessException(ErrorCode.REQUEST_NOT_FOUND));

        if (!request.getBuyerId().equals(buyerId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        FlowerShop shop = shopRepository.findById(proposal.getFlowerShopId())
                .orElseThrow(() -> new BusinessException(ErrorCode.SHOP_NOT_FOUND));

        return toDetail(proposal, shop);
    }

    private ProposalSummary toSummary(Proposal proposal, FlowerShop shop) {
        return new ProposalSummary(
                proposal.getId(),
                shop.getShopName(),
                proposal.getConceptTitle(),
                proposal.getStatus(),
                proposal.getExpiresAt()
        );
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
}
