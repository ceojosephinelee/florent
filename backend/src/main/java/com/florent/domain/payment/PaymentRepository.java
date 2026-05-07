package com.florent.domain.payment;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository {
    Payment save(Payment payment);
    Optional<Payment> findByReservationId(Long reservationId);
    boolean existsByIdempotencyKey(String idempotencyKey);
    void deleteByReservationIds(List<Long> reservationIds);
}
