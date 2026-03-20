package com.florent.adapter.in.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.florent.common.exception.BusinessException;
import com.florent.common.exception.ErrorCode;
import com.florent.common.security.JwtProvider;
import com.florent.domain.auth.DevLoginUseCase;
import com.florent.domain.auth.KakaoLoginResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = DevAuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("local")
class DevAuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DevLoginUseCase devLoginUseCase;

    @MockBean
    private JwtProvider jwtProvider;

    @Test
    @DisplayName("구매자 개발 로그인 성공 시 200 응답")
    void 구매자_개발_로그인_성공() throws Exception {
        // given
        given(devLoginUseCase.devLogin(eq("BUYER")))
                .willReturn(new KakaoLoginResult("dev-access", "dev-refresh", "BUYER", false, false));

        String body = """
                { "role": "BUYER" }
                """;

        // when & then
        mockMvc.perform(post("/api/v1/auth/dev-login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").value("dev-access"))
                .andExpect(jsonPath("$.data.refreshToken").value("dev-refresh"))
                .andExpect(jsonPath("$.data.role").value("BUYER"))
                .andExpect(jsonPath("$.data.isNewUser").value(false));
    }

    @Test
    @DisplayName("판매자 개발 로그인 성공 시 200 응답")
    void 판매자_개발_로그인_성공() throws Exception {
        // given
        given(devLoginUseCase.devLogin(eq("SELLER")))
                .willReturn(new KakaoLoginResult("dev-access", "dev-refresh", "SELLER", false, true));

        String body = """
                { "role": "SELLER" }
                """;

        // when & then
        mockMvc.perform(post("/api/v1/auth/dev-login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.role").value("SELLER"))
                .andExpect(jsonPath("$.data.hasFlowerShop").value(true));
    }

    @Test
    @DisplayName("잘못된 role 전달 시 400 응답")
    void 잘못된_역할_400() throws Exception {
        // given
        String body = """
                { "role": "ADMIN" }
                """;

        // when & then
        mockMvc.perform(post("/api/v1/auth/dev-login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("해당 역할의 유저가 없으면 404 응답")
    void 유저_없음_404() throws Exception {
        // given
        given(devLoginUseCase.devLogin(eq("BUYER")))
                .willThrow(new BusinessException(ErrorCode.USER_NOT_FOUND));

        String body = """
                { "role": "BUYER" }
                """;

        // when & then
        mockMvc.perform(post("/api/v1/auth/dev-login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("USER_NOT_FOUND"));
    }
}
