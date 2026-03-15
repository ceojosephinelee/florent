package com.florent.adapter.out.persistence.notification;

import com.florent.adapter.out.persistence.buyer.BuyerJpaEntity;
import com.florent.adapter.out.persistence.buyer.BuyerJpaRepository;
import com.florent.adapter.out.persistence.seller.SellerJpaEntity;
import com.florent.adapter.out.persistence.seller.SellerJpaRepository;
import com.florent.common.exception.BusinessException;
import com.florent.common.exception.ErrorCode;
import com.florent.domain.notification.NotificationUserResolverPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationUserResolverAdapter implements NotificationUserResolverPort {

    private final BuyerJpaRepository buyerJpaRepository;
    private final SellerJpaRepository sellerJpaRepository;

    @Override
    public Long findUserIdByBuyerId(Long buyerId) {
        return buyerJpaRepository.findById(buyerId)
                .map(BuyerJpaEntity::getUserId)
                .orElseThrow(() -> new BusinessException(ErrorCode.FORBIDDEN));
    }

    @Override
    public Long findUserIdBySellerId(Long sellerId) {
        return sellerJpaRepository.findById(sellerId)
                .map(SellerJpaEntity::getUserId)
                .orElseThrow(() -> new BusinessException(ErrorCode.FORBIDDEN));
    }
}