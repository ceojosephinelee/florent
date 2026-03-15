package com.florent.adapter.in.seller.dto;

import com.florent.domain.reservation.SellerReservationDetailResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record SellerReservationDetailResponse(
        Long reservationId,
        String status,
        String buyerNickName,
        ProposalInfo proposal,
        String fulfillmentType,
        LocalDate fulfillmentDate,
        FulfillmentSlot fulfillmentSlot,
        String placeAddressText,
        BigDecimal placeLat,
        BigDecimal placeLng,
        LocalDateTime confirmedAt,
        RequestInfo request
) {
    public record ProposalInfo(
            Long proposalId, String conceptTitle, String description,
            List<String> imageUrls, BigDecimal price
    ) {}

    public record FulfillmentSlot(String kind, String value) {}

    public record RequestInfo(
            Long requestId, List<String> purposeTags,
            List<String> relationTags, List<String> moodTags,
            String budgetTier
    ) {}

    public static SellerReservationDetailResponse from(SellerReservationDetailResult result) {
        return new SellerReservationDetailResponse(
                result.reservationId(),
                result.status(),
                result.buyerNickName(),
                new ProposalInfo(
                        result.proposal().proposalId(),
                        result.proposal().conceptTitle(),
                        result.proposal().description(),
                        result.proposal().imageUrls(),
                        result.proposal().price()),
                result.fulfillmentType(),
                result.fulfillmentDate(),
                new FulfillmentSlot(result.fulfillmentSlotKind(), result.fulfillmentSlotValue()),
                result.placeAddressText(),
                result.placeLat(),
                result.placeLng(),
                result.confirmedAt(),
                new RequestInfo(
                        result.request().requestId(),
                        result.request().purposeTags(),
                        result.request().relationTags(),
                        result.request().moodTags(),
                        result.request().budgetTier())
        );
    }
}
