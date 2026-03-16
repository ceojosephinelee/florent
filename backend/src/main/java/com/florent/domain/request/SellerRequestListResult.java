package com.florent.domain.request;

import java.util.List;

public record SellerRequestListResult(
        List<SellerRequestSummaryResult> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean last
) {}
