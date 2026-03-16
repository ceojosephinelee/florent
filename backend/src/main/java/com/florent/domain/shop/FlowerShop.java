package com.florent.domain.shop;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.Objects;

@Getter
public class FlowerShop {
    private Long id;
    private Long sellerId;
    private String shopName;
    private String shopDescription;
    private String shopPhone;
    private String shopAddress;
    private BigDecimal shopLat;
    private BigDecimal shopLng;

    private FlowerShop() {}

    public static FlowerShop create(Long sellerId, String shopName, String shopDescription,
                                    String shopPhone, String shopAddress,
                                    BigDecimal shopLat, BigDecimal shopLng) {
        Objects.requireNonNull(sellerId, "sellerId must not be null");
        Objects.requireNonNull(shopName, "shopName must not be null");
        Objects.requireNonNull(shopAddress, "shopAddress must not be null");
        Objects.requireNonNull(shopLat, "shopLat must not be null");
        Objects.requireNonNull(shopLng, "shopLng must not be null");

        FlowerShop shop = new FlowerShop();
        shop.sellerId = sellerId;
        shop.shopName = shopName;
        shop.shopDescription = shopDescription;
        shop.shopPhone = shopPhone;
        shop.shopAddress = shopAddress;
        shop.shopLat = shopLat;
        shop.shopLng = shopLng;
        return shop;
    }

    public static FlowerShop reconstitute(
            Long id, Long sellerId, String shopName, String shopDescription,
            String shopPhone, String shopAddress,
            BigDecimal shopLat, BigDecimal shopLng) {
        FlowerShop shop = new FlowerShop();
        shop.id = id;
        shop.sellerId = sellerId;
        shop.shopName = shopName;
        shop.shopDescription = shopDescription;
        shop.shopPhone = shopPhone;
        shop.shopAddress = shopAddress;
        shop.shopLat = shopLat;
        shop.shopLng = shopLng;
        return shop;
    }

    public void update(String name, String description, String phone,
                       String addressText, BigDecimal lat, BigDecimal lng) {
        if (name != null) this.shopName = name;
        if (description != null) this.shopDescription = description;
        if (phone != null) this.shopPhone = phone;
        if (addressText != null) this.shopAddress = addressText;
        if (lat != null) this.shopLat = lat;
        if (lng != null) this.shopLng = lng;
    }
}
