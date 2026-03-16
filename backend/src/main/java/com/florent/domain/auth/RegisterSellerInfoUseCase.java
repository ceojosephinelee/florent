package com.florent.domain.auth;

public interface RegisterSellerInfoUseCase {
    RegisterSellerInfoResult register(Long sellerId, RegisterSellerInfoCommand command);
}
