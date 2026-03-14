package com.florent.adapter.out.persistence.shop;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FlowerShopJpaRepository extends JpaRepository<FlowerShopJpaEntity, Long> {
    Optional<FlowerShopJpaEntity> findBySellerId(Long sellerId);
}
