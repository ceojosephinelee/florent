package com.florent.domain.request;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record RequestSummaryResult(
        Long requestId,
        RequestStatus status,
        List<String> purposeTags,
        List<String> relationTags,
        BudgetTier budgetTier,
        FulfillmentType fulfillmentType,
        LocalDate fulfillmentDate,
        LocalDateTime expiresAt,
        int draftProposalCount,
        int submittedProposalCount
) {
    public static RequestSummaryResult from(CurationRequest request,
                                            int draftCount, int submittedCount) {
        return new RequestSummaryResult(
                request.getId(),
                request.getStatus(),
                request.getPurposeTags(),
                request.getRelationTags(),
                request.getBudgetTier(),
                request.getFulfillmentType(),
                request.getFulfillmentDate(),
                request.getExpiresAt(),
                draftCount,
                submittedCount
        );
    }
}
