package com.florent.fake;

import com.florent.domain.auth.KakaoOAuthPort;
import com.florent.domain.auth.KakaoUserInfo;

public class FakeKakaoOAuthPort implements KakaoOAuthPort {

    private KakaoUserInfo response;

    public void setResponse(KakaoUserInfo response) {
        this.response = response;
    }

    @Override
    public KakaoUserInfo getUserInfo(String kakaoAccessToken) {
        if (response != null) {
            return response;
        }
        return new KakaoUserInfo(
                "kakao_" + kakaoAccessToken,
                kakaoAccessToken + "@kakao.com",
                "TestUser");
    }
}
