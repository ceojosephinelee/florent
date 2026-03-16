package com.florent.adapter.out.kakao;

import com.florent.domain.auth.KakaoOAuthPort;
import com.florent.domain.auth.KakaoUserInfo;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("local")
public class MockKakaoOAuthAdapter implements KakaoOAuthPort {

    @Override
    public KakaoUserInfo getUserInfo(String kakaoAccessToken) {
        return new KakaoUserInfo(
                "mock_kakao_" + kakaoAccessToken,
                "mock_" + kakaoAccessToken + "@kakao.com",
                "MockUser");
    }
}
