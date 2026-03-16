package com.florent.domain.user;

import com.florent.common.exception.BusinessException;
import com.florent.common.exception.ErrorCode;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class User {
    private Long id;
    private String kakaoId;
    private String email;
    private UserRole role;
    private String refreshToken;
    private LocalDateTime refreshTokenExpiresAt;
    private LocalDateTime createdAt;

    private User() {}

    public static User createFromKakao(String kakaoId, String email) {
        User user = new User();
        user.kakaoId = kakaoId;
        user.email = email;
        user.role = null;
        user.createdAt = LocalDateTime.now();
        return user;
    }

    public static User reconstitute(Long id, String kakaoId, String email, UserRole role,
                                    String refreshToken, LocalDateTime refreshTokenExpiresAt,
                                    LocalDateTime createdAt) {
        User user = new User();
        user.id = id;
        user.kakaoId = kakaoId;
        user.email = email;
        user.role = role;
        user.refreshToken = refreshToken;
        user.refreshTokenExpiresAt = refreshTokenExpiresAt;
        user.createdAt = createdAt;
        return user;
    }

    public void assignRole(UserRole role) {
        if (this.role != null) {
            throw new BusinessException(ErrorCode.ROLE_ALREADY_SET);
        }
        if (role == null) {
            throw new BusinessException(ErrorCode.INVALID_ROLE);
        }
        this.role = role;
    }

    public void updateRefreshToken(String token, LocalDateTime expiresAt) {
        this.refreshToken = token;
        this.refreshTokenExpiresAt = expiresAt;
    }

    public void clearRefreshToken() {
        this.refreshToken = null;
        this.refreshTokenExpiresAt = null;
    }

    public boolean isRefreshTokenValid(LocalDateTime now) {
        return refreshToken != null
                && refreshTokenExpiresAt != null
                && refreshTokenExpiresAt.isAfter(now);
    }
}
