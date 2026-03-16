package com.florent.domain.request;

public interface GetSellerRequestListUseCase {
    SellerRequestListResult getSellerRequests(Long sellerId, int page, int size);
}
