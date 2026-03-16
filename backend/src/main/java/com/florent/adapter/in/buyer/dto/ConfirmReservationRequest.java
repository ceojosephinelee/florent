package com.florent.adapter.in.buyer.dto;

import jakarta.validation.constraints.NotBlank;

public record ConfirmReservationRequest(
        @NotBlank String idempotencyKey
) {}
