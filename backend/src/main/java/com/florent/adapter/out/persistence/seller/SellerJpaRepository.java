package com.florent.adapter.out.persistence.seller;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerJpaRepository extends JpaRepository<SellerJpaEntity, Long> {
}