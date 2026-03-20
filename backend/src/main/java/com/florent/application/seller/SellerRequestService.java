package com.florent.application.seller;

import com.florent.common.exception.BusinessException;
import com.florent.common.exception.ErrorCode;
import com.florent.common.util.HaversineUtil;
import com.florent.domain.proposal.Proposal;
import com.florent.domain.proposal.ProposalRepository;
import com.florent.domain.request.CurationRequest;
import com.florent.domain.request.CurationRequestRepository;
import com.florent.domain.request.FulfillmentType;
import com.florent.domain.request.GetSellerRequestDetailUseCase;
import com.florent.domain.request.GetSellerRequestListUseCase;
import com.florent.domain.request.SellerRequestDetailResult;
import com.florent.domain.request.SellerRequestListResult;
import com.florent.domain.request.SellerRequestSummaryResult;
import com.florent.domain.shop.FlowerShop;
import com.florent.domain.shop.FlowerShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SellerRequestService implements GetSellerRequestListUseCase, GetSellerRequestDetailUseCase {

    private static final double PICKUP_RADIUS_KM = 5.0;
    private static final double DELIVERY_RADIUS_KM = 5.0;

    private final CurationRequestRepository requestRepository;
    private final FlowerShopRepository shopRepository;
    private final ProposalRepository proposalRepository;

    @Transactional(readOnly = true)
    @Override
    public SellerRequestListResult getSellerRequests(Long sellerId, int page, int size) {
        FlowerShop shop = findShopBySellerId(sellerId);
        List<CurationRequest> nearbyRequests = filterNearbyRequests(shop);

        int totalElements = nearbyRequests.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        List<CurationRequest> pageContent = paginate(nearbyRequests, page, size);

        List<SellerRequestSummaryResult> summaries = buildSummaries(pageContent, shop.getId());
        boolean last = (page + 1) >= totalPages || totalPages == 0;
        return new SellerRequestListResult(
                summaries, page, size, totalElements, totalPages, last);
    }

    @Transactional(readOnly = true)
    @Override
    public SellerRequestDetailResult getSellerRequestDetail(Long requestId, Long sellerId) {
        FlowerShop shop = findShopBySellerId(sellerId);

        CurationRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REQUEST_NOT_FOUND));

        validateWithinRadius(request, shop);

        var myProposal = proposalRepository
                .findByRequestIdAndFlowerShopId(requestId, shop.getId())
                .orElse(null);

        Long myProposalId = myProposal != null ? myProposal.getId() : null;
        String myProposalStatus = myProposal != null ? myProposal.getStatus().name() : null;

        return SellerRequestDetailResult.from(request, myProposalId, myProposalStatus);
    }

    private List<CurationRequest> filterNearbyRequests(FlowerShop shop) {
        return requestRepository.findAll().stream()
                .filter(r -> HaversineUtil.isWithinRadius(
                        r.getPlaceLat(), r.getPlaceLng(),
                        shop.getShopLat(), shop.getShopLng(),
                        radiusFor(r.getFulfillmentType())))
                .sorted(Comparator.comparing(CurationRequest::getCreatedAt).reversed())
                .toList();
    }

    private List<CurationRequest> paginate(List<CurationRequest> list, int page, int size) {
        int fromIndex = Math.min(page * size, list.size());
        int toIndex = Math.min(fromIndex + size, list.size());
        return list.subList(fromIndex, toIndex);
    }

    private List<SellerRequestSummaryResult> buildSummaries(
            List<CurationRequest> requests, Long shopId) {
        List<Long> requestIds = requests.stream()
                .map(CurationRequest::getId).toList();

        Map<Long, Proposal> myProposalMap = proposalRepository
                .findByRequestIdsAndFlowerShopId(requestIds, shopId).stream()
                .collect(Collectors.toMap(Proposal::getRequestId, Function.identity()));

        return requests.stream()
                .map(request -> {
                    Proposal p = myProposalMap.get(request.getId());
                    String status = p != null ? p.getStatus().name() : null;
                    return SellerRequestSummaryResult.from(request, status);
                })
                .toList();
    }

    private void validateWithinRadius(CurationRequest request, FlowerShop shop) {
        if (!HaversineUtil.isWithinRadius(
                request.getPlaceLat(), request.getPlaceLng(),
                shop.getShopLat(), shop.getShopLng(),
                radiusFor(request.getFulfillmentType()))) {
            throw new BusinessException(ErrorCode.REQUEST_NOT_FOUND);
        }
    }

    private double radiusFor(FulfillmentType type) {
        return type == FulfillmentType.PICKUP ? PICKUP_RADIUS_KM : DELIVERY_RADIUS_KM;
    }

    private FlowerShop findShopBySellerId(Long sellerId) {
        return shopRepository.findBySellerId(sellerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SHOP_NOT_FOUND));
    }
}
