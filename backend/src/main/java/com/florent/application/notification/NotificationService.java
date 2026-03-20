package com.florent.application.notification;

import com.florent.common.exception.BusinessException;
import com.florent.common.exception.ErrorCode;
import com.florent.domain.notification.GetNotificationsUseCase;
import com.florent.domain.notification.MarkNotificationReadResult;
import com.florent.domain.notification.MarkNotificationReadUseCase;
import com.florent.domain.notification.Notification;
import com.florent.domain.notification.NotificationMessages;
import com.florent.domain.notification.NotificationPageResult;
import com.florent.domain.notification.NotificationRepository;
import com.florent.domain.notification.NotificationType;
import com.florent.domain.notification.NotificationUserResolverPort;
import com.florent.domain.notification.OutboxEvent;
import com.florent.domain.notification.OutboxEventRepository;
import com.florent.domain.notification.ReferenceType;
import com.florent.domain.notification.SaveNotificationUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService implements SaveNotificationUseCase,
        GetNotificationsUseCase, MarkNotificationReadUseCase {

    private final NotificationRepository notificationRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final NotificationUserResolverPort userResolverPort;
    private final Clock clock;

    // ── SaveNotificationUseCase ──

    @Override
    public void saveRequestArrived(Long sellerId, Long requestId) {
        Long userId = userResolverPort.findUserIdBySellerId(sellerId);
        saveNotificationWithOutbox(
                userId, NotificationType.REQUEST_ARRIVED,
                ReferenceType.REQUEST, requestId);
    }

    @Override
    public void saveProposalArrived(Long buyerId, Long proposalId) {
        Long userId = userResolverPort.findUserIdByBuyerId(buyerId);
        saveNotificationWithOutbox(
                userId, NotificationType.PROPOSAL_ARRIVED,
                ReferenceType.PROPOSAL, proposalId);
    }

    @Override
    public void saveReservationConfirmed(Long sellerId, Long reservationId) {
        Long userId = userResolverPort.findUserIdBySellerId(sellerId);
        saveNotificationWithOutbox(
                userId, NotificationType.RESERVATION_CONFIRMED,
                ReferenceType.RESERVATION, reservationId);
    }

    // ── GetNotificationsUseCase ──

    @Transactional(readOnly = true)
    @Override
    public NotificationPageResult getNotifications(Long userId, int page, int size) {
        List<Notification> notifications = notificationRepository.findByUserId(userId, page, size);
        long totalElements = notificationRepository.countByUserId(userId);
        return NotificationPageResult.of(notifications, page, size, totalElements);
    }

    // ── MarkNotificationReadUseCase ──

    @Override
    public MarkNotificationReadResult markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOTIFICATION_NOT_FOUND));

        if (!notification.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        notification.markAsRead(clock);
        Notification saved = notificationRepository.save(notification);
        return MarkNotificationReadResult.from(saved);
    }

    // ── private ──

    private void saveNotificationWithOutbox(
            Long userId, NotificationType type,
            ReferenceType referenceType, Long referenceId) {
        String dedupKey = type.name() + ":" + referenceType.name() + ":" + referenceId;
        if (outboxEventRepository.existsByDedupKey(dedupKey)) {
            return;
        }

        Notification notification = Notification.create(
                userId, type, referenceType, referenceId,
                NotificationMessages.title(type),
                NotificationMessages.body(type),
                clock);
        Notification saved = notificationRepository.save(notification);

        log.info("알림 저장 완료: userId={}, type={}, notificationId={}", userId, type, saved.getId());

        OutboxEvent outbox = OutboxEvent.create(saved.getId(), dedupKey, clock);
        outboxEventRepository.save(outbox);
    }
}
