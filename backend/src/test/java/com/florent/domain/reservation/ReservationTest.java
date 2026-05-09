package com.florent.domain.reservation;

import com.florent.common.exception.BusinessException;
import com.florent.common.exception.ErrorCode;
import com.florent.support.TestFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReservationTest {

    private final Clock fixedClock = TestFixtures.FIXED_CLOCK;

    private Reservation createPendingReservation() {
        return Reservation.create(
                1L, 10L, "PICKUP", LocalDate.of(2026, 3, 20),
                "PICKUP_30M", "14:00",
                "서울시 강남구", new BigDecimal("37.498095"), new BigDecimal("127.027610"),
                fixedClock);
    }

    @Test
    @DisplayName("create() — PENDING_CONTACT 상태이고 confirmedAt은 null이다")
    void create_상태는_PENDING_CONTACT이고_confirmedAt은_null() {
        // when
        Reservation reservation = createPendingReservation();

        // then
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.PENDING_CONTACT);
        assertThat(reservation.getConfirmedAt()).isNull();
        assertThat(reservation.getCreatedAt()).isEqualTo(LocalDateTime.now(fixedClock));
        assertThat(reservation.getRequestId()).isEqualTo(1L);
        assertThat(reservation.getProposalId()).isEqualTo(10L);
        assertThat(reservation.getFulfillmentType()).isEqualTo("PICKUP");
        assertThat(reservation.getFulfillmentSlotKind()).isEqualTo("PICKUP_30M");
        assertThat(reservation.getFulfillmentSlotValue()).isEqualTo("14:00");
    }

    @Test
    @DisplayName("confirm() — PENDING_CONTACT → CONFIRMED 전이하고 confirmedAt이 설정된다")
    void confirm_PENDING_CONTACT에서_CONFIRMED로_전이() {
        // given
        Reservation reservation = createPendingReservation();

        // when
        reservation.confirm(fixedClock);

        // then
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
        assertThat(reservation.getConfirmedAt()).isEqualTo(LocalDateTime.now(fixedClock));
    }

    @Test
    @DisplayName("cancel() — PENDING_CONTACT → CANCELLED 전이한다")
    void cancel_PENDING_CONTACT에서_CANCELLED로_전이() {
        // given
        Reservation reservation = createPendingReservation();

        // when
        reservation.cancel();

        // then
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CANCELLED);
    }

    @Test
    @DisplayName("confirm() — CONFIRMED 상태에서 호출하면 INVALID_RESERVATION_STATE 예외")
    void confirm_CONFIRMED에서_호출하면_예외() {
        // given
        Reservation reservation = createPendingReservation();
        reservation.confirm(fixedClock);

        // when & then
        assertThatThrownBy(() -> reservation.confirm(fixedClock))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.INVALID_RESERVATION_STATE);
    }

    @Test
    @DisplayName("cancel() — CONFIRMED 상태에서 호출하면 INVALID_RESERVATION_STATE 예외")
    void cancel_CONFIRMED에서_호출하면_예외() {
        // given
        Reservation reservation = createPendingReservation();
        reservation.confirm(fixedClock);

        // when & then
        assertThatThrownBy(() -> reservation.cancel())
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.INVALID_RESERVATION_STATE);
    }

    @Test
    @DisplayName("confirm() — CANCELLED 상태에서 호출하면 INVALID_RESERVATION_STATE 예외")
    void confirm_CANCELLED에서_호출하면_예외() {
        // given
        Reservation reservation = createPendingReservation();
        reservation.cancel();

        // when & then
        assertThatThrownBy(() -> reservation.confirm(fixedClock))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.INVALID_RESERVATION_STATE);
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
