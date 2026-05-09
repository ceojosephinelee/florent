package com.florent.adapter.in.buyer.dto;

import com.florent.domain.reservation.ConfirmReservationResult;

public record ConfirmReservationResponse(
        Long reservationId,
        String status
) {
    public static ConfirmReservationResponse from(ConfirmReservationResult result) {
        return new ConfirmReservationResponse(
                result.reservationId(),
                result.status()
        );
    }
}
