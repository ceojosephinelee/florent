package com.florent.adapter.in.buyer;

import com.florent.domain.buyer.BuyerProfileResult;

import java.time.LocalDateTime;

public record BuyerProfileResponse(
        Long buyerId,
        String nickName,
        String email,
        String role,
        LocalDateTime createdAt
) {
    public static BuyerProfileResponse from(BuyerProfileResult result) {
        return new BuyerProfileResponse(
                result.buyerId(),
                result.nickName(),
                result.email(),
                result.role(),
                result.createdAt()
        );
    }
}
