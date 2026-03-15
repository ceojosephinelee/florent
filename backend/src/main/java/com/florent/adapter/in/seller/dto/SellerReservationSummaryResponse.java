package com.florent.adapter.in.seller.dto;

import com.florent.domain.reservation.SellerReservationSummaryResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record SellerReservationSummaryResponse(
        Long reservationId,
        String status,
        String conceptTitle,
        BigDecimal price,
        String fulfillmentType,
        LocalDate fulfillmentDate,
        FulfillmentSlot fulfillmentSlot,
        String buyerNickName,
        LocalDateTime confirmedAt
) {
    public record FulfillmentSlot(String kind, String value) {}

    public static SellerReservationSummaryResponse from(SellerReservationSummaryResult result) {
        return new SellerReservationSummaryResponse(
                result.reservationId(),
                result.status(),
                result.conceptTitle(),
                result.price(),
                result.fulfillmentType(),
                result.fulfillmentDate(),
                new FulfillmentSlot(result.fulfillmentSlotKind(), result.fulfillmentSlotValue()),
                result.buyerNickName(),
                result.confirmedAt()
        );
    }
}
