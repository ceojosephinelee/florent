package com.florent.adapter.out.persistence.notification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OutboxEventJpaRepository extends JpaRepository<OutboxEventJpaEntity, Long> {

    boolean existsByDedupKey(String dedupKey);

    @Query("SELECT e FROM OutboxEventJpaEntity e "
            + "WHERE e.status = 'PENDING' AND e.availableAt <= :now "
            + "ORDER BY e.createdAt ASC")
    List<OutboxEventJpaEntity> findPendingBefore(
            @Param("now") LocalDateTime now,
            org.springframework.data.domain.Pageable pageable);
}