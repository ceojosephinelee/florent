package com.florent.adapter.in.buyer.dto;

import com.florent.domain.request.RequestListResult;
import com.florent.domain.request.RequestSummaryResult;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record RequestListResponse(
        List<RequestSummaryResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean last
) {
    public static RequestListResponse from(RequestListResult result) {
        return new RequestListResponse(
                result.content().stream().map(RequestSummaryResponse::from).toList(),
                result.page(),
                result.size(),
                result.totalElements(),
                result.totalPages(),
                result.last());
    }

    public record RequestSummaryResponse(
            Long requestId,
            String status,
            String budgetTier,
            String fulfillmentType,
            LocalDate fulfillmentDate,
            LocalDateTime expiresAt,
            int draftProposalCount,
            int submittedProposalCount
    ) {
        public static RequestSummaryResponse from(RequestSummaryResult r) {
            return new RequestSummaryResponse(
                    r.requestId(),
                    r.status().name(),
                    r.budgetTier().name(),
                    r.fulfillmentType().name(),
                    r.fulfillmentDate(),
                    r.expiresAt(),
                    r.draftProposalCount(),
                    r.submittedProposalCount());
        }
    }
}
