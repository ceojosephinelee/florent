package com.florent.adapter.in.seller.dto;

import com.florent.domain.shop.UpdateShopCommand;

import java.math.BigDecimal;

public record UpdateShopRequest(
        String name,
        String description,
        String phone,
        String addressText,
        BigDecimal lat,
        BigDecimal lng
) {
    public UpdateShopCommand toCommand() {
        return new UpdateShopCommand(name, description, phone, addressText, lat, lng);
    }
}
