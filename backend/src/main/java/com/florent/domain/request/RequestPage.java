package com.florent.domain.request;

import java.util.List;

public record RequestPage(
        List<CurationRequest> content,
        long totalElements,
        int totalPages,
        boolean last
) {}
