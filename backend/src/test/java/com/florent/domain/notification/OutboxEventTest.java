package com.florent.domain.notification;

import com.florent.support.TestFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Clock;

import static org.assertj.core.api.Assertions.assertThat;

class OutboxEventTest {

    private final Clock fixedClock = TestFixtures.FIXED_CLOCK;

    @Test
    @DisplayName("create() — PENDING 상태이고 attemptCount는 0이다")
    void create_정상_생성() {
        // given & when
        OutboxEvent event = OutboxEvent.create(1L, "dedup-key-1", fixedClock);

        // then
        assertThat(event.getStatus()).isEqualTo(OutboxStatus.PENDING);
        assertThat(event.getAttemptCount()).isZero();
        assertThat(event.getNotificationId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("markSent() — 상태가 SENT로 변경된다")
    void markSent_정상_전이() {
        // given
        OutboxEvent event = OutboxEvent.create(1L, "dedup-key-1", fixedClock);

        // when
        event.markSent();

        // then
        assertThat(event.getStatus()).isEqualTo(OutboxStatus.SENT);
    }

    @Test
    @DisplayName("incrementAttemptAndRetry() — 3회 시도 후 FAILED로 전이한다")
    void incrementAttempt_3회_초과_시_FAILED() {
        // given
        OutboxEvent event = OutboxEvent.create(1L, "dedup-key-1", fixedClock);

        // when
        event.incrementAttemptAndRetry(fixedClock); // 1회
        event.incrementAttemptAndRetry(fixedClock); // 2회
        event.incrementAttemptAndRetry(fixedClock); // 3회 → FAILED

        // then
        assertThat(event.getAttemptCount()).isEqualTo(3);
        assertThat(event.getStatus()).isEqualTo(OutboxStatus.FAILED);
    }

    @Test
    @DisplayName("incrementAttemptAndRetry() — 3회 미만이면 PENDING을 유지한다")
    void incrementAttempt_3회_미만_PENDING_유지() {
        // given
        OutboxEvent event = OutboxEvent.create(1L, "dedup-key-1", fixedClock);

        // when
        event.incrementAttemptAndRetry(fixedClock); // 1회

        // then
        assertThat(event.getAttemptCount()).isEqualTo(1);
        assertThat(event.getStatus()).isEqualTo(OutboxStatus.PENDING);
    }
}