package com.florent.adapter.in.seller.dto;

import com.florent.domain.proposal.SubmitProposalResult;

import java.time.LocalDateTime;

public record SubmitProposalResponse(
        Long proposalId,
        String status,
        LocalDateTime submittedAt
) {
    public static SubmitProposalResponse from(SubmitProposalResult result) {
        return new SubmitProposalResponse(
                result.proposalId(),
                result.status().name(),
                result.submittedAt());
    }
}
