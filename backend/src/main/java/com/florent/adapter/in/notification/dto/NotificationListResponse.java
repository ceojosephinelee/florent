package com.florent.adapter.in.notification.dto;

import com.florent.domain.notification.NotificationPageResult;

import java.util.List;

public record NotificationListResponse(
        List<NotificationResponse> notifications,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean last
) {
    public static NotificationListResponse from(NotificationPageResult result) {
        List<NotificationResponse> items = result.notifications().stream()
                .map(NotificationResponse::from)
                .toList();
        return new NotificationListResponse(
                items, result.page(), result.size(),
                result.totalElements(), result.totalPages(), result.last());
    }
}
