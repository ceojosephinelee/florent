package com.florent.domain.proposal;

public interface GetSellerProposalListUseCase {
    SellerProposalListResult getMyProposals(Long sellerId, int page, int size);
}
