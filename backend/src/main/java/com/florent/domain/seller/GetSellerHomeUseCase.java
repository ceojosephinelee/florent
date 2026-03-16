package com.florent.domain.seller;

public interface GetSellerHomeUseCase {
    SellerHomeResult getHome(Long sellerId);
}
