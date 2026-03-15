package com.florent.domain.reservation;

public record ConfirmReservationCommand(
        Long buyerId,
        Long proposalId,
        String idempotencyKey
) {}
