package com.florent.adapter.in.seller.dto;

import com.florent.domain.proposal.SaveProposalResult;

public record SaveProposalResponse(Long proposalId, String status) {
    public static SaveProposalResponse from(SaveProposalResult result) {
        return new SaveProposalResponse(result.proposalId(), result.status().name());
    }
}
