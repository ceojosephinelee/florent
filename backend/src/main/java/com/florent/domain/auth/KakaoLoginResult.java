package com.florent.domain.auth;

public record KakaoLoginResult(
        String accessToken,
        String refreshToken,
        String role,
        boolean isNewUser
) {}
