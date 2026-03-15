package com.florent.domain.proposal;

public interface GetSellerProposalDetailUseCase {
    ProposalDetail getSellerProposalDetail(Long proposalId, Long sellerId);
}
