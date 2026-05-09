package com.florent.domain.reservation;

public interface ConfirmSellerReservationUseCase {
    ConfirmReservationResult confirmBySeller(Long reservationId, Long sellerId);
}
