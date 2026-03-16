package com.florent.adapter.in.seller.dto;

import com.florent.domain.shop.ShopDetailResult;

import java.math.BigDecimal;

public record ShopDetailResponse(
        Long shopId,
        String name,
        String description,
        String phone,
        String addressText,
        BigDecimal lat,
        BigDecimal lng
) {
    public static ShopDetailResponse from(ShopDetailResult result) {
        return new ShopDetailResponse(
                result.shopId(),
                result.name(),
                result.description(),
                result.phone(),
                result.addressText(),
                result.lat(),
                result.lng()
        );
    }
}
