package com.florent.adapter.in.buyer.dto;

import com.florent.domain.reservation.BuyerReservationDetailResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record BuyerReservationDetailResponse(
        Long reservationId,
        String status,
        ProposalInfo proposal,
        ShopInfo shop,
        String fulfillmentType,
        LocalDate fulfillmentDate,
        FulfillmentSlot fulfillmentSlot,
        String placeAddressText,
        LocalDateTime confirmedAt,
        RequestInfo request
) {
    public record ProposalInfo(
            Long proposalId, String conceptTitle, String description,
            List<String> imageUrls, BigDecimal price
    ) {}

    public record ShopInfo(
            Long shopId, String name, String phone,
            String addressText, BigDecimal lat, BigDecimal lng
    ) {}

    public record FulfillmentSlot(String kind, String value) {}

    public record RequestInfo(
            Long requestId, List<String> purposeTags,
            List<String> relationTags, List<String> moodTags,
            String budgetTier
    ) {}

    public static BuyerReservationDetailResponse from(BuyerReservationDetailResult result) {
        return new BuyerReservationDetailResponse(
                result.reservationId(),
                result.status(),
                new ProposalInfo(
                        result.proposal().proposalId(),
                        result.proposal().conceptTitle(),
                        result.proposal().description(),
                        result.proposal().imageUrls(),
                        result.proposal().price()),
                new ShopInfo(
                        result.shop().shopId(),
                        result.shop().name(),
                        result.shop().phone(),
                        result.shop().addressText(),
                        result.shop().lat(),
                        result.shop().lng()),
                result.fulfillmentType(),
                result.fulfillmentDate(),
                new FulfillmentSlot(result.fulfillmentSlotKind(), result.fulfillmentSlotValue()),
                result.placeAddressText(),
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
