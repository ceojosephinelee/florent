package com.florent.domain.reservation;

import com.florent.support.TestFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ReservationTest {

    private final Clock fixedClock = TestFixtures.FIXED_CLOCK;

    @Test
    @DisplayName("create() — CONFIRMED 상태이고 confirmedAt/createdAt이 현재 시각이다")
    void create_상태는_CONFIRMED이고_시각이_설정된다() {
        // given
        LocalDateTime now = LocalDateTime.now(fixedClock);

        // when
        Reservation reservation = Reservation.create(
                1L, 10L, "PICKUP", LocalDate.of(2026, 3, 20),
                "PICKUP_30M", "14:00",
                "서울시 강남구", new BigDecimal("37.498095"), new BigDecimal("127.027610"),
                fixedClock);

        // then
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
        assertThat(reservation.getRequestId()).isEqualTo(1L);
        assertThat(reservation.getProposalId()).isEqualTo(10L);
        assertThat(reservation.getFulfillmentType()).isEqualTo("PICKUP");
        assertThat(reservation.getFulfillmentDate()).isEqualTo(LocalDate.of(2026, 3, 20));
        assertThat(reservation.getFulfillmentSlotKind()).isEqualTo("PICKUP_30M");
        assertThat(reservation.getFulfillmentSlotValue()).isEqualTo("14:00");
        assertThat(reservation.getPlaceAddressText()).isEqualTo("서울시 강남구");
        assertThat(reservation.getConfirmedAt()).isEqualTo(now);
        assertThat(reservation.getCreatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("reconstitute() — 모든 필드가 올바르게 복원된다")
    void reconstitute_모든_필드_복원() {
        // given
        LocalDateTime confirmedAt = LocalDateTime.of(2026, 3, 15, 10, 0);
        LocalDateTime createdAt = LocalDateTime.of(2026, 3, 15, 10, 0);

        // when
        Reservation reservation = Reservation.reconstitute(
                1L, 2L, 3L, ReservationStatus.CONFIRMED,
                "DELIVERY", LocalDate.of(2026, 3, 20),
                "DELIVERY_WINDOW", "MORNING",
                "서울시 마포구", new BigDecimal("37.55"), new BigDecimal("126.92"),
                confirmedAt, createdAt);

        // then
        assertThat(reservation.getId()).isEqualTo(1L);
        assertThat(reservation.getRequestId()).isEqualTo(2L);
        assertThat(reservation.getProposalId()).isEqualTo(3L);
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
        assertThat(reservation.getFulfillmentType()).isEqualTo("DELIVERY");
        assertThat(reservation.getFulfillmentSlotKind()).isEqualTo("DELIVERY_WINDOW");
        assertThat(reservation.getFulfillmentSlotValue()).isEqualTo("MORNING");
    }
}
