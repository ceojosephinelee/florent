package com.florent.domain.seller;

public interface GetSellerProfileUseCase {
    SellerProfileResult getProfile(Long sellerId);
}
