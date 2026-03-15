package com.florent.domain.reservation;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class Reservation {
    private Long id;
    private Long requestId;
    private Long proposalId;
    private ReservationStatus status;
    private String fulfillmentType;
    private LocalDate fulfillmentDate;
    private String fulfillmentSlotKind;
    private String fulfillmentSlotValue;
    private String placeAddressText;
    private BigDecimal placeLat;
    private BigDecimal placeLng;
    private LocalDateTime confirmedAt;
    private LocalDateTime createdAt;

    private Reservation() {}

    public static Reservation create(
            Long requestId, Long proposalId,
            String fulfillmentType, LocalDate fulfillmentDate,
            String fulfillmentSlotKind, String fulfillmentSlotValue,
            String placeAddressText, BigDecimal placeLat, BigDecimal placeLng,
            Clock clock) {
        Reservation r = new Reservation();
        r.requestId = requestId;
        r.proposalId = proposalId;
        r.status = ReservationStatus.CONFIRMED;
        r.fulfillmentType = fulfillmentType;
        r.fulfillmentDate = fulfillmentDate;
        r.fulfillmentSlotKind = fulfillmentSlotKind;
        r.fulfillmentSlotValue = fulfillmentSlotValue;
        r.placeAddressText = placeAddressText;
        r.placeLat = placeLat;
        r.placeLng = placeLng;
        r.confirmedAt = LocalDateTime.now(clock);
        r.createdAt = LocalDateTime.now(clock);
        return r;
    }

    public static Reservation reconstitute(
            Long id, Long requestId, Long proposalId, ReservationStatus status,
            String fulfillmentType, LocalDate fulfillmentDate,
            String fulfillmentSlotKind, String fulfillmentSlotValue,
            String placeAddressText, BigDecimal placeLat, BigDecimal placeLng,
            LocalDateTime confirmedAt, LocalDateTime createdAt) {
        Reservation r = new Reservation();
        r.id = id;
        r.requestId = requestId;
        r.proposalId = proposalId;
        r.status = status;
        r.fulfillmentType = fulfillmentType;
        r.fulfillmentDate = fulfillmentDate;
        r.fulfillmentSlotKind = fulfillmentSlotKind;
        r.fulfillmentSlotValue = fulfillmentSlotValue;
        r.placeAddressText = placeAddressText;
        r.placeLat = placeLat;
        r.placeLng = placeLng;
        r.confirmedAt = confirmedAt;
        r.createdAt = createdAt;
        return r;
    }
}
