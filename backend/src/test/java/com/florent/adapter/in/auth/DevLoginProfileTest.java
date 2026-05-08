package com.florent.adapter.in.auth;

import com.florent.application.auth.DevAuthService;
import com.florent.common.config.DevAuthFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Profile;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * dev-login 관련 컴포넌트의 프로파일 제한 검증.
 *
 * <p>local 프로파일 HTTP 테스트: {@link DevAuthControllerTest} 참조
 * (이미 @ActiveProfiles("local") + dev-login 200 검증 완료)
 *
 * <p>prod 환경 HTTP 404 테스트: {@link DevLoginProdDisabledTest} 참조
 */
class DevLoginProfileTest {

    @Test
    @DisplayName("DevAuthController는 @Profile(\"local\")만 허용")
    void DevAuthController_프로파일_검증() {
        Profile profile = DevAuthController.class.getAnnotation(Profile.class);
        assertThat(profile).isNotNull();
        assertThat(profile.value()).containsExactly("local");
    }

    @Test
    @DisplayName("DevAuthService는 @Profile(\"local\")만 허용")
    void DevAuthService_프로파일_검증() {
        Profile profile = DevAuthService.class.getAnnotation(Profile.class);
        assertThat(profile).isNotNull();
        assertThat(profile.value()).containsExactly("local");
    }

    @Test
    @DisplayName("DevAuthFilter는 @Profile(\"local\")만 허용")
    void DevAuthFilter_프로파일_검증() {
        Profile profile = DevAuthFilter.class.getAnnotation(Profile.class);
        assertThat(profile).isNotNull();
        assertThat(profile.value()).containsExactly("local");
    }
}
