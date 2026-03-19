package com.florent.domain.user;

import com.florent.common.exception.BusinessException;
import com.florent.common.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserTest {

    @Test
    @DisplayName("createFromKakao — 카카오 정보로 신규 유저가 생성된다")
    void createFromKakao_신규_유저_생성() {
        // given
        String kakaoId = "kakao123";
        String email = "test@kakao.com";

        // when
        User user = User.createFromKakao(kakaoId, email, "테스트유저");

        // then
        assertThat(user.getKakaoId()).isEqualTo(kakaoId);
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getRole()).isNull();
        assertThat(user.getRefreshToken()).isNull();
    }

    @Test
    @DisplayName("assignRole — 역할이 null인 유저에게 역할이 설정된다")
    void assignRole_성공() {
        // given
        User user = User.createFromKakao("kakao123", "test@kakao.com", "테스트유저");

        // when
        user.assignRole(UserRole.BUYER);

        // then
        assertThat(user.getRole()).isEqualTo(UserRole.BUYER);
    }

    @Test
    @DisplayName("assignRole — 이미 역할이 설정된 유저는 ROLE_ALREADY_SET 예외가 발생한다")
    void assignRole_중복_설정_예외() {
        // given
        User user = User.createFromKakao("kakao123", "test@kakao.com", "테스트유저");
        user.assignRole(UserRole.BUYER);

        // when & then
        assertThatThrownBy(() -> user.assignRole(UserRole.SELLER))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getErrorCode())
                        .isEqualTo(ErrorCode.ROLE_ALREADY_SET));
    }

    @Test
    @DisplayName("assignRole — null 역할 입력 시 INVALID_ROLE 예외가 발생한다")
    void assignRole_null_역할_예외() {
        // given
        User user = User.createFromKakao("kakao123", "test@kakao.com", "테스트유저");

        // when & then
        assertThatThrownBy(() -> user.assignRole(null))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getErrorCode())
                        .isEqualTo(ErrorCode.INVALID_ROLE));
    }

    @Test
    @DisplayName("updateRefreshToken — 리프레시 토큰과 만료시각이 저장된다")
    void updateRefreshToken_저장() {
        // given
        User user = User.createFromKakao("kakao123", "test@kakao.com", "테스트유저");
        LocalDateTime expiresAt = LocalDateTime.of(2026, 4, 15, 10, 0);

        // when
        user.updateRefreshToken("refresh-token-123", expiresAt);

        // then
        assertThat(user.getRefreshToken()).isEqualTo("refresh-token-123");
        assertThat(user.getRefreshTokenExpiresAt()).isEqualTo(expiresAt);
    }

    @Test
    @DisplayName("clearRefreshToken — 리프레시 토큰이 제거된다")
    void clearRefreshToken_제거() {
        // given
        User user = User.createFromKakao("kakao123", "test@kakao.com", "테스트유저");
        user.updateRefreshToken("refresh-token-123", LocalDateTime.of(2026, 4, 15, 10, 0));

        // when
        user.clearRefreshToken();

        // then
        assertThat(user.getRefreshToken()).isNull();
        assertThat(user.getRefreshTokenExpiresAt()).isNull();
    }

    @Test
    @DisplayName("isRefreshTokenValid — 유효한 토큰이면 true를 반환한다")
    void isRefreshTokenValid_유효() {
        // given
        User user = User.createFromKakao("kakao123", "test@kakao.com", "테스트유저");
        user.updateRefreshToken("token", LocalDateTime.of(2026, 4, 15, 10, 0));

        // when
        boolean valid = user.isRefreshTokenValid(LocalDateTime.of(2026, 3, 15, 10, 0));

        // then
        assertThat(valid).isTrue();
    }

    @Test
    @DisplayName("isRefreshTokenValid — 만료된 토큰이면 false를 반환한다")
    void isRefreshTokenValid_만료() {
        // given
        User user = User.createFromKakao("kakao123", "test@kakao.com", "테스트유저");
        user.updateRefreshToken("token", LocalDateTime.of(2026, 3, 14, 10, 0));

        // when
        boolean valid = user.isRefreshTokenValid(LocalDateTime.of(2026, 3, 15, 10, 0));

        // then
        assertThat(valid).isFalse();
    }

    @Test
    @DisplayName("isRefreshTokenValid — 토큰이 null이면 false를 반환한다")
    void isRefreshTokenValid_토큰_없음() {
        // given
        User user = User.createFromKakao("kakao123", "test@kakao.com", "테스트유저");

        // when
        boolean valid = user.isRefreshTokenValid(LocalDateTime.of(2026, 3, 15, 10, 0));

        // then
        assertThat(valid).isFalse();
    }

    @Test
    @DisplayName("reconstitute — DB 재구성이 정상적으로 동작한다")
    void reconstitute_DB_재구성() {
        // given & when
        User user = User.reconstitute(
                1L, "kakao123", "test@kakao.com", "테스트유저",
                UserRole.BUYER, "refresh-token",
                LocalDateTime.of(2026, 4, 15, 10, 0),
                LocalDateTime.of(2026, 3, 15, 10, 0));

        // then
        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getKakaoId()).isEqualTo("kakao123");
        assertThat(user.getRole()).isEqualTo(UserRole.BUYER);
    }
}
