package com.florent.adapter.in.buyer.dto;

import com.florent.domain.reservation.BuyerReservationSummaryResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record BuyerReservationSummaryResponse(
        Long reservationId,
        String status,
        String shopName,
        String conceptTitle,
        BigDecimal price,
        String fulfillmentType,
        LocalDate fulfillmentDate,
        FulfillmentSlot fulfillmentSlot,
        LocalDateTime confirmedAt
) {
    public record FulfillmentSlot(String kind, String value) {}

    public static BuyerReservationSummaryResponse from(BuyerReservationSummaryResult result) {
        return new BuyerReservationSummaryResponse(
                result.reservationId(),
                result.status(),
                result.shopName(),
                result.conceptTitle(),
                result.price(),
                result.fulfillmentType(),
                result.fulfillmentDate(),
                new FulfillmentSlot(result.fulfillmentSlotKind(), result.fulfillmentSlotValue()),
                result.confirmedAt()
        );
    }
}
