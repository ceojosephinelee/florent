package com.florent.adapter.in.seller.dto;

import com.florent.domain.shop.RegisterShopCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record RegisterShopRequest(
        @NotBlank String name,
        String description,
        String phone,
        @NotBlank String addressText,
        @NotNull BigDecimal lat,
        @NotNull BigDecimal lng
) {
    public RegisterShopCommand toCommand() {
        return new RegisterShopCommand(name, description, phone, addressText, lat, lng);
    }
}
