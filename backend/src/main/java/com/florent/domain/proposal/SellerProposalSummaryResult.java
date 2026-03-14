package com.florent.domain.proposal;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SellerProposalSummaryResult(
        Long proposalId,
        Long requestId,
        ProposalStatus status,
        String conceptTitle,
        BigDecimal price,
        LocalDateTime expiresAt
) {
    public static SellerProposalSummaryResult from(Proposal proposal) {
        return new SellerProposalSummaryResult(
                proposal.getId(),
                proposal.getRequestId(),
                proposal.getStatus(),
                proposal.getConceptTitle(),
                proposal.getPrice(),
                proposal.getExpiresAt()
        );
    }
}
