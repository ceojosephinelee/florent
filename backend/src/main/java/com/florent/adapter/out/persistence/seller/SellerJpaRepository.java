package com.florent.adapter.out.persistence.seller;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SellerJpaRepository extends JpaRepository<SellerJpaEntity, Long> {
    Optional<SellerJpaEntity> findByUserId(Long userId);
}