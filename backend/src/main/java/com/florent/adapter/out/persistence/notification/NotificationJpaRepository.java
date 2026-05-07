package com.florent.adapter.out.persistence.notification;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationJpaRepository extends JpaRepository<NotificationJpaEntity, Long> {
    Page<NotificationJpaEntity> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    long countByUserId(Long userId);

    @org.springframework.data.jpa.repository.Query("SELECT n.id FROM NotificationJpaEntity n WHERE n.userId = :userId")
    java.util.List<Long> findIdsByUserId(@org.springframework.data.repository.query.Param("userId") Long userId);

    void deleteByUserId(Long userId);
}