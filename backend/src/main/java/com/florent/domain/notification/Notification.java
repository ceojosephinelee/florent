package com.florent.domain.notification;

import com.florent.common.exception.BusinessException;
import com.florent.common.exception.ErrorCode;
import lombok.Getter;

import java.time.Clock;
import java.time.LocalDateTime;

@Getter
public class Notification {
    private Long id;
    private Long userId;
    private NotificationType type;
    private ReferenceType referenceType;
    private Long referenceId;
    private String title;
    private String body;
    private boolean isRead;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Notification() {}

    public static Notification create(
            Long userId, NotificationType type,
            ReferenceType referenceType, Long referenceId,
            String title, String body, Clock clock) {
        Notification n = new Notification();
        n.userId = userId;
        n.type = type;
        n.referenceType = referenceType;
        n.referenceId = referenceId;
        n.title = title;
        n.body = body;
        n.isRead = false;
        n.createdAt = LocalDateTime.now(clock);
        n.updatedAt = LocalDateTime.now(clock);
        return n;
    }

    public void markAsRead(Clock clock) {
        if (this.isRead) {
            throw new BusinessException(ErrorCode.NOTIFICATION_ALREADY_READ);
        }
        this.isRead = true;
        this.updatedAt = LocalDateTime.now(clock);
    }

    public static Notification reconstitute(
            Long id, Long userId, NotificationType type,
            ReferenceType referenceType, Long referenceId,
            String title, String body, boolean isRead,
            LocalDateTime createdAt, LocalDateTime updatedAt) {
        Notification n = new Notification();
        n.id = id;
        n.userId = userId;
        n.type = type;
        n.referenceType = referenceType;
        n.referenceId = referenceId;
        n.title = title;
        n.body = body;
        n.isRead = isRead;
        n.createdAt = createdAt;
        n.updatedAt = updatedAt;
        return n;
    }
}