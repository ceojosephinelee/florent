package com.florent.adapter.out.kakao;

import com.florent.common.exception.BusinessException;
import com.florent.common.exception.ErrorCode;
import com.florent.domain.auth.KakaoOAuthPort;
import com.florent.domain.auth.KakaoUserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
@Profile("!local & !test")
@RequiredArgsConstructor
public class KakaoOAuthAdapter implements KakaoOAuthPort {

    private static final String KAKAO_USER_INFO_URL = "https://kapi.kakao.com/v2/user/me";

    private final RestTemplate restTemplate;

    @Override
    @SuppressWarnings("unchecked")
    public KakaoUserInfo getUserInfo(String kakaoAccessToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(kakaoAccessToken);

            ResponseEntity<Map> response = restTemplate.exchange(
                    KAKAO_USER_INFO_URL, HttpMethod.GET,
                    new HttpEntity<>(headers), Map.class);

            Map<String, Object> body = response.getBody();
            if (body == null) {
                throw new BusinessException(ErrorCode.KAKAO_AUTH_FAILED);
            }

            String kakaoId = String.valueOf(body.get("id"));
            Map<String, Object> kakaoAccount = (Map<String, Object>) body.get("kakao_account");
            String email = kakaoAccount != null ? (String) kakaoAccount.get("email") : null;

            Map<String, Object> properties = (Map<String, Object>) body.get("properties");
            String nickname = properties != null ? (String) properties.get("nickname") : null;

            return new KakaoUserInfo(kakaoId, email, nickname);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.KAKAO_AUTH_FAILED);
        }
    }
}
