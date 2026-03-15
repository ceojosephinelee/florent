package com.florent.domain.notification;

import lombok.Getter;

import java.time.Clock;
import java.time.LocalDateTime;

@Getter
public class UserDevice {
    private Long id;
    private Long userId;
    private DevicePlatform platform;
    private String fcmToken;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private UserDevice() {}

    public static UserDevice register(
            Long userId, DevicePlatform platform,
            String fcmToken, Clock clock) {
        UserDevice d = new UserDevice();
        d.userId = userId;
        d.platform = platform;
        d.fcmToken = fcmToken;
        d.isActive = true;
        d.createdAt = LocalDateTime.now(clock);
        d.updatedAt = LocalDateTime.now(clock);
        return d;
    }

    public void activate(Clock clock) {
        this.isActive = true;
        this.updatedAt = LocalDateTime.now(clock);
    }

    public void updateToken(String fcmToken, DevicePlatform platform, Clock clock) {
        this.fcmToken = fcmToken;
        this.platform = platform;
        this.isActive = true;
        this.updatedAt = LocalDateTime.now(clock);
    }

    public static UserDevice reconstitute(
            Long id, Long userId, DevicePlatform platform,
            String fcmToken, boolean isActive,
            LocalDateTime createdAt, LocalDateTime updatedAt) {
        UserDevice d = new UserDevice();
        d.id = id;
        d.userId = userId;
        d.platform = platform;
        d.fcmToken = fcmToken;
        d.isActive = isActive;
        d.createdAt = createdAt;
        d.updatedAt = updatedAt;
        return d;
    }
}