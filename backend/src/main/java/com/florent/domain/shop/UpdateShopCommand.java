package com.florent.domain.shop;

import java.math.BigDecimal;

public record UpdateShopCommand(
        String name,
        String description,
        String phone,
        String addressText,
        BigDecimal lat,
        BigDecimal lng
) {}
