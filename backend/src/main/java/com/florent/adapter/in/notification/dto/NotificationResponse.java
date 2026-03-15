package com.florent.adapter.in.notification.dto;

import com.florent.domain.notification.Notification;

import java.time.LocalDateTime;

public record NotificationResponse(
        Long notificationId,
        String type,
        String referenceType,
        Long referenceId,
        String title,
        String body,
        boolean isRead,
        LocalDateTime createdAt
) {
    public static NotificationResponse from(Notification n) {
        return new NotificationResponse(
                n.getId(),
                n.getType().name(),
                n.getReferenceType().name(),
                n.getReferenceId(),
                n.getTitle(),
                n.getBody(),
                n.isRead(),
                n.getCreatedAt());
    }
}