package com.florent.domain.request;

public interface GetSellerRequestDetailUseCase {
    SellerRequestDetailResult getSellerRequestDetail(Long requestId, Long sellerId);
}
