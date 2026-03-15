package com.florent.domain.notification;

import com.florent.common.exception.BusinessException;
import com.florent.common.exception.ErrorCode;
import lombok.Getter;

import java.time.Clock;
import java.time.LocalDateTime;

@Getter
public class OutboxEvent {
    private static final int MAX_ATTEMPT_COUNT = 3;

    private Long id;
    private Long notificationId;
    private OutboxStatus status;
    private String dedupKey;
    private int attemptCount;
    private LocalDateTime availableAt;
    private LocalDateTime createdAt;

    private OutboxEvent() {}

    public static OutboxEvent create(Long notificationId, String dedupKey, Clock clock) {
        OutboxEvent e = new OutboxEvent();
        e.notificationId = notificationId;
        e.status = OutboxStatus.PENDING;
        e.dedupKey = dedupKey;
        e.attemptCount = 0;
        e.availableAt = LocalDateTime.now(clock);
        e.createdAt = LocalDateTime.now(clock);
        return e;
    }

    public void markSent() {
        this.status = OutboxStatus.SENT;
    }

    public void incrementAttemptAndRetry(Clock clock) {
        this.attemptCount++;
        if (this.attemptCount >= MAX_ATTEMPT_COUNT) {
            this.status = OutboxStatus.FAILED;
        } else {
            this.availableAt = LocalDateTime.now(clock).plusSeconds(30L * attemptCount);
        }
    }

    public static OutboxEvent reconstitute(
            Long id, Long notificationId, OutboxStatus status,
            String dedupKey, int attemptCount,
            LocalDateTime availableAt, LocalDateTime createdAt) {
        OutboxEvent e = new OutboxEvent();
        e.id = id;
        e.notificationId = notificationId;
        e.status = status;
        e.dedupKey = dedupKey;
        e.attemptCount = attemptCount;
        e.availableAt = availableAt;
        e.createdAt = createdAt;
        return e;
    }
}