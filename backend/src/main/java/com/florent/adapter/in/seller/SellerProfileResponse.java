package com.florent.adapter.in.seller;

import com.florent.domain.seller.SellerProfileResult;

import java.time.LocalDateTime;

public record SellerProfileResponse(
        Long sellerId,
        String shopName,
        String shopAddress,
        String role,
        LocalDateTime createdAt
) {
    public static SellerProfileResponse from(SellerProfileResult result) {
        return new SellerProfileResponse(
                result.sellerId(),
                result.shopName(),
                result.shopAddress(),
                result.role(),
                result.createdAt()
        );
    }
}
