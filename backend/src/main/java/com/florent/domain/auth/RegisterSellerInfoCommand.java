package com.florent.domain.auth;

import java.math.BigDecimal;

public record RegisterSellerInfoCommand(
        String shopName,
        String shopAddress,
        BigDecimal shopLat,
        BigDecimal shopLng,
        String businessNumber
) {}
