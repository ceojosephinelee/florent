package com.florent.fake;

import com.florent.domain.notification.NotificationUserResolverPort;

import java.util.HashMap;
import java.util.Map;

public class FakeNotificationUserResolverPort implements NotificationUserResolverPort {

    private final Map<Long, Long> buyerToUser = new HashMap<>();
    private final Map<Long, Long> sellerToUser = new HashMap<>();

    public void addBuyerMapping(Long buyerId, Long userId) {
        buyerToUser.put(buyerId, userId);
    }

    public void addSellerMapping(Long sellerId, Long userId) {
        sellerToUser.put(sellerId, userId);
    }

    @Override
    public Long findUserIdByBuyerId(Long buyerId) {
        return buyerToUser.getOrDefault(buyerId, buyerId);
    }

    @Override
    public Long findUserIdBySellerId(Long sellerId) {
        return sellerToUser.getOrDefault(sellerId, sellerId);
    }
}