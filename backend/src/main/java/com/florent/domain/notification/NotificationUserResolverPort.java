package com.florent.domain.notification;

/**
 * buyerId/sellerId → userId 변환을 위한 outbound port.
 * NotificationService가 알림 대상의 USER.id를 조회할 때 사용한다.
 */
public interface NotificationUserResolverPort {
    Long findUserIdByBuyerId(Long buyerId);
    Long findUserIdBySellerId(Long sellerId);
}