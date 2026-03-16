package com.florent.adapter.in.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record RegisterSellerInfoRequest(
        @NotBlank String shopName,
        @NotBlank String shopAddress,
        @NotNull BigDecimal shopLat,
        @NotNull BigDecimal shopLng,
        String businessNumber
) {}
