package com.florent.adapter.out.payment;

import com.florent.domain.payment.Payment;
import com.florent.domain.payment.PaymentPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Clock;

@Slf4j
@Component
public class MockPaymentAdapter implements PaymentPort {

    @Override
    public Payment pay(Long reservationId, BigDecimal amount, String idempotencyKey, Clock clock) {
        log.info("[MockPayment] reservationId={}, amount={}, idempotencyKey={}",
                reservationId, amount, idempotencyKey);
        return Payment.createSucceeded(reservationId, amount, idempotencyKey, clock);
    }
}
