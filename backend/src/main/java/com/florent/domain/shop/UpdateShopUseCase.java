package com.florent.domain.shop;

public interface UpdateShopUseCase {
    ShopDetailResult update(Long sellerId, UpdateShopCommand command);
}
