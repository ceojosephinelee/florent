package com.florent.domain.request;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record SellerRequestSummaryResult(
        Long requestId,
        RequestStatus status,
        List<String> purposeTags,
        List<String> relationTags,
        List<String> moodTags,
        BudgetTier budgetTier,
        FulfillmentType fulfillmentType,
        LocalDate fulfillmentDate,
        LocalDateTime expiresAt,
        String myProposalStatus
) {
    public static SellerRequestSummaryResult from(CurationRequest request, String myProposalStatus) {
        return new SellerRequestSummaryResult(
                request.getId(),
                request.getStatus(),
                request.getPurposeTags(),
                request.getRelationTags(),
                request.getMoodTags(),
                request.getBudgetTier(),
                request.getFulfillmentType(),
                request.getFulfillmentDate(),
                request.getExpiresAt(),
                myProposalStatus
        );
    }
}
