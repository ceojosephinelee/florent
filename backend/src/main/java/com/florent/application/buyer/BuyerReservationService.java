package com.florent.application.buyer;

import com.florent.common.exception.BusinessException;
import com.florent.common.exception.ErrorCode;
import com.florent.domain.notification.SaveNotificationUseCase;
import com.florent.domain.payment.Payment;
import com.florent.domain.payment.PaymentPort;
import com.florent.domain.payment.PaymentRepository;
import com.florent.domain.proposal.Proposal;
import com.florent.domain.proposal.ProposalRepository;
import com.florent.domain.request.CurationRequest;
import com.florent.domain.request.CurationRequestRepository;
import com.florent.domain.reservation.BuyerReservationDetailResult;
import com.florent.domain.reservation.BuyerReservationSummaryResult;
import com.florent.domain.reservation.ConfirmReservationCommand;
import com.florent.domain.reservation.ConfirmReservationResult;
import com.florent.domain.reservation.ConfirmReservationUseCase;
import com.florent.domain.reservation.GetBuyerReservationDetailUseCase;
import com.florent.domain.reservation.GetBuyerReservationListUseCase;
import com.florent.domain.reservation.Reservation;
import com.florent.domain.reservation.ReservationRepository;
import com.florent.domain.shop.FlowerShop;
import com.florent.domain.shop.FlowerShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BuyerReservationService implements ConfirmReservationUseCase,
        GetBuyerReservationListUseCase, GetBuyerReservationDetailUseCase {

    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentPort paymentPort;
    private final ProposalRepository proposalRepository;
    private final CurationRequestRepository requestRepository;
    private final FlowerShopRepository shopRepository;
    private final SaveNotificationUseCase saveNotificationPort;
    private final Clock clock;

    @Transactional
    @Override
    public ConfirmReservationResult confirm(ConfirmReservationCommand command) {
        if (paymentRepository.existsByIdempotencyKey(command.idempotencyKey())) {
            throw new BusinessException(ErrorCode.DUPLICATE_PAYMENT);
        }

        Proposal proposal = proposalRepository.findById(command.proposalId())
                .orElseThrow(() -> new BusinessException(ErrorCode.PROPOSAL_NOT_FOUND));

        CurationRequest request = requestRepository.findById(proposal.getRequestId())
                .orElseThrow(() -> new BusinessException(ErrorCode.REQUEST_NOT_FOUND));

        if (!request.getBuyerId().equals(command.buyerId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        if (reservationRepository.existsByRequestId(request.getId())) {
            throw new BusinessException(ErrorCode.REQUEST_ALREADY_CONFIRMED);
        }

        // 6-step transaction
        request.confirm();
        requestRepository.save(request);

        proposal.select();
        proposalRepository.save(proposal);

        markOtherProposalsNotSelected(request.getId(), proposal.getId());

        Reservation reservation = Reservation.create(
                request.getId(), proposal.getId(),
                request.getFulfillmentType().name(),
                request.getFulfillmentDate(),
                proposal.getAvailableSlotKind(),
                proposal.getAvailableSlotValue(),
                request.getPlaceAddressText(),
                request.getPlaceLat(), request.getPlaceLng(),
                clock);
        Reservation savedReservation = reservationRepository.save(reservation);

        Payment payment = paymentPort.pay(
                savedReservation.getId(), proposal.getPrice(),
                command.idempotencyKey(), clock);
        Payment savedPayment = paymentRepository.save(payment);

        FlowerShop shop = shopRepository.findById(proposal.getFlowerShopId())
                .orElseThrow(() -> new BusinessException(ErrorCode.SHOP_NOT_FOUND));
        saveNotificationPort.saveReservationConfirmed(
                shop.getSellerId(), savedReservation.getId());

        return ConfirmReservationResult.from(savedReservation, savedPayment);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BuyerReservationSummaryResult> getList(Long buyerId) {
        List<Reservation> reservations = reservationRepository.findAllByBuyerId(buyerId);
        if (reservations.isEmpty()) {
            return List.of();
        }

        List<Long> proposalIds = reservations.stream()
                .map(Reservation::getProposalId).toList();
        Map<Long, Proposal> proposalMap = proposalRepository.findAllByIds(proposalIds)
                .stream().collect(Collectors.toMap(Proposal::getId, Function.identity()));

        List<Long> shopIds = proposalMap.values().stream()
                .map(Proposal::getFlowerShopId).distinct().toList();
        Map<Long, FlowerShop> shopMap = shopRepository.findAllByIds(shopIds)
                .stream().collect(Collectors.toMap(FlowerShop::getId, Function.identity()));

        return reservations.stream()
                .map(r -> toBuyerSummary(r, proposalMap, shopMap))
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public BuyerReservationDetailResult getDetail(Long reservationId, Long buyerId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESERVATION_NOT_FOUND));

        CurationRequest request = requestRepository.findById(reservation.getRequestId())
                .orElseThrow(() -> new BusinessException(ErrorCode.REQUEST_NOT_FOUND));

        if (!request.getBuyerId().equals(buyerId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        Proposal proposal = proposalRepository.findById(reservation.getProposalId())
                .orElseThrow(() -> new BusinessException(ErrorCode.PROPOSAL_NOT_FOUND));

        FlowerShop shop = shopRepository.findById(proposal.getFlowerShopId())
                .orElseThrow(() -> new BusinessException(ErrorCode.SHOP_NOT_FOUND));

        return toBuyerDetail(reservation, proposal, shop, request);
    }

    private void markOtherProposalsNotSelected(Long requestId, Long selectedProposalId) {
        proposalRepository.findByRequestId(requestId).stream()
                .filter(p -> !p.getId().equals(selectedProposalId))
                .filter(Proposal::isVisibleToBuyer)
                .forEach(p -> {
                    p.markNotSelected();
                    proposalRepository.save(p);
                });
    }

    private BuyerReservationSummaryResult toBuyerSummary(
            Reservation r, Map<Long, Proposal> proposalMap,
            Map<Long, FlowerShop> shopMap) {
        Proposal proposal = proposalMap.get(r.getProposalId());
        FlowerShop shop = shopMap.get(proposal.getFlowerShopId());
        return new BuyerReservationSummaryResult(
                r.getId(), r.getStatus().name(),
                shop.getShopName(), proposal.getConceptTitle(),
                proposal.getPrice(),
                r.getFulfillmentType(), r.getFulfillmentDate(),
                r.getFulfillmentSlotKind(), r.getFulfillmentSlotValue(),
                r.getConfirmedAt());
    }

    private BuyerReservationDetailResult toBuyerDetail(
            Reservation r, Proposal proposal,
            FlowerShop shop, CurationRequest request) {
        return new BuyerReservationDetailResult(
                r.getId(), r.getStatus().name(),
                r.getFulfillmentType(), r.getFulfillmentDate(),
                r.getFulfillmentSlotKind(), r.getFulfillmentSlotValue(),
                r.getPlaceAddressText(), r.getPlaceLat(), r.getPlaceLng(),
                r.getConfirmedAt(),
                new BuyerReservationDetailResult.ProposalInfo(
                        proposal.getId(), proposal.getConceptTitle(),
                        proposal.getDescription(), proposal.getImageUrls(),
                        proposal.getPrice()),
                new BuyerReservationDetailResult.ShopInfo(
                        shop.getId(), shop.getShopName(), shop.getShopPhone(),
                        shop.getShopAddress(), shop.getShopLat(), shop.getShopLng()),
                new BuyerReservationDetailResult.RequestInfo(
                        request.getId(), request.getPurposeTags(),
                        request.getRelationTags(), request.getMoodTags(),
                        request.getBudgetTier().name()));
    }
}
