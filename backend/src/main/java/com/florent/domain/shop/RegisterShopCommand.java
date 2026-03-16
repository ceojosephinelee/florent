package com.florent.domain.shop;

import java.math.BigDecimal;

public record RegisterShopCommand(
        String name,
        String description,
        String phone,
        String addressText,
        BigDecimal lat,
        BigDecimal lng
) {}
