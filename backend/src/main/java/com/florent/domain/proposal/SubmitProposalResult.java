package com.florent.domain.proposal;

import java.time.LocalDateTime;

public record SubmitProposalResult(
        Long proposalId,
        ProposalStatus status,
        LocalDateTime submittedAt
) {
    public static SubmitProposalResult from(Proposal proposal) {
        return new SubmitProposalResult(
                proposal.getId(),
                proposal.getStatus(),
                proposal.getSubmittedAt()
        );
    }
}
