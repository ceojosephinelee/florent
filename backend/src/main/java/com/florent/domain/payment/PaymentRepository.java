package com.florent.domain.payment;

import java.util.Optional;

public interface PaymentRepository {
    Payment save(Payment payment);
    Optional<Payment> findByReservationId(Long reservationId);
    boolean existsByIdempotencyKey(String idempotencyKey);
}
