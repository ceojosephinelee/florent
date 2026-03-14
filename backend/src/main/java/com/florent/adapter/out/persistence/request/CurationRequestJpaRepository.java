package com.florent.adapter.out.persistence.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurationRequestJpaRepository extends JpaRepository<CurationRequestJpaEntity, Long> {
    Page<CurationRequestJpaEntity> findByBuyerIdOrderByCreatedAtDesc(Long buyerId, Pageable pageable);
}
