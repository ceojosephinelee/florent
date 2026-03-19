package com.florent.adapter.out.persistence.user;

import com.florent.domain.user.User;
import com.florent.domain.user.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "\"user\"")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class UserJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String kakaoId;

    private String email;

    private String nickname;

    private String role;

    @Column(length = 512)
    private String refreshToken;

    private LocalDateTime refreshTokenExpiresAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void prePersist() {
        this.createdAt = this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public static UserJpaEntity from(User domain) {
        UserJpaEntity entity = new UserJpaEntity();
        entity.id = domain.getId();
        entity.kakaoId = domain.getKakaoId();
        entity.email = domain.getEmail();
        entity.nickname = domain.getNickname();
        entity.role = domain.getRole() != null ? domain.getRole().name() : null;
        entity.refreshToken = domain.getRefreshToken();
        entity.refreshTokenExpiresAt = domain.getRefreshTokenExpiresAt();
        entity.createdAt = domain.getCreatedAt();
        return entity;
    }

    public User toDomain() {
        return User.reconstitute(
                id, kakaoId, email, nickname,
                role != null ? UserRole.valueOf(role) : null,
                refreshToken, refreshTokenExpiresAt, createdAt);
    }
}
