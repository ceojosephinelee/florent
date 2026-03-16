package com.florent.adapter.in.auth.dto;

import com.florent.domain.auth.KakaoLoginResult;

public record KakaoLoginResponse(
        String accessToken,
        String refreshToken,
        String role,
        boolean isNewUser
) {
    public static KakaoLoginResponse from(KakaoLoginResult result) {
        return new KakaoLoginResponse(
                result.accessToken(), result.refreshToken(),
                result.role(), result.isNewUser());
    }
}
