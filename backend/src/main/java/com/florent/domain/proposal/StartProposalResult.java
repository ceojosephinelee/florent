package com.florent.domain.proposal;

import java.time.LocalDateTime;

public record StartProposalResult(
        Long proposalId,
        ProposalStatus status,
        LocalDateTime expiresAt
) {
    public static StartProposalResult from(Proposal proposal) {
        return new StartProposalResult(
                proposal.getId(),
                proposal.getStatus(),
                proposal.getExpiresAt()
        );
    }
}
