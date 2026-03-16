package com.florent.domain.buyer;

public interface GetBuyerProfileUseCase {
    BuyerProfileResult getProfile(Long buyerId, Long userId);
}
