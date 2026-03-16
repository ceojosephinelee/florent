package com.florent.domain.reservation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record SellerReservationSummaryResult(
        Long reservationId,
        String status,
        String conceptTitle,
        BigDecimal price,
        String fulfillmentType,
        LocalDate fulfillmentDate,
        String fulfillmentSlotKind,
        String fulfillmentSlotValue,
        String buyerNickName,
        LocalDateTime confirmedAt
) {}
