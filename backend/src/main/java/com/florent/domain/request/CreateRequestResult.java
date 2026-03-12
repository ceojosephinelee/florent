package com.florent.domain.request;

import java.time.LocalDateTime;

public record CreateRequestResult(
    Long requestId,
    String status,
    LocalDateTime expiresAt
) {
    public static CreateRequestResult from(CurationRequest request) {
        return new CreateRequestResult(
            request.getId(),
            request.getStatus().name(),
            request.getExpiresAt()
        );
    }
}
