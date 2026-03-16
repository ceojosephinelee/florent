package com.florent.domain.seller;

public interface GetSellerStatsUseCase {
    SellerStatsResult getStats(Long sellerId);
}
