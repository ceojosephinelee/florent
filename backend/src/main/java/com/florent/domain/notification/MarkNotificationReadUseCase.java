package com.florent.domain.notification;

public interface MarkNotificationReadUseCase {
    MarkNotificationReadResult markAsRead(Long notificationId, Long userId);
}