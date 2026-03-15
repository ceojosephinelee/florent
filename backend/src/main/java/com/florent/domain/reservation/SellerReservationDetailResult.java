package com.florent.domain.reservation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record SellerReservationDetailResult(
        Long reservationId,
        String status,
        String fulfillmentType,
        LocalDate fulfillmentDate,
        String fulfillmentSlotKind,
        String fulfillmentSlotValue,
        String buyerNickName,
        String placeAddressText,
        BigDecimal placeLat,
        BigDecimal placeLng,
        LocalDateTime confirmedAt,
        ProposalInfo proposal,
        RequestInfo request
) {
    public record ProposalInfo(
            Long proposalId,
            String conceptTitle,
            String description,
            List<String> imageUrls,
            BigDecimal price
    ) {}

    public record RequestInfo(
            Long requestId,
            List<String> purposeTags,
            List<String> relationTags,
            List<String> moodTags,
            String budgetTier
    ) {}
}
