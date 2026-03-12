package com.florent.domain.shop;

import java.util.List;
import java.util.Optional;

public interface FlowerShopRepository {
    Optional<FlowerShop> findById(Long id);
    List<FlowerShop> findAll();
}
