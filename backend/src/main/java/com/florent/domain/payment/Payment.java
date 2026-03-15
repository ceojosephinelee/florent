package com.florent.domain.payment;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;

@Getter
public class Payment {
    private Long id;
    private Long reservationId;
    private PaymentProvider provider;
    private PaymentStatus status;
    private BigDecimal amount;
    private String currency;
    private String idempotencyKey;
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;

    private Payment() {}

    public static Payment createSucceeded(
            Long reservationId, BigDecimal amount,
            String idempotencyKey, Clock clock) {
        Payment p = new Payment();
        p.reservationId = reservationId;
        p.provider = PaymentProvider.MOCK;
        p.status = PaymentStatus.SUCCEEDED;
        p.amount = amount;
        p.currency = "KRW";
        p.idempotencyKey = idempotencyKey;
        p.paidAt = LocalDateTime.now(clock);
        p.createdAt = LocalDateTime.now(clock);
        return p;
    }

    public static Payment reconstitute(
            Long id, Long reservationId, PaymentProvider provider,
            PaymentStatus status, BigDecimal amount, String currency,
            String idempotencyKey, LocalDateTime paidAt, LocalDateTime createdAt) {
        Payment p = new Payment();
        p.id = id;
        p.reservationId = reservationId;
        p.provider = provider;
        p.status = status;
        p.amount = amount;
        p.currency = currency;
        p.idempotencyKey = idempotencyKey;
        p.paidAt = paidAt;
        p.createdAt = createdAt;
        return p;
    }
}
