package com.florent.domain.seller;

import java.time.LocalDateTime;

public record SellerProfileResult(
        Long sellerId,
        String shopName,
        String shopAddress,
        String role,
        LocalDateTime createdAt
) {}
