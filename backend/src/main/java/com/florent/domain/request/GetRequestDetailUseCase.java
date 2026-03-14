package com.florent.domain.request;

public interface GetRequestDetailUseCase {
    RequestDetailResult getDetail(Long requestId, Long buyerId);
}
