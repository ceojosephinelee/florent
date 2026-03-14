package com.florent.domain.proposal;

public record SaveProposalResult(Long proposalId, ProposalStatus status) {
    public static SaveProposalResult from(Proposal proposal) {
        return new SaveProposalResult(proposal.getId(), proposal.getStatus());
    }
}
