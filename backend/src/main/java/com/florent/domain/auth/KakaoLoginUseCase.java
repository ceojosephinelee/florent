package com.florent.domain.auth;

public interface KakaoLoginUseCase {
    KakaoLoginResult login(KakaoLoginCommand command);
}
