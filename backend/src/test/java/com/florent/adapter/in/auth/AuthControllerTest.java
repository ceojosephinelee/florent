package com.florent.adapter.in.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.florent.common.exception.BusinessException;
import com.florent.common.exception.ErrorCode;
import com.florent.common.security.JwtProvider;
import com.florent.domain.auth.KakaoLoginResult;
import com.florent.domain.auth.KakaoLoginUseCase;
import com.florent.domain.auth.LogoutUseCase;
import com.florent.domain.auth.ReissueTokenResult;
import com.florent.domain.auth.ReissueTokenUseCase;
import com.florent.domain.auth.RegisterSellerInfoResult;
import com.florent.domain.auth.RegisterSellerInfoUseCase;
import com.florent.domain.auth.SetRoleResult;
import com.florent.domain.auth.SetRoleUseCase;
import com.florent.support.WithMockAuthUser;
import com.florent.support.WithMockSeller;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private KakaoLoginUseCase kakaoLoginUseCase;

    @MockBean
    private SetRoleUseCase setRoleUseCase;

    @MockBean
    private ReissueTokenUseCase reissueTokenUseCase;

    @MockBean
    private LogoutUseCase logoutUseCase;

    @MockBean
    private RegisterSellerInfoUseCase registerSellerInfoUseCase;

    @MockBean
    private JwtProvider jwtProvider;

    // === 카카오 로그인 ===

    @Test
    @DisplayName("카카오 로그인 성공 시 200 응답")
    void 카카오_로그인_성공() throws Exception {
        // given
        given(kakaoLoginUseCase.login(any()))
                .willReturn(new KakaoLoginResult("access-token", "refresh-token", null, true));

        String body = """
                { "kakaoAccessToken": "kakao-access-token" }
                """;

        // when & then
        mockMvc.perform(post("/api/v1/auth/kakao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").value("access-token"))
                .andExpect(jsonPath("$.data.refreshToken").value("refresh-token"))
                .andExpect(jsonPath("$.data.isNewUser").value(true));
    }

    @Test
    @DisplayName("카카오 로그인 시 필수 필드 누락 400")
    void 카카오_로그인_필수_필드_누락() throws Exception {
        // given
        String body = "{}";

        // when & then
        mockMvc.perform(post("/api/v1/auth/kakao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    // === 역할 설정 ===

    @Test
    @DisplayName("역할 설정 성공 시 200 응답")
    @WithMockAuthUser(userId = 1L)
    void 역할_설정_성공() throws Exception {
        // given
        given(setRoleUseCase.setRole(eq(1L), any()))
                .willReturn(new SetRoleResult("BUYER", "new-access", "new-refresh"));

        String body = """
                { "role": "BUYER" }
                """;

        // when & then
        mockMvc.perform(post("/api/v1/auth/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.role").value("BUYER"))
                .andExpect(jsonPath("$.data.accessToken").value("new-access"));
    }

    @Test
    @DisplayName("역할 중복 설정 시 422 응답")
    @WithMockAuthUser(userId = 1L)
    void 역할_중복_설정_422() throws Exception {
        // given
        given(setRoleUseCase.setRole(eq(1L), any()))
                .willThrow(new BusinessException(ErrorCode.ROLE_ALREADY_SET));

        String body = """
                { "role": "SELLER" }
                """;

        // when & then
        mockMvc.perform(post("/api/v1/auth/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("ROLE_ALREADY_SET"));
    }

    // === 토큰 재발급 ===

    @Test
    @DisplayName("토큰 재발급 성공 시 200 응답")
    void 토큰_재발급_성공() throws Exception {
        // given
        given(reissueTokenUseCase.reissue(any()))
                .willReturn(new ReissueTokenResult("new-access", "new-refresh"));

        String body = """
                { "refreshToken": "old-refresh-token" }
                """;

        // when & then
        mockMvc.perform(post("/api/v1/auth/reissue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").value("new-access"))
                .andExpect(jsonPath("$.data.refreshToken").value("new-refresh"));
    }

    @Test
    @DisplayName("만료된 리프레시 토큰 재발급 시 401 응답")
    void 토큰_재발급_만료_401() throws Exception {
        // given
        given(reissueTokenUseCase.reissue(any()))
                .willThrow(new BusinessException(ErrorCode.REFRESH_TOKEN_EXPIRED));

        String body = """
                { "refreshToken": "expired-token" }
                """;

        // when & then
        mockMvc.perform(post("/api/v1/auth/reissue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("REFRESH_TOKEN_EXPIRED"));
    }

    // === 로그아웃 ===

    @Test
    @DisplayName("로그아웃 성공 시 200 응답")
    @WithMockAuthUser(userId = 1L)
    void 로그아웃_성공() throws Exception {
        // given
        willDoNothing().given(logoutUseCase).logout(1L);

        // when & then
        mockMvc.perform(post("/api/v1/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    // === 판매자 정보 등록 ===

    @Test
    @DisplayName("판매자 정보 등록 성공 시 200 응답")
    @WithMockAuthUser(userId = 1L, sellerId = 1L, role = "SELLER")
    void 판매자_정보_등록_성공() throws Exception {
        // given
        given(registerSellerInfoUseCase.register(eq(1L), any()))
                .willReturn(new RegisterSellerInfoResult(1L, "꽃다발가게"));

        String body = """
                {
                    "shopName": "꽃다발가게",
                    "shopAddress": "서울시 강남구",
                    "shopLat": 37.498095,
                    "shopLng": 127.027610
                }
                """;

        // when & then
        mockMvc.perform(post("/api/v1/auth/seller-info")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.sellerId").value(1))
                .andExpect(jsonPath("$.data.shopName").value("꽃다발가게"));
    }

    @Test
    @DisplayName("판매자 정보 중복 등록 시 409 응답")
    @WithMockAuthUser(userId = 1L, sellerId = 1L, role = "SELLER")
    void 판매자_정보_중복_등록_409() throws Exception {
        // given
        given(registerSellerInfoUseCase.register(eq(1L), any()))
                .willThrow(new BusinessException(ErrorCode.SELLER_ALREADY_REGISTERED));

        String body = """
                {
                    "shopName": "꽃다발가게",
                    "shopAddress": "서울시 강남구",
                    "shopLat": 37.498095,
                    "shopLng": 127.027610
                }
                """;

        // when & then
        mockMvc.perform(post("/api/v1/auth/seller-info")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("SELLER_ALREADY_REGISTERED"));
    }

    @Test
    @DisplayName("판매자 정보 등록 시 필수 필드 누락 400")
    @WithMockAuthUser(userId = 1L, sellerId = 1L, role = "SELLER")
    void 판매자_정보_필수_필드_누락_400() throws Exception {
        // given
        String body = """
                { "shopName": "꽃다발가게" }
                """;

        // when & then
        mockMvc.perform(post("/api/v1/auth/seller-info")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }
}
