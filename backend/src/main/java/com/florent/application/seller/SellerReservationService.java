package com.florent.application.seller;

import com.florent.common.exception.BusinessException;
import com.florent.common.exception.ErrorCode;
import com.florent.domain.buyer.Buyer;
import com.florent.domain.buyer.BuyerRepository;
import com.florent.domain.proposal.Proposal;
import com.florent.domain.proposal.ProposalRepository;
import com.florent.domain.request.CurationRequest;
import com.florent.domain.request.CurationRequestRepository;
import com.florent.domain.reservation.GetSellerReservationDetailUseCase;
import com.florent.domain.reservation.GetSellerReservationListUseCase;
import com.florent.domain.reservation.Reservation;
import com.florent.domain.reservation.ReservationRepository;
import com.florent.domain.reservation.SellerReservationDetailResult;
import com.florent.domain.reservation.SellerReservationSummaryResult;
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
public class SellerReservationService implements GetSellerReservationListUseCase,
        GetSellerReservationDetailUseCase {

    private final ReservationRepository reservationRepository;
    private final ProposalRepository proposalRepository;
    private final CurationRequestRepository requestRepository;
    private final BuyerRepository buyerRepository;
    private final FlowerShopRepository shopRepository;

    @Override
    public List<SellerReservationSummaryResult> getList(Long sellerId) {
        List<Reservation> reservations = reservationRepository.findAllBySellerId(sellerId);
        if (reservations.isEmpty()) {
            return List.of();
        }

        List<Long> proposalIds = reservations.stream()
                .map(Reservation::getProposalId).toList();
        Map<Long, Proposal> proposalMap = proposalRepository.findAllByIds(proposalIds)
                .stream().collect(Collectors.toMap(Proposal::getId, Function.identity()));

        List<Long> requestIds = reservations.stream()
                .map(Reservation::getRequestId).distinct().toList();
        Map<Long, CurationRequest> requestMap = requestRepository.findAllByIds(requestIds)
                .stream().collect(Collectors.toMap(CurationRequest::getId, Function.identity()));

        List<Long> buyerIds = requestMap.values().stream()
                .map(CurationRequest::getBuyerId).distinct().toList();
        Map<Long, Buyer> buyerMap = buyerRepository.findAllByIds(buyerIds)
                .stream().collect(Collectors.toMap(Buyer::getId, Function.identity()));

        return reservations.stream()
                .map(r -> toSellerSummary(r, proposalMap, requestMap, buyerMap))
                .toList();
    }

    @Override
    public SellerReservationDetailResult getDetail(Long reservationId, Long sellerId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESERVATION_NOT_FOUND));

        Proposal proposal = proposalRepository.findById(reservation.getProposalId())
                .orElseThrow(() -> new BusinessException(ErrorCode.PROPOSAL_NOT_FOUND));

        FlowerShop shop = shopRepository.findBySellerId(sellerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SHOP_NOT_FOUND));

        if (!proposal.getFlowerShopId().equals(shop.getId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        CurationRequest request = requestRepository.findById(reservation.getRequestId())
                .orElseThrow(() -> new BusinessException(ErrorCode.REQUEST_NOT_FOUND));

        Buyer buyer = buyerRepository.findById(request.getBuyerId())
                .orElseThrow(() -> new BusinessException(ErrorCode.FORBIDDEN));

        return toSellerDetail(reservation, proposal, request, buyer);
    }

    private SellerReservationSummaryResult toSellerSummary(
            Reservation r, Map<Long, Proposal> proposalMap,
            Map<Long, CurationRequest> requestMap,
            Map<Long, Buyer> buyerMap) {
        Proposal proposal = proposalMap.get(r.getProposalId());
        CurationRequest request = requestMap.get(r.getRequestId());
        Buyer buyer = buyerMap.get(request.getBuyerId());
        return new SellerReservationSummaryResult(
                r.getId(), r.getStatus().name(),
                proposal.getConceptTitle(), proposal.getPrice(),
                r.getFulfillmentType(), r.getFulfillmentDate(),
                r.getFulfillmentSlotKind(), r.getFulfillmentSlotValue(),
                buyer != null ? buyer.getNickName() : null,
                r.getConfirmedAt());
    }

    private SellerReservationDetailResult toSellerDetail(
            Reservation r, Proposal proposal,
            CurationRequest request, Buyer buyer) {
        return new SellerReservationDetailResult(
                r.getId(), r.getStatus().name(),
                r.getFulfillmentType(), r.getFulfillmentDate(),
                r.getFulfillmentSlotKind(), r.getFulfillmentSlotValue(),
                buyer.getNickName(),
                r.getPlaceAddressText(), r.getPlaceLat(), r.getPlaceLng(),
                r.getConfirmedAt(),
                new SellerReservationDetailResult.ProposalInfo(
                        proposal.getId(), proposal.getConceptTitle(),
                        proposal.getDescription(), proposal.getImageUrls(),
                        proposal.getPrice()),
                new SellerReservationDetailResult.RequestInfo(
                        request.getId(), request.getPurposeTags(),
                        request.getRelationTags(), request.getMoodTags(),
                        request.getBudgetTier().name()));
    }
}
