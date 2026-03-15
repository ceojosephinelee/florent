package com.florent.adapter.out.persistence.payment;

import com.florent.domain.payment.Payment;
import com.florent.domain.payment.PaymentProvider;
import com.florent.domain.payment.PaymentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PaymentJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long reservationId;

    @Column(nullable = false, length = 20)
    private String provider;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false, length = 10)
    private String currency;

    @Column(nullable = false, unique = true)
    private String idempotencyKey;

    private LocalDateTime paidAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    public static PaymentJpaEntity from(Payment domain) {
        PaymentJpaEntity entity = new PaymentJpaEntity();
        entity.id = domain.getId();
        entity.reservationId = domain.getReservationId();
        entity.provider = domain.getProvider().name();
        entity.status = domain.getStatus().name();
        entity.amount = domain.getAmount();
        entity.currency = domain.getCurrency();
        entity.idempotencyKey = domain.getIdempotencyKey();
        entity.paidAt = domain.getPaidAt();
        entity.createdAt = domain.getCreatedAt();
        return entity;
    }

    public Payment toDomain() {
        return Payment.reconstitute(
                id, reservationId,
                PaymentProvider.valueOf(provider),
                PaymentStatus.valueOf(status),
                amount, currency, idempotencyKey,
                paidAt, createdAt
        );
    }
}
