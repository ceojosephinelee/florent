package com.florent.domain.auth;

public record EmailAuthResult(
        String accessToken,
        String refreshToken,
        String role,
        boolean isNewUser,
        boolean hasFlowerShop
) {}
