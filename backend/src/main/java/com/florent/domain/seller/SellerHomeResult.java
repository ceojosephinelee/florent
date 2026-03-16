package com.florent.domain.seller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record SellerHomeResult(
        int openRequestCount,
        int draftProposalCount,
        int submittedProposalCount,
        int confirmedReservationCount,
        List<RecentRequestItem> recentRequests
) {
    public record RecentRequestItem(
            Long requestId,
            String status,
            List<String> purposeTags,
            String budgetTier,
            String fulfillmentType,
            LocalDate fulfillmentDate,
            LocalDateTime expiresAt
    ) {}
}
