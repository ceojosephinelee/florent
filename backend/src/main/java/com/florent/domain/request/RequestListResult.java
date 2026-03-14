package com.florent.domain.request;

import java.util.List;

public record RequestListResult(
        List<RequestSummaryResult> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean last
) {}
