package com.florent.domain.reservation;

public interface ConfirmReservationUseCase {
    ConfirmReservationResult confirm(ConfirmReservationCommand command);
}
