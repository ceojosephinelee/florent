package com.florent.domain.reservation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record BuyerReservationDetailResult(
        Long reservationId,
        String status,
        String fulfillmentType,
        LocalDate fulfillmentDate,
        String fulfillmentSlotKind,
        String fulfillmentSlotValue,
        String placeAddressText,
        BigDecimal placeLat,
        BigDecimal placeLng,
        LocalDateTime confirmedAt,
        ProposalInfo proposal,
        ShopInfo shop,
        RequestInfo request
) {
    public record ProposalInfo(
            Long proposalId,
            String conceptTitle,
            String description,
            List<String> imageUrls,
            BigDecimal price
    ) {}

    public record ShopInfo(
            Long shopId,
            String name,
            String phone,
            String addressText,
            BigDecimal lat,
            BigDecimal lng
    ) {}

    public record RequestInfo(
            Long requestId,
            List<String> purposeTags,
            List<String> relationTags,
            List<String> moodTags,
            String budgetTier
    ) {}
}
