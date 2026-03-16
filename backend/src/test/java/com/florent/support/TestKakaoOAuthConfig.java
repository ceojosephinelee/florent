package com.florent.support;

import com.florent.domain.auth.KakaoOAuthPort;
import com.florent.domain.auth.KakaoUserInfo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestKakaoOAuthConfig {

    @Bean
    public KakaoOAuthPort kakaoOAuthPort() {
        return kakaoAccessToken -> new KakaoUserInfo(
                "test_kakao_" + kakaoAccessToken,
                "test_" + kakaoAccessToken + "@kakao.com",
                "TestUser");
    }
}
