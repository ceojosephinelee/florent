package com.florent.domain.notification;

import java.util.List;

public record NotificationPageResult(
        List<Notification> notifications,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean last
) {
    public static NotificationPageResult of(
            List<Notification> notifications, int page, int size, long totalElements) {
        int totalPages = (int) Math.ceil((double) totalElements / size);
        boolean last = page >= totalPages - 1;
        return new NotificationPageResult(notifications, page, size, totalElements, totalPages, last);
    }
}