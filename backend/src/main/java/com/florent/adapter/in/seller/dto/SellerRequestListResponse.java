package com.florent.adapter.in.seller.dto;

import com.florent.domain.request.SellerRequestListResult;
import com.florent.domain.request.SellerRequestSummaryResult;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record SellerRequestListResponse(
        List<SellerRequestSummaryResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean last
) {
    public static SellerRequestListResponse from(SellerRequestListResult result) {
        return new SellerRequestListResponse(
                result.content().stream().map(SellerRequestSummaryResponse::from).toList(),
                result.page(),
                result.size(),
                result.totalElements(),
                result.totalPages(),
                result.last());
    }

    public record SellerRequestSummaryResponse(
            Long requestId,
            String status,
            List<String> purposeTags,
            List<String> relationTags,
            List<String> moodTags,
            String budgetTier,
            String fulfillmentType,
            LocalDate fulfillmentDate,
            LocalDateTime expiresAt,
            String myProposalStatus
    ) {
        public static SellerRequestSummaryResponse from(SellerRequestSummaryResult r) {
            return new SellerRequestSummaryResponse(
                    r.requestId(),
                    r.status().name(),
                    r.purposeTags(),
                    r.relationTags(),
                    r.moodTags(),
                    r.budgetTier().name(),
                    r.fulfillmentType().name(),
                    r.fulfillmentDate(),
                    r.expiresAt(),
                    r.myProposalStatus());
        }
    }
}
