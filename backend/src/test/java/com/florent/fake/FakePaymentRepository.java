package com.florent.fake;

import com.florent.domain.payment.Payment;
import com.florent.domain.payment.PaymentRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class FakePaymentRepository implements PaymentRepository {

    private final Map<Long, Payment> store = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Payment save(Payment payment) {
        Long id = payment.getId();
        if (id == null) {
            id = idGenerator.getAndIncrement();
        }
        Payment persisted = Payment.reconstitute(
                id, payment.getReservationId(), payment.getProvider(),
                payment.getStatus(), payment.getAmount(), payment.getCurrency(),
                payment.getIdempotencyKey(), payment.getPaidAt(), payment.getCreatedAt()
        );
        store.put(id, persisted);
        return persisted;
    }

    @Override
    public Optional<Payment> findByReservationId(Long reservationId) {
        return store.values().stream()
                .filter(p -> p.getReservationId().equals(reservationId))
                .findFirst();
    }

    @Override
    public boolean existsByIdempotencyKey(String idempotencyKey) {
        return store.values().stream()
                .anyMatch(p -> p.getIdempotencyKey().equals(idempotencyKey));
    }
}
