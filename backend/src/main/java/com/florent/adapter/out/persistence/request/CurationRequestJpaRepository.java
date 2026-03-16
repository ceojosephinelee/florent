package com.florent.adapter.out.persistence.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface CurationRequestJpaRepository extends JpaRepository<CurationRequestJpaEntity, Long> {
    Page<CurationRequestJpaEntity> findByBuyerIdOrderByCreatedAtDesc(Long buyerId, Pageable pageable);

    @Query("SELECT r FROM CurationRequestJpaEntity r "
            + "WHERE r.status = 'OPEN' AND r.expiresAt < :now")
    List<CurationRequestJpaEntity> findOpenExpiredBefore(@Param("now") LocalDateTime now);
}
