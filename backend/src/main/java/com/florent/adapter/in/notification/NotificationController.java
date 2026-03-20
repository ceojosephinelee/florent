package com.florent.adapter.in.notification;

import com.florent.adapter.in.notification.dto.MarkReadResponse;
import com.florent.adapter.in.notification.dto.NotificationListResponse;
import com.florent.common.response.ApiResponse;
import com.florent.common.security.UserPrincipal;
import com.florent.domain.notification.GetNotificationsUseCase;
import com.florent.domain.notification.MarkNotificationReadResult;
import com.florent.domain.notification.MarkNotificationReadUseCase;
import com.florent.domain.notification.NotificationPageResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final GetNotificationsUseCase getNotificationsUseCase;
    private final MarkNotificationReadUseCase markNotificationReadUseCase;

    @GetMapping
    public ResponseEntity<ApiResponse<NotificationListResponse>> getNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        NotificationPageResult result = getNotificationsUseCase.getNotifications(
                principal.getUserId(), page, size);
        log.debug("알림 조회: userId={}, page={}, size={}, 결과={}건",
                principal.getUserId(), page, size, result.notifications().size());
        return ResponseEntity.ok(ApiResponse.success(NotificationListResponse.from(result)));
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse<MarkReadResponse>> markAsRead(
            @PathVariable Long notificationId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        MarkNotificationReadResult result = markNotificationReadUseCase.markAsRead(
                notificationId, principal.getUserId());
        return ResponseEntity.ok(ApiResponse.success(MarkReadResponse.from(result)));
    }
}