package com.florent.domain.notification;

import java.time.LocalDateTime;
import java.util.List;

public record NotificationPageResult(
        List<NotificationItem> notifications,
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
        List<NotificationItem> items = notifications.stream()
                .map(NotificationItem::from)
                .toList();
        return new NotificationPageResult(items, page, size, totalElements, totalPages, last);
    }

    public record NotificationItem(
            Long notificationId,
            String type,
            String referenceType,
            Long referenceId,
            String title,
            String body,
            boolean isRead,
            LocalDateTime createdAt
    ) {
        public static NotificationItem from(Notification n) {
            return new NotificationItem(
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
}
