package com.florent.domain.shop;

public interface RegisterShopUseCase {
    RegisterShopResult register(Long sellerId, RegisterShopCommand command);
}
