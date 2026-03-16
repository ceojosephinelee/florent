package com.florent.domain.shop;

import java.util.List;
import java.util.Optional;

public interface FlowerShopRepository {
    FlowerShop save(FlowerShop shop);
    Optional<FlowerShop> findById(Long id);
    List<FlowerShop> findAll();
    List<FlowerShop> findAllByIds(List<Long> ids);
    Optional<FlowerShop> findBySellerId(Long sellerId);
}
