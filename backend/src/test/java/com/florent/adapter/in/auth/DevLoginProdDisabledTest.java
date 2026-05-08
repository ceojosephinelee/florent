package com.florent.adapter.in.auth;

import com.florent.common.security.JwtProvider;
import com.florent.domain.auth.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired; //nocheck: @WebMvcTest standard
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * prod 환경 시뮬레이션 — DevAuthController 미로드 시 dev-login 404 검증.
 *
 * <p>AuthController만 로드하여 DevAuthController 부재 상태를 재현한다.
 * prod에서는 @Profile("local") 때문에 DevAuthController 빈이 등록되지 않으므로,
 * /api/v1/auth/dev-login 요청은 핸들러가 없어 404를 반환한다.
 *
 * <p>local 프로파일 정상 동작 테스트: {@link DevAuthControllerTest} 참조
 * <p>프로파일 어노테이션 검증: {@link DevLoginProfileTest} 참조
 */
@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("local")
@TestPropertySource(properties = {
        "spring.web.resources.add-mappings=false"
})
class DevLoginProdDisabledTest {

    @Autowired // @WebMvcTest 표준 패턴
    private MockMvc mockMvc;

    @MockBean private KakaoLoginUseCase kakaoLoginUseCase;
    @MockBean private EmailSignupUseCase emailSignupUseCase;
    @MockBean private EmailLoginUseCase emailLoginUseCase;
    @MockBean private SetRoleUseCase setRoleUseCase;
    @MockBean private ReissueTokenUseCase reissueTokenUseCase;
    @MockBean private LogoutUseCase logoutUseCase;
    @MockBean private WithdrawUseCase withdrawUseCase;
    @MockBean private RegisterSellerInfoUseCase registerSellerInfoUseCase;
    @MockBean private JwtProvider jwtProvider;

    @Test
    @DisplayName("DevAuthController 미로드 시 dev-login 요청은 404 반환")
    void prod_환경_dev_login_404() throws Exception {
        // given
        String body = """
                { "role": "BUYER" }
                """;

        // when & then
        mockMvc.perform(post("/api/v1/auth/dev-login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound());
    }
}
