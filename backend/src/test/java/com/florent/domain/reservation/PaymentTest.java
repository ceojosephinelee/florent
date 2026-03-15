package com.florent.domain.reservation;

import com.florent.domain.payment.Payment;
import com.florent.domain.payment.PaymentProvider;
import com.florent.domain.payment.PaymentStatus;
import com.florent.support.TestFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentTest {

    private final Clock fixedClock = TestFixtures.FIXED_CLOCK;

    @Test
    @DisplayName("createSucceeded() — MOCK 제공자, SUCCEEDED 상태, KRW 통화로 생성된다")
    void createSucceeded_정상_생성() {
        // given
        LocalDateTime now = LocalDateTime.now(fixedClock);

        // when
        Payment payment = Payment.createSucceeded(
                1L, new BigDecimal("35000"), "uuid-key-123", fixedClock);

        // then
        assertThat(payment.getReservationId()).isEqualTo(1L);
        assertThat(payment.getProvider()).isEqualTo(PaymentProvider.MOCK);
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.SUCCEEDED);
        assertThat(payment.getAmount()).isEqualByComparingTo(new BigDecimal("35000"));
        assertThat(payment.getCurrency()).isEqualTo("KRW");
        assertThat(payment.getIdempotencyKey()).isEqualTo("uuid-key-123");
        assertThat(payment.getPaidAt()).isEqualTo(now);
        assertThat(payment.getCreatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("reconstitute() — 모든 필드가 올바르게 복원된다")
    void reconstitute_모든_필드_복원() {
        // given
        LocalDateTime paidAt = LocalDateTime.of(2026, 3, 15, 10, 0);
        LocalDateTime createdAt = LocalDateTime.of(2026, 3, 15, 10, 0);

        // when
        Payment payment = Payment.reconstitute(
                1L, 2L, PaymentProvider.MOCK, PaymentStatus.SUCCEEDED,
                new BigDecimal("50000"), "KRW", "key-abc",
                paidAt, createdAt);

        // then
        assertThat(payment.getId()).isEqualTo(1L);
        assertThat(payment.getReservationId()).isEqualTo(2L);
        assertThat(payment.getProvider()).isEqualTo(PaymentProvider.MOCK);
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.SUCCEEDED);
        assertThat(payment.getAmount()).isEqualByComparingTo(new BigDecimal("50000"));
        assertThat(payment.getIdempotencyKey()).isEqualTo("key-abc");
    }
}
