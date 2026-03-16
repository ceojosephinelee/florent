package com.florent.adapter.in.auth.dto;

import com.florent.domain.auth.RegisterSellerInfoResult;

public record RegisterSellerInfoResponse(Long sellerId, String shopName) {
    public static RegisterSellerInfoResponse from(RegisterSellerInfoResult result) {
        return new RegisterSellerInfoResponse(result.sellerId(), result.shopName());
    }
}
