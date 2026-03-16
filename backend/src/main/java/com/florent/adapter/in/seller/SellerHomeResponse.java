package com.florent.adapter.in.seller;

import com.florent.domain.seller.SellerHomeResult;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record SellerHomeResponse(
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

    public static SellerHomeResponse from(SellerHomeResult result) {
        List<RecentRequestItem> items = result.recentRequests().stream()
                .map(r -> new RecentRequestItem(
                        r.requestId(), r.status(), r.purposeTags(),
                        r.budgetTier(), r.fulfillmentType(),
                        r.fulfillmentDate(), r.expiresAt()))
                .toList();
        return new SellerHomeResponse(
                result.openRequestCount(),
                result.draftProposalCount(),
                result.submittedProposalCount(),
                result.confirmedReservationCount(),
                items
        );
    }
}
