package com.florent.adapter.in.buyer.dto;

import com.florent.domain.proposal.ProposalSummary;

import java.time.LocalDateTime;

public record ProposalSummaryResponse(
        Long proposalId,
        String shopName,
        String conceptTitle,
        String status,
        LocalDateTime expiresAt
) {
    public static ProposalSummaryResponse from(ProposalSummary summary) {
        return new ProposalSummaryResponse(
                summary.proposalId(),
                summary.shopName(),
                summary.conceptTitle(),
                summary.status().name(),
                summary.expiresAt()
        );
    }
}
