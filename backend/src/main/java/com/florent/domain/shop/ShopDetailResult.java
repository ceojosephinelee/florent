package com.florent.domain.shop;

import java.math.BigDecimal;

public record ShopDetailResult(
        Long shopId,
        String name,
        String description,
        String phone,
        String addressText,
        BigDecimal lat,
        BigDecimal lng
) {
    public static ShopDetailResult from(FlowerShop shop) {
        return new ShopDetailResult(
                shop.getId(),
                shop.getShopName(),
                shop.getShopDescription(),
                shop.getShopPhone(),
                shop.getShopAddress(),
                shop.getShopLat(),
                shop.getShopLng()
        );
    }
}
