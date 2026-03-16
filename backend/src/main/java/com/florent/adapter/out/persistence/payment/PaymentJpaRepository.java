package com.florent.adapter.out.persistence.payment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentJpaRepository extends JpaRepository<PaymentJpaEntity, Long> {
    Optional<PaymentJpaEntity> findByReservationId(Long reservationId);
    boolean existsByIdempotencyKey(String idempotencyKey);
}
