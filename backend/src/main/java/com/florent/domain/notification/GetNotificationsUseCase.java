package com.florent.domain.notification;

public interface GetNotificationsUseCase {
    NotificationPageResult getNotifications(Long userId, int page, int size);
}