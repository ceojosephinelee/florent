package com.florent.adapter.out.persistence.notification;

import com.florent.domain.notification.Notification;
import com.florent.domain.notification.NotificationType;
import com.florent.domain.notification.ReferenceType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class NotificationJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, length = 30)
    private String type;

    @Column(nullable = false, length = 30)
    private String referenceType;

    @Column(nullable = false)
    private Long referenceId;

    @Column(nullable = false)
    private String title;

    @Column(length = 500)
    private String body;

    @Column(nullable = false)
    private boolean isRead;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public static NotificationJpaEntity from(Notification domain) {
        NotificationJpaEntity e = new NotificationJpaEntity();
        e.id = domain.getId();
        e.userId = domain.getUserId();
        e.type = domain.getType().name();
        e.referenceType = domain.getReferenceType().name();
        e.referenceId = domain.getReferenceId();
        e.title = domain.getTitle();
        e.body = domain.getBody();
        e.isRead = domain.isRead();
        e.createdAt = domain.getCreatedAt();
        e.updatedAt = domain.getUpdatedAt();
        return e;
    }

    public Notification toDomain() {
        return Notification.reconstitute(
                id, userId,
                NotificationType.valueOf(type),
                ReferenceType.valueOf(referenceType),
                referenceId, title, body, isRead,
                createdAt, updatedAt);
    }
}