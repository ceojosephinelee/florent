package com.florent.domain.reservation;

import com.florent.domain.payment.Payment;

import java.math.BigDecimal;

public record ConfirmReservationResult(
        Long reservationId,
        String status,
        String paymentStatus,
        BigDecimal amount
) {
    public static ConfirmReservationResult from(Reservation reservation, Payment payment) {
        return new ConfirmReservationResult(
                reservation.getId(),
                reservation.getStatus().name(),
                payment.getStatus().name(),
                payment.getAmount()
        );
    }
}
