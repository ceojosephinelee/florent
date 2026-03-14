package com.florent.domain.request;

public interface GetRequestListUseCase {
    RequestListResult getList(Long buyerId, int page, int size);
}
