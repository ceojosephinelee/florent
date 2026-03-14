package com.florent.domain.shop;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class FlowerShop {
    private Long id;
    private Long sellerId;
    private String shopName;
    private String shopPhone;
    private String shopAddress;
    private BigDecimal shopLat;
    private BigDecimal shopLng;

    private FlowerShop() {}

    public static FlowerShop reconstitute(
            Long id, Long sellerId, String shopName, String shopPhone,
            String shopAddress, BigDecimal shopLat, BigDecimal shopLng) {
        FlowerShop shop = new FlowerShop();
        shop.id = id;
        shop.sellerId = sellerId;
        shop.shopName = shopName;
        shop.shopPhone = shopPhone;
        shop.shopAddress = shopAddress;
        shop.shopLat = shopLat;
        shop.shopLng = shopLng;
        return shop;
    }
}
