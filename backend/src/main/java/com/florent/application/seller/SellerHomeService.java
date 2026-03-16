package com.florent.application.seller;

import com.florent.common.exception.BusinessException;
import com.florent.common.exception.ErrorCode;
import com.florent.common.util.HaversineUtil;
import com.florent.domain.proposal.Proposal;
import com.florent.domain.proposal.ProposalRepository;
import com.florent.domain.proposal.ProposalStatus;
import com.florent.domain.request.CurationRequest;
import com.florent.domain.request.CurationRequestRepository;
import com.florent.domain.request.RequestStatus;
import com.florent.domain.reservation.ReservationRepository;
import com.florent.domain.reservation.ReservationStatus;
import com.florent.domain.seller.GetSellerHomeUseCase;
import com.florent.domain.seller.SellerHomeResult;
import com.florent.domain.shop.FlowerShop;
import com.florent.domain.shop.FlowerShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SellerHomeService implements GetSellerHomeUseCase {

    private static final double RADIUS_KM = 2.0;
    private static final int RECENT_REQUEST_LIMIT = 5;

    private final FlowerShopRepository shopRepository;
    private final CurationRequestRepository requestRepository;
    private final ProposalRepository proposalRepository;
    private final ReservationRepository reservationRepository;

    @Override
    @Transactional(readOnly = true)
    public SellerHomeResult getHome(Long sellerId) {
        FlowerShop shop = shopRepository.findBySellerId(sellerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SHOP_NOT_FOUND));

        List<CurationRequest> nearbyOpen = filterNearbyOpenRequests(shop);
        List<Proposal> myProposals = proposalRepository.findAllByFlowerShopId(shop.getId());

        int draftCount = countByStatus(myProposals, ProposalStatus.DRAFT);
        int submittedCount = countByStatus(myProposals, ProposalStatus.SUBMITTED);
        int confirmedCount = (int) reservationRepository.findAllBySellerId(sellerId)
                .stream()
                .filter(r -> r.getStatus() == ReservationStatus.CONFIRMED)
                .count();

        List<SellerHomeResult.RecentRequestItem> recentRequests = nearbyOpen.stream()
                .sorted(Comparator.comparing(CurationRequest::getCreatedAt).reversed())
                .limit(RECENT_REQUEST_LIMIT)
                .map(this::toRecentRequestItem)
                .toList();

        return new SellerHomeResult(
                nearbyOpen.size(),
                draftCount,
                submittedCount,
                confirmedCount,
                recentRequests
        );
    }

    private List<CurationRequest> filterNearbyOpenRequests(FlowerShop shop) {
        return requestRepository.findAll().stream()
                .filter(r -> r.getStatus() == RequestStatus.OPEN)
                .filter(r -> HaversineUtil.isWithinRadius(
                        r.getPlaceLat(), r.getPlaceLng(),
                        shop.getShopLat(), shop.getShopLng(), RADIUS_KM))
                .toList();
    }

    private int countByStatus(List<Proposal> proposals, ProposalStatus status) {
        return (int) proposals.stream()
                .filter(p -> p.getStatus() == status)
                .count();
    }

    private SellerHomeResult.RecentRequestItem toRecentRequestItem(CurationRequest r) {
        return new SellerHomeResult.RecentRequestItem(
                r.getId(),
                r.getStatus().name(),
                r.getPurposeTags(),
                r.getBudgetTier().name(),
                r.getFulfillmentType().name(),
                r.getFulfillmentDate(),
                r.getExpiresAt()
        );
    }
}
