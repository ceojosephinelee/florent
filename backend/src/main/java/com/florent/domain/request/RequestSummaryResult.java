package com.florent.domain.request;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record RequestSummaryResult(
        Long requestId,
        RequestStatus status,
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
                request.getBudgetTier(),
                request.getFulfillmentType(),
                request.getFulfillmentDate(),
                request.getExpiresAt(),
                draftCount,
                submittedCount
        );
    }
}
