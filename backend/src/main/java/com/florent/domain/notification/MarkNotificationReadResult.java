package com.florent.domain.notification;

public record MarkNotificationReadResult(
        Long notificationId,
        boolean isRead
) {
    public static MarkNotificationReadResult from(Notification notification) {
        return new MarkNotificationReadResult(notification.getId(), notification.isRead());
    }
}