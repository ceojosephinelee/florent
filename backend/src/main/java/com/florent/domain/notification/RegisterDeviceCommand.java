package com.florent.domain.notification;

public record RegisterDeviceCommand(
        Long userId,
        String fcmToken,
        DevicePlatform platform
) {}