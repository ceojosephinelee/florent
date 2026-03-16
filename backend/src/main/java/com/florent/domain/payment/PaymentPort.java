package com.florent.domain.payment;

import java.math.BigDecimal;
import java.time.Clock;

public interface PaymentPort {
    Payment pay(Long reservationId, BigDecimal amount, String idempotencyKey, Clock clock);
}
