package com.florent.adapter.out.persistence.payment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentJpaRepository extends JpaRepository<PaymentJpaEntity, Long> {
    Optional<PaymentJpaEntity> findByReservationId(Long reservationId);
    boolean existsByIdempotencyKey(String idempotencyKey);

    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query(
            "DELETE FROM PaymentJpaEntity p WHERE p.reservationId IN :reservationIds")
    void deleteByReservationIds(
            @org.springframework.data.repository.query.Param("reservationIds") java.util.List<Long> reservationIds);
}
