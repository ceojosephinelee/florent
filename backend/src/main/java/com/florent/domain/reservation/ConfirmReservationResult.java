package com.florent.domain.reservation;

public record ConfirmReservationResult(
        Long reservationId,
        String status
) {
    public static ConfirmReservationResult from(Reservation reservation) {
        return new ConfirmReservationResult(
                reservation.getId(),
                reservation.getStatus().name()
        );
    }
}
