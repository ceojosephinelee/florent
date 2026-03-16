package com.florent.adapter.in.seller.dto;

import com.florent.domain.shop.RegisterShopResult;

public record RegisterShopResponse(Long shopId, String name) {
    public static RegisterShopResponse from(RegisterShopResult result) {
        return new RegisterShopResponse(result.shopId(), result.name());
    }
}
