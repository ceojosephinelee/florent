package com.florent.application.buyer;

import com.florent.common.exception.BusinessException;
import com.florent.common.exception.ErrorCode;
import com.florent.common.util.HaversineUtil;
import com.florent.domain.notification.SaveNotificationUseCase;
import com.florent.domain.proposal.ProposalCountPort;
import com.florent.domain.proposal.ProposalStatus;
import com.florent.domain.request.CreateRequestCommand;
import com.florent.domain.request.CreateRequestResult;
import com.florent.domain.request.CreateRequestUseCase;
import com.florent.domain.request.CurationRequest;
import com.florent.domain.request.CurationRequestRepository;
import com.florent.domain.request.GetRequestDetailUseCase;
import com.florent.domain.request.GetRequestListUseCase;
import com.florent.domain.request.RequestDetailResult;
import com.florent.domain.request.RequestListResult;
import com.florent.domain.request.RequestPage;
import com.florent.domain.request.RequestSummaryResult;
import com.florent.domain.shop.FlowerShop;
import com.florent.domain.shop.FlowerShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BuyerRequestService implements CreateRequestUseCase,
        GetRequestListUseCase, GetRequestDetailUseCase {

    private static final double NOTIFICATION_RADIUS_KM = 2.0;

    private final CurationRequestRepository requestRepository;
    private final FlowerShopRepository shopRepository;
    private final SaveNotificationUseCase saveNotificationPort;
    private final ProposalCountPort proposalCountPort;
    private final Clock clock;

    @Transactional
    @Override
    public CreateRequestResult create(CreateRequestCommand command) {
        CurationRequest request = CurationRequest.create(command, clock);
        CurationRequest saved = requestRepository.save(request);
        notifyNearbyShops(command.placeLat(), command.placeLng(), saved.getId());
        return CreateRequestResult.from(saved);
    }

    @Transactional(readOnly = true)
    @Override
    public RequestListResult getList(Long buyerId, int page, int size) {
        RequestPage requestPage = requestRepository.findByBuyerId(buyerId, page, size);

        List<Long> requestIds = requestPage.content().stream()
                .map(CurationRequest::getId)
                .toList();

        Map<Long, Map<ProposalStatus, Integer>> countMap =
                proposalCountPort.countByRequestIdsGroupByStatus(requestIds);

        List<RequestSummaryResult> summaries = requestPage.content().stream()
                .map(request -> {
                    Map<ProposalStatus, Integer> statusCounts =
                            countMap.getOrDefault(request.getId(), Map.of());
                    return RequestSummaryResult.from(
                            request,
                            statusCounts.getOrDefault(ProposalStatus.DRAFT, 0),
                            statusCounts.getOrDefault(ProposalStatus.SUBMITTED, 0));
                })
                .toList();

        return new RequestListResult(
                summaries, page, size,
                requestPage.totalElements(),
                requestPage.totalPages(),
                requestPage.last());
    }

    @Transactional(readOnly = true)
    @Override
    public RequestDetailResult getDetail(Long requestId, Long buyerId) {
        CurationRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REQUEST_NOT_FOUND));

        if (!request.getBuyerId().equals(buyerId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        Map<Long, Map<ProposalStatus, Integer>> countMap =
                proposalCountPort.countByRequestIdsGroupByStatus(Collections.singletonList(requestId));
        Map<ProposalStatus, Integer> statusCounts =
                countMap.getOrDefault(requestId, Map.of());

        int draftCount = statusCounts.getOrDefault(ProposalStatus.DRAFT, 0);
        int submittedCount = statusCounts.getOrDefault(ProposalStatus.SUBMITTED, 0);

        return RequestDetailResult.from(request, draftCount, submittedCount);
    }

    private void notifyNearbyShops(BigDecimal lat, BigDecimal lng, Long requestId) {
        List<FlowerShop> nearbyShops = shopRepository.findAll().stream()
                .filter(shop -> HaversineUtil.isWithinRadius(
                        lat, lng, shop.getShopLat(), shop.getShopLng(),
                        NOTIFICATION_RADIUS_KM))
                .toList();

        nearbyShops.forEach(shop ->
                saveNotificationPort.saveRequestArrived(shop.getSellerId(), requestId));
    }
}
