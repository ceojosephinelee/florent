package com.florent.domain.proposal;

import java.util.List;

public record ProposalPage(
        List<Proposal> content,
        long totalElements,
        int totalPages,
        boolean last
) {}
