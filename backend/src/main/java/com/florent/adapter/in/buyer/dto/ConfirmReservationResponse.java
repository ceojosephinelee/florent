package com.florent.adapter.in.buyer.dto;

import com.florent.domain.reservation.ConfirmReservationResult;

import java.math.BigDecimal;

public record ConfirmReservationResponse(
        Long reservationId,
        String status,
        String paymentStatus,
        BigDecimal amount
) {
    public static ConfirmReservationResponse from(ConfirmReservationResult result) {
        return new ConfirmReservationResponse(
                result.reservationId(),
                result.status(),
                result.paymentStatus(),
                result.amount()
        );
    }
}
