package com.florent.domain.proposal;

import java.util.List;

public record SellerProposalListResult(
        List<SellerProposalSummaryResult> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean last
) {}
