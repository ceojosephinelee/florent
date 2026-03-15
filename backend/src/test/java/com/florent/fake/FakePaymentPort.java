package com.florent.fake;

import com.florent.domain.payment.Payment;
import com.florent.domain.payment.PaymentPort;

import java.math.BigDecimal;
import java.time.Clock;

public class FakePaymentPort implements PaymentPort {

    @Override
    public Payment pay(Long reservationId, BigDecimal amount, String idempotencyKey, Clock clock) {
        return Payment.createSucceeded(reservationId, amount, idempotencyKey, clock);
    }
}
