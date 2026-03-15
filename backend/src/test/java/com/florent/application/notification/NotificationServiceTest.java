package com.florent.application.notification;

import com.florent.common.exception.BusinessException;
import com.florent.common.exception.ErrorCode;
import com.florent.domain.notification.MarkNotificationReadResult;
import com.florent.domain.notification.Notification;
import com.florent.domain.notification.NotificationPageResult;
import com.florent.domain.notification.NotificationType;
import com.florent.domain.notification.OutboxEvent;
import com.florent.domain.notification.ReferenceType;
import com.florent.fake.FakeNotificationRepository;
import com.florent.fake.FakeNotificationUserResolverPort;
import com.florent.fake.FakeOutboxEventRepository;
import com.florent.support.TestFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NotificationServiceTest {

    private FakeNotificationRepository notificationRepository;
    private FakeOutboxEventRepository outboxEventRepository;
    private FakeNotificationUserResolverPort userResolverPort;
    private NotificationService sut;

    private final Clock fixedClock = TestFixtures.FIXED_CLOCK;

    @BeforeEach
    void setUp() {
        notificationRepository = new FakeNotificationRepository();
        outboxEventRepository = new FakeOutboxEventRepository();
        userResolverPort = new FakeNotificationUserResolverPort();
        sut = new NotificationService(
                notificationRepository, outboxEventRepository,
                userResolverPort, fixedClock);
    }

    // ─── saveRequestArrived ───

    @Test
    @DisplayName("saveRequestArrived — Notification과 OutboxEvent가 저장된다")
    void saveRequestArrived_정상_저장() {
        // given
        userResolverPort.addSellerMapping(10L, 100L);

        // when
        sut.saveRequestArrived(10L, 1L);

        // then
        List<Notification> notifications = notificationRepository.findByUserId(100L, 0, 10);
        assertThat(notifications).hasSize(1);
        assertThat(notifications.get(0).getType()).isEqualTo(NotificationType.REQUEST_ARRIVED);
        assertThat(notifications.get(0).getReferenceType()).isEqualTo(ReferenceType.REQUEST);
        assertThat(notifications.get(0).getReferenceId()).isEqualTo(1L);

        List<OutboxEvent> outboxEvents = outboxEventRepository.findAll();
        assertThat(outboxEvents).hasSize(1);
    }

    @Test
    @DisplayName("saveProposalArrived — PROPOSAL_ARRIVED 알림이 저장된다")
    void saveProposalArrived_정상_저장() {
        // given
        userResolverPort.addBuyerMapping(5L, 50L);

        // when
        sut.saveProposalArrived(5L, 200L);

        // then
        List<Notification> notifications = notificationRepository.findByUserId(50L, 0, 10);
        assertThat(notifications).hasSize(1);
        assertThat(notifications.get(0).getType()).isEqualTo(NotificationType.PROPOSAL_ARRIVED);
    }

    @Test
    @DisplayName("saveReservationConfirmed — RESERVATION_CONFIRMED 알림이 저장된다")
    void saveReservationConfirmed_정상_저장() {
        // given
        userResolverPort.addSellerMapping(10L, 100L);

        // when
        sut.saveReservationConfirmed(10L, 300L);

        // then
        List<Notification> notifications = notificationRepository.findByUserId(100L, 0, 10);
        assertThat(notifications).hasSize(1);
        assertThat(notifications.get(0).getType()).isEqualTo(NotificationType.RESERVATION_CONFIRMED);
        assertThat(notifications.get(0).getReferenceType()).isEqualTo(ReferenceType.RESERVATION);
    }

    // ─── getNotifications ───

    @Test
    @DisplayName("알림 목록을 페이지네이션으로 조회한다")
    void getNotifications_페이지네이션_조회() {
        // given
        sut.saveRequestArrived(1L, 10L);
        sut.saveRequestArrived(1L, 20L);
        sut.saveRequestArrived(1L, 30L);

        // when
        NotificationPageResult result = sut.getNotifications(1L, 0, 2);

        // then
        assertThat(result.notifications()).hasSize(2);
        assertThat(result.totalElements()).isEqualTo(3);
        assertThat(result.totalPages()).isEqualTo(2);
        assertThat(result.last()).isFalse();
    }

    @Test
    @DisplayName("알림이 없으면 빈 목록을 반환한다")
    void getNotifications_빈_목록() {
        // given — 없음

        // when
        NotificationPageResult result = sut.getNotifications(999L, 0, 20);

        // then
        assertThat(result.notifications()).isEmpty();
        assertThat(result.totalElements()).isZero();
    }

    // ─── markAsRead ───

    @Test
    @DisplayName("알림을 읽음 처리한다")
    void markAsRead_정상_처리() {
        // given
        sut.saveRequestArrived(1L, 10L);
        Notification saved = notificationRepository.findByUserId(1L, 0, 1).get(0);

        // when
        MarkNotificationReadResult result = sut.markAsRead(saved.getId(), 1L);

        // then
        assertThat(result.isRead()).isTrue();
    }

    @Test
    @DisplayName("존재하지 않는 알림 읽음 처리 시 NOTIFICATION_NOT_FOUND")
    void markAsRead_존재하지_않는_알림() {
        // given — 없음

        // when & then
        assertThatThrownBy(() -> sut.markAsRead(999L, 1L))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getErrorCode())
                        .isEqualTo(ErrorCode.NOTIFICATION_NOT_FOUND));
    }

    @Test
    @DisplayName("다른 사용자의 알림 읽음 처리 시 FORBIDDEN")
    void markAsRead_타인_알림_FORBIDDEN() {
        // given
        sut.saveRequestArrived(1L, 10L);
        Notification saved = notificationRepository.findByUserId(1L, 0, 1).get(0);

        // when & then
        assertThatThrownBy(() -> sut.markAsRead(saved.getId(), 999L))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getErrorCode())
                        .isEqualTo(ErrorCode.FORBIDDEN));
    }
}