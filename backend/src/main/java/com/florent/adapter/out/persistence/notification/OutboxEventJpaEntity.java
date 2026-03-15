package com.florent.adapter.out.persistence.notification;

import com.florent.domain.notification.OutboxEvent;
import com.florent.domain.notification.OutboxStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "outbox_event")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class OutboxEventJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long notificationId;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(nullable = false, unique = true)
    private String dedupKey;

    @Column(nullable = false)
    private int attemptCount;

    @Column(nullable = false)
    private LocalDateTime availableAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    public static OutboxEventJpaEntity from(OutboxEvent domain) {
        OutboxEventJpaEntity e = new OutboxEventJpaEntity();
        e.id = domain.getId();
        e.notificationId = domain.getNotificationId();
        e.status = domain.getStatus().name();
        e.dedupKey = domain.getDedupKey();
        e.attemptCount = domain.getAttemptCount();
        e.availableAt = domain.getAvailableAt();
        e.createdAt = domain.getCreatedAt();
        return e;
    }

    public OutboxEvent toDomain() {
        return OutboxEvent.reconstitute(
                id, notificationId,
                OutboxStatus.valueOf(status),
                dedupKey, attemptCount,
                availableAt, createdAt);
    }
}