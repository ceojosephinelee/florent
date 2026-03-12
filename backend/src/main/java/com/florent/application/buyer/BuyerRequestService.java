package com.florent.application.buyer;

import com.florent.common.util.HaversineUtil;
import com.florent.domain.notification.SaveNotificationUseCase;
import com.florent.domain.request.CreateRequestCommand;
import com.florent.domain.request.CreateRequestResult;
import com.florent.domain.request.CreateRequestUseCase;
import com.florent.domain.request.CurationRequest;
import com.florent.domain.request.CurationRequestRepository;
import com.florent.domain.shop.FlowerShop;
import com.florent.domain.shop.FlowerShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BuyerRequestService implements CreateRequestUseCase {

    private static final double NOTIFICATION_RADIUS_KM = 2.0;

    private final CurationRequestRepository requestRepository;
    private final FlowerShopRepository shopRepository;
    private final SaveNotificationUseCase saveNotificationUseCase;

    @Transactional
    @Override
    public CreateRequestResult create(CreateRequestCommand command) {
        CurationRequest request = CurationRequest.create(command);
        CurationRequest saved = requestRepository.save(request);
        notifyNearbyShops(command.placeLat(), command.placeLng(), saved.getId());
        return CreateRequestResult.from(saved);
    }

    private void notifyNearbyShops(BigDecimal lat, BigDecimal lng, Long requestId) {
        List<FlowerShop> nearbyShops = shopRepository.findAll().stream()
                .filter(shop -> HaversineUtil.isWithinRadius(
                        lat, lng, shop.getShopLat(), shop.getShopLng(),
                        NOTIFICATION_RADIUS_KM))
                .toList();

        nearbyShops.forEach(shop ->
                saveNotificationUseCase.saveRequestArrived(shop.getSellerId(), requestId));
    }
}
