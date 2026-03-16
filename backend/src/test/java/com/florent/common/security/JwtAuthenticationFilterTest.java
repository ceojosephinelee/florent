package com.florent.common.security;

import com.florent.support.TestFixtures;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class JwtAuthenticationFilterTest {

    private static final String SECRET_KEY = "test-secret-key-for-jwt-minimum-256-bits-long-padding";
    private static final long ACCESS_TOKEN_VALIDITY_MS = 3600000L;
    private static final long REFRESH_TOKEN_VALIDITY_MS = 2592000000L;

    private final Clock fixedClock = TestFixtures.FIXED_CLOCK;
    private JwtProvider jwtProvider;
    private JwtAuthenticationFilter sut;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        JwtProperties properties = new JwtProperties(SECRET_KEY, ACCESS_TOKEN_VALIDITY_MS, REFRESH_TOKEN_VALIDITY_MS);
        jwtProvider = new JwtProvider(properties, fixedClock);
        sut = new JwtAuthenticationFilter(jwtProvider);
    }

    @Test
    @DisplayName("유효한 토큰 시 SecurityContext에 Authentication 설정")
    void 유효한_토큰_시_SecurityContext에_Authentication_설정() throws ServletException, IOException {
        // given
        String token = jwtProvider.generateAccessToken(1L, "BUYER", 10L, null);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);

        // when
        sut.doFilterInternal(request, response, filterChain);

        // then
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        assertThat(principal.getUserId()).isEqualTo(1L);
        assertThat(principal.getBuyerId()).isEqualTo(10L);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("토큰 없으면 SecurityContext 비어있음")
    void 토큰_없으면_SecurityContext_비어있음() throws ServletException, IOException {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);

        // when
        sut.doFilterInternal(request, response, filterChain);

        // then
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("만료 토큰 시 401 JSON 응답")
    void 만료_토큰_시_401_JSON_응답() throws ServletException, IOException {
        // given — 과거 시점에서 토큰 생성
        Clock pastClock = Clock.fixed(Instant.parse("2026-03-10T10:00:00Z"), ZoneId.of("Asia/Seoul"));
        JwtProperties properties = new JwtProperties(SECRET_KEY, ACCESS_TOKEN_VALIDITY_MS, REFRESH_TOKEN_VALIDITY_MS);
        JwtProvider pastProvider = new JwtProvider(properties, pastClock);
        String token = pastProvider.generateAccessToken(1L, "BUYER", 10L, null);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);

        // when
        sut.doFilterInternal(request, response, filterChain);

        // then
        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentAsString()).contains("TOKEN_EXPIRED");
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("잘못된 서명 시 401 JSON 응답")
    void 잘못된_서명_시_401_JSON_응답() throws ServletException, IOException {
        // given — 다른 키로 생성된 토큰
        JwtProperties otherProps = new JwtProperties(
                "other-secret-key-for-jwt-minimum-256-bits-long-padding-x",
                ACCESS_TOKEN_VALIDITY_MS, REFRESH_TOKEN_VALIDITY_MS);
        JwtProvider otherProvider = new JwtProvider(otherProps, fixedClock);
        String token = otherProvider.generateAccessToken(1L, "BUYER", 10L, null);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);

        // when
        sut.doFilterInternal(request, response, filterChain);

        // then
        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentAsString()).contains("UNAUTHORIZED");
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("제외 경로 shouldNotFilter true")
    void 제외_경로_shouldNotFilter_true() {
        // given
        MockHttpServletRequest authRequest = new MockHttpServletRequest();
        authRequest.setRequestURI("/api/v1/auth/kakao");

        MockHttpServletRequest reissueRequest = new MockHttpServletRequest();
        reissueRequest.setRequestURI("/api/v1/auth/reissue");

        MockHttpServletRequest healthRequest = new MockHttpServletRequest();
        healthRequest.setRequestURI("/actuator/health");

        MockHttpServletRequest swaggerRequest = new MockHttpServletRequest();
        swaggerRequest.setRequestURI("/swagger-ui/index.html");

        MockHttpServletRequest apiDocsRequest = new MockHttpServletRequest();
        apiDocsRequest.setRequestURI("/v3/api-docs/swagger-config");

        MockHttpServletRequest normalRequest = new MockHttpServletRequest();
        normalRequest.setRequestURI("/api/v1/buyer/requests");

        // then
        assertThat(sut.shouldNotFilter(authRequest)).isTrue();
        assertThat(sut.shouldNotFilter(reissueRequest)).isTrue();
        assertThat(sut.shouldNotFilter(healthRequest)).isTrue();
        assertThat(sut.shouldNotFilter(swaggerRequest)).isTrue();
        assertThat(sut.shouldNotFilter(apiDocsRequest)).isTrue();
        assertThat(sut.shouldNotFilter(normalRequest)).isFalse();
    }
}
