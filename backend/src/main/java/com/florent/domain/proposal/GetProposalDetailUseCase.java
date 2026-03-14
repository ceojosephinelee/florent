package com.florent.domain.proposal;

public interface GetProposalDetailUseCase {
    ProposalDetail getProposalDetail(Long proposalId, Long buyerId);
}
