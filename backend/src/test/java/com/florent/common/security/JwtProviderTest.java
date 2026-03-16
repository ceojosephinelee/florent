package com.florent.common.security;

import com.florent.common.exception.BusinessException;
import com.florent.common.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.florent.support.TestFixtures;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtProviderTest {

    private static final String SECRET_KEY = "test-secret-key-for-jwt-minimum-256-bits-long-padding";
    private static final long ACCESS_TOKEN_VALIDITY_MS = 3600000L;
    private static final long REFRESH_TOKEN_VALIDITY_MS = 2592000000L;

    private JwtProvider sut;
    private final Clock fixedClock = TestFixtures.FIXED_CLOCK;

    @BeforeEach
    void setUp() {
        JwtProperties properties = new JwtProperties(SECRET_KEY, ACCESS_TOKEN_VALIDITY_MS, REFRESH_TOKEN_VALIDITY_MS);
        sut = new JwtProvider(properties, fixedClock);
    }

    @Test
    @DisplayName("액세스 토큰 생성 및 검증 성공")
    void 액세스_토큰_생성_및_검증_성공() {
        // given
        String token = sut.generateAccessToken(1L, "BUYER", 10L, null);

        // when
        Claims claims = sut.validateAndExtractClaims(token);

        // then
        assertThat(claims.getSubject()).isEqualTo("1");
        assertThat(claims.get("role", String.class)).isEqualTo("BUYER");
        assertThat(claims.get("buyerId", Long.class)).isEqualTo(10L);
        assertThat(claims.get("sellerId")).isNull();
    }

    @Test
    @DisplayName("만료된 토큰 검증 시 TOKEN_EXPIRED 예외")
    void 만료된_토큰_검증_시_TOKEN_EXPIRED_예외() {
        // given — 과거 시점에서 토큰 생성
        Clock pastClock = Clock.fixed(
                Instant.parse("2026-03-10T10:00:00Z"), ZoneId.of("Asia/Seoul"));
        JwtProperties properties = new JwtProperties(SECRET_KEY, ACCESS_TOKEN_VALIDITY_MS, REFRESH_TOKEN_VALIDITY_MS);
        JwtProvider pastProvider = new JwtProvider(properties, pastClock);
        String token = pastProvider.generateAccessToken(1L, "BUYER", 10L, null);

        // when & then — 현재 시점에서 검증 (토큰은 이미 만료)
        assertThatThrownBy(() -> sut.validateAndExtractClaims(token))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.TOKEN_EXPIRED);
    }

    @Test
    @DisplayName("잘못된 서명 토큰 검증 시 UNAUTHORIZED 예외")
    void 잘못된_서명_토큰_검증_시_UNAUTHORIZED_예외() {
        // given — 다른 키로 생성된 토큰
        JwtProperties otherProps = new JwtProperties(
                "other-secret-key-for-jwt-minimum-256-bits-long-padding-x",
                ACCESS_TOKEN_VALIDITY_MS, REFRESH_TOKEN_VALIDITY_MS);
        JwtProvider otherProvider = new JwtProvider(otherProps, fixedClock);
        String token = otherProvider.generateAccessToken(1L, "BUYER", 10L, null);

        // when & then
        assertThatThrownBy(() -> sut.validateAndExtractClaims(token))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.UNAUTHORIZED);
    }

    @Test
    @DisplayName("리프레시 토큰 생성 및 검증 성공 — subject만 포함")
    void 리프레시_토큰_생성_및_검증_성공() {
        // given
        String token = sut.generateRefreshToken(1L);

        // when
        Claims claims = sut.validateAndExtractClaims(token);

        // then
        assertThat(claims.getSubject()).isEqualTo("1");
        assertThat(claims.get("role")).isNull();
        assertThat(claims.get("buyerId")).isNull();
        assertThat(claims.get("sellerId")).isNull();
    }

    @Test
    @DisplayName("토큰에서 클레임 추출 성공")
    void 토큰에서_클레임_추출_성공() {
        // given
        String token = sut.generateAccessToken(5L, "SELLER", null, 20L);

        // when
        Claims claims = sut.validateAndExtractClaims(token);

        // then
        assertThat(claims.getSubject()).isEqualTo("5");
        assertThat(claims.get("role", String.class)).isEqualTo("SELLER");
        assertThat(claims.get("buyerId")).isNull();
        assertThat(claims.get("sellerId", Long.class)).isEqualTo(20L);
    }
}
