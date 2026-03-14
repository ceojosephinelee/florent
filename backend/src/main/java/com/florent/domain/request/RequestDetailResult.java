package com.florent.domain.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record RequestDetailResult(
        Long requestId,
        RequestStatus status,
        List<String> purposeTags,
        List<String> relationTags,
        List<String> moodTags,
        BudgetTier budgetTier,
        FulfillmentType fulfillmentType,
        LocalDate fulfillmentDate,
        List<TimeSlot> requestedTimeSlots,
        String placeAddressText,
        BigDecimal placeLat,
        BigDecimal placeLng,
        LocalDateTime expiresAt,
        int draftProposalCount,
        int submittedProposalCount
) {
    public static RequestDetailResult from(CurationRequest request,
                                           int draftCount, int submittedCount) {
        return new RequestDetailResult(
                request.getId(),
                request.getStatus(),
                request.getPurposeTags(),
                request.getRelationTags(),
                request.getMoodTags(),
                request.getBudgetTier(),
                request.getFulfillmentType(),
                request.getFulfillmentDate(),
                request.getRequestedTimeSlots(),
                request.getPlaceAddressText(),
                request.getPlaceLat(),
                request.getPlaceLng(),
                request.getExpiresAt(),
                draftCount,
                submittedCount
        );
    }
}
