package com.florent.domain.auth;

public record ReissueTokenResult(
        String accessToken,
        String refreshToken
) {}
