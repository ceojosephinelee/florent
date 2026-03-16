package com.florent.domain.buyer;

import java.time.LocalDateTime;

public record BuyerProfileResult(
        Long buyerId,
        String nickName,
        String email,
        String role,
        LocalDateTime createdAt
) {}
