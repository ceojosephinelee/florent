package com.florent.domain.request;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record SellerRequestDetailResult(
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
        LocalDateTime expiresAt,
        Long myProposalId,
        String myProposalStatus
) {
    public static SellerRequestDetailResult from(CurationRequest request,
                                                  Long myProposalId,
                                                  String myProposalStatus) {
        return new SellerRequestDetailResult(
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
                request.getExpiresAt(),
                myProposalId,
                myProposalStatus
        );
    }
}
