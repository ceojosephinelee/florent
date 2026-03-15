package com.florent.domain.reservation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record BuyerReservationSummaryResult(
        Long reservationId,
        String status,
        String shopName,
        String conceptTitle,
        BigDecimal price,
        String fulfillmentType,
        LocalDate fulfillmentDate,
        String fulfillmentSlotKind,
        String fulfillmentSlotValue,
        LocalDateTime confirmedAt
) {}
