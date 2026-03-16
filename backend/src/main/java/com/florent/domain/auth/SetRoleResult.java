package com.florent.domain.auth;

public record SetRoleResult(
        String role,
        String accessToken,
        String refreshToken
) {}
