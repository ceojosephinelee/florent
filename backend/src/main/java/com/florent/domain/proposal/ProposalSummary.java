package com.florent.domain.proposal;

import java.time.LocalDateTime;

public record ProposalSummary(
        Long proposalId,
        String shopName,
        String conceptTitle,
        ProposalStatus status,
        LocalDateTime expiresAt
) {}
