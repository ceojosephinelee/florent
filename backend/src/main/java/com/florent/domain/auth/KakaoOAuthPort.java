package com.florent.domain.auth;

public interface KakaoOAuthPort {
    KakaoUserInfo getUserInfo(String kakaoAccessToken);
}
