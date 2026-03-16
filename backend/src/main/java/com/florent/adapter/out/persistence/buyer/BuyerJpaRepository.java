package com.florent.adapter.out.persistence.buyer;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BuyerJpaRepository extends JpaRepository<BuyerJpaEntity, Long> {
    Optional<BuyerJpaEntity> findByUserId(Long userId);
}
