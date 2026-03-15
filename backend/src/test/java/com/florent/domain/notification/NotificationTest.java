package com.florent.domain.notification;

import com.florent.common.exception.BusinessException;
import com.florent.common.exception.ErrorCode;
import com.florent.support.TestFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Clock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NotificationTest {

    private final Clock fixedClock = TestFixtures.FIXED_CLOCK;

    @Test
    @DisplayName("create() — isRead는 false이고 필드가 올바르게 설정된다")
    void create_정상_생성() {
        // given & when
        Notification n = Notification.create(
                1L, NotificationType.REQUEST_ARRIVED,
                ReferenceType.REQUEST, 100L,
                "제목", "본문", fixedClock);

        // then
        assertThat(n.getUserId()).isEqualTo(1L);
        assertThat(n.getType()).isEqualTo(NotificationType.REQUEST_ARRIVED);
        assertThat(n.getReferenceType()).isEqualTo(ReferenceType.REQUEST);
        assertThat(n.getReferenceId()).isEqualTo(100L);
        assertThat(n.isRead()).isFalse();
        assertThat(n.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("markAsRead() — 읽지 않은 알림을 읽음 처리한다")
    void markAsRead_정상_처리() {
        // given
        Notification n = Notification.create(
                1L, NotificationType.PROPOSAL_ARRIVED,
                ReferenceType.PROPOSAL, 200L,
                "제목", "본문", fixedClock);

        // when
        n.markAsRead(fixedClock);

        // then
        assertThat(n.isRead()).isTrue();
    }

    @Test
    @DisplayName("markAsRead() — 이미 읽은 알림에 BusinessException이 발생한다")
    void markAsRead_이미_읽은_알림_예외() {
        // given
        Notification n = Notification.create(
                1L, NotificationType.RESERVATION_CONFIRMED,
                ReferenceType.RESERVATION, 300L,
                "제목", "본문", fixedClock);
        n.markAsRead(fixedClock);

        // when & then
        assertThatThrownBy(() -> n.markAsRead(fixedClock))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getErrorCode())
                        .isEqualTo(ErrorCode.NOTIFICATION_ALREADY_READ));
    }
}