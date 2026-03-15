package com.florent.adapter.in.notification.dto;

import com.florent.domain.notification.MarkNotificationReadResult;

public record MarkReadResponse(
        Long notificationId,
        boolean isRead
) {
    public static MarkReadResponse from(MarkNotificationReadResult result) {
        return new MarkReadResponse(result.notificationId(), result.isRead());
    }
}