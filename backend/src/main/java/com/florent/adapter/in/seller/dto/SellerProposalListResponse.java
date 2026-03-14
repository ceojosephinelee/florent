package com.florent.adapter.in.seller.dto;

import com.florent.domain.proposal.SellerProposalListResult;
import com.florent.domain.proposal.SellerProposalSummaryResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record SellerProposalListResponse(
        List<ProposalSummaryItem> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean last
) {
    public record ProposalSummaryItem(
            Long proposalId,
            Long requestId,
            String status,
            String conceptTitle,
            BigDecimal price,
            LocalDateTime expiresAt
    ) {
        public static ProposalSummaryItem from(SellerProposalSummaryResult r) {
            return new ProposalSummaryItem(
                    r.proposalId(), r.requestId(),
                    r.status().name(), r.conceptTitle(),
                    r.price(), r.expiresAt());
        }
    }

    public static SellerProposalListResponse from(SellerProposalListResult result) {
        return new SellerProposalListResponse(
                result.content().stream()
                        .map(ProposalSummaryItem::from)
                        .toList(),
                result.page(), result.size(),
                result.totalElements(), result.totalPages(),
                result.last());
    }
}
