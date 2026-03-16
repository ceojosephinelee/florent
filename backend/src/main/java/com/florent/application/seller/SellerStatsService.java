package com.florent.application.seller;

import com.florent.common.exception.BusinessException;
import com.florent.common.exception.ErrorCode;
import com.florent.common.util.HaversineUtil;
import com.florent.domain.proposal.Proposal;
import com.florent.domain.proposal.ProposalRepository;
import com.florent.domain.proposal.ProposalStatus;
import com.florent.domain.request.CurationRequest;
import com.florent.domain.request.CurationRequestRepository;
import com.florent.domain.reservation.Reservation;
import com.florent.domain.reservation.ReservationRepository;
import com.florent.domain.seller.GetSellerStatsUseCase;
import com.florent.domain.seller.SellerStatsResult;
import com.florent.domain.shop.FlowerShop;
import com.florent.domain.shop.FlowerShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SellerStatsService implements GetSellerStatsUseCase {

    private static final double RADIUS_KM = 2.0;
    private static final int RECENT_RESERVATION_LIMIT = 10;

    private final FlowerShopRepository shopRepository;
    private final CurationRequestRepository requestRepository;
    private final ProposalRepository proposalRepository;
    private final ReservationRepository reservationRepository;
    private final Clock clock;

    @Override
    @Transactional(readOnly = true)
    public SellerStatsResult getStats(Long sellerId) {
        FlowerShop shop = shopRepository.findBySellerId(sellerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SHOP_NOT_FOUND));

        LocalDateTime monthStart = YearMonth.now(clock).atDay(1).atStartOfDay();

        int monthlyReceived = countMonthlyNearbyRequests(shop, monthStart);
        int monthlySubmitted = countMonthlySubmittedProposals(shop, monthStart);

        List<Reservation> sellerReservations = reservationRepository.findAllBySellerId(sellerId);
        int monthlyConfirmed = countMonthlyConfirmed(sellerReservations, monthStart);

        List<SellerStatsResult.RecentReservationItem> recentReservations =
                buildRecentReservations(sellerReservations, shop.getId());

        return new SellerStatsResult(
                monthlyReceived,
                monthlySubmitted,
                monthlyConfirmed,
                recentReservations
        );
    }

    private int countMonthlyNearbyRequests(FlowerShop shop, LocalDateTime monthStart) {
        return (int) requestRepository.findAll().stream()
                .filter(r -> r.getCreatedAt().isAfter(monthStart) || r.getCreatedAt().isEqual(monthStart))
                .filter(r -> HaversineUtil.isWithinRadius(
                        r.getPlaceLat(), r.getPlaceLng(),
                        shop.getShopLat(), shop.getShopLng(), RADIUS_KM))
                .count();
    }

    private int countMonthlySubmittedProposals(FlowerShop shop, LocalDateTime monthStart) {
        return (int) proposalRepository.findAllByFlowerShopId(shop.getId()).stream()
                .filter(p -> p.getStatus() == ProposalStatus.SUBMITTED
                        || p.getStatus() == ProposalStatus.SELECTED
                        || p.getStatus() == ProposalStatus.NOT_SELECTED)
                .filter(p -> p.getSubmittedAt() != null
                        && (p.getSubmittedAt().isAfter(monthStart) || p.getSubmittedAt().isEqual(monthStart)))
                .count();
    }

    private int countMonthlyConfirmed(List<Reservation> reservations, LocalDateTime monthStart) {
        return (int) reservations.stream()
                .filter(r -> r.getConfirmedAt().isAfter(monthStart)
                        || r.getConfirmedAt().isEqual(monthStart))
                .count();
    }

    private List<SellerStatsResult.RecentReservationItem> buildRecentReservations(
            List<Reservation> reservations, Long shopId) {
        List<Long> proposalIds = reservations.stream()
                .map(Reservation::getProposalId).toList();
        Map<Long, Proposal> proposalMap = proposalRepository.findAllByIds(proposalIds).stream()
                .collect(Collectors.toMap(Proposal::getId, Function.identity()));

        return reservations.stream()
                .sorted(Comparator.comparing(Reservation::getConfirmedAt).reversed())
                .limit(RECENT_RESERVATION_LIMIT)
                .map(r -> {
                    Proposal p = proposalMap.get(r.getProposalId());
                    return new SellerStatsResult.RecentReservationItem(
                            r.getId(),
                            p != null ? p.getConceptTitle() : null,
                            p != null ? p.getPrice() : null,
                            r.getFulfillmentType(),
                            r.getConfirmedAt()
                    );
                })
                .toList();
    }
}
