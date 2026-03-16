package com.florent.adapter.out.persistence.notification;

import com.florent.domain.notification.DevicePlatform;
import com.florent.domain.notification.UserDevice;
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
@Table(name = "user_device")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class UserDeviceJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, length = 20)
    private String platform;

    @Column(nullable = false, unique = true, length = 512)
    private String fcmToken;

    @Column(nullable = false)
    private boolean isActive;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public static UserDeviceJpaEntity from(UserDevice domain) {
        UserDeviceJpaEntity e = new UserDeviceJpaEntity();
        e.id = domain.getId();
        e.userId = domain.getUserId();
        e.platform = domain.getPlatform().name();
        e.fcmToken = domain.getFcmToken();
        e.isActive = domain.isActive();
        e.createdAt = domain.getCreatedAt();
        e.updatedAt = domain.getUpdatedAt();
        return e;
    }

    public UserDevice toDomain() {
        return UserDevice.reconstitute(
                id, userId,
                DevicePlatform.valueOf(platform),
                fcmToken, isActive,
                createdAt, updatedAt);
    }
}