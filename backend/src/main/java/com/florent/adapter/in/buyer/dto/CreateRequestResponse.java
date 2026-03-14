package com.florent.adapter.in.buyer.dto;

import com.florent.domain.request.CreateRequestResult;

import java.time.LocalDateTime;

public record CreateRequestResponse(
    Long requestId,
    String status,
    LocalDateTime expiresAt
) {
    public static CreateRequestResponse from(CreateRequestResult result) {
        return new CreateRequestResponse(
            result.requestId(),
            result.status(),
            result.expiresAt()
        );
    }
}
