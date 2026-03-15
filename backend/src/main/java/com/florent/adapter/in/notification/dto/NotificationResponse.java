package com.florent.adapter.in.notification.dto;

import com.florent.domain.notification.NotificationPageResult.NotificationItem;

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
    public static NotificationResponse from(NotificationItem item) {
        return new NotificationResponse(
                item.notificationId(),
                item.type(),
                item.referenceType(),
                item.referenceId(),
                item.title(),
                item.body(),
                item.isRead(),
                item.createdAt());
    }
}
