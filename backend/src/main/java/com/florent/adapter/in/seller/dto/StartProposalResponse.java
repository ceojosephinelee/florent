package com.florent.adapter.in.seller.dto;

import com.florent.domain.proposal.StartProposalResult;

import java.time.LocalDateTime;

public record StartProposalResponse(
        Long proposalId,
        String status,
        LocalDateTime expiresAt
) {
    public static StartProposalResponse from(StartProposalResult result) {
        return new StartProposalResponse(
                result.proposalId(),
                result.status().name(),
                result.expiresAt());
    }
}
