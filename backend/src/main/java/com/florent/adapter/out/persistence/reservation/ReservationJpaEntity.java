package com.florent.adapter.out.persistence.reservation;

import com.florent.domain.reservation.Reservation;
import com.florent.domain.reservation.ReservationStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservation")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ReservationJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long requestId;

    @Column(nullable = false, unique = true)
    private Long proposalId;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(nullable = false, length = 20)
    private String fulfillmentType;

    @Column(nullable = false)
    private LocalDate fulfillmentDate;

    @Column(nullable = false, length = 30)
    private String fulfillmentSlotKind;

    @Column(nullable = false, length = 30)
    private String fulfillmentSlotValue;

    @Column(nullable = false)
    private String placeAddressText;

    @Column(nullable = false, precision = 9, scale = 6)
    private BigDecimal placeLat;

    @Column(nullable = false, precision = 9, scale = 6)
    private BigDecimal placeLng;

    @Column(nullable = false)
    private LocalDateTime confirmedAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public static ReservationJpaEntity from(Reservation domain) {
        ReservationJpaEntity entity = new ReservationJpaEntity();
        entity.id = domain.getId();
        entity.requestId = domain.getRequestId();
        entity.proposalId = domain.getProposalId();
        entity.status = domain.getStatus().name();
        entity.fulfillmentType = domain.getFulfillmentType();
        entity.fulfillmentDate = domain.getFulfillmentDate();
        entity.fulfillmentSlotKind = domain.getFulfillmentSlotKind();
        entity.fulfillmentSlotValue = domain.getFulfillmentSlotValue();
        entity.placeAddressText = domain.getPlaceAddressText();
        entity.placeLat = domain.getPlaceLat();
        entity.placeLng = domain.getPlaceLng();
        entity.confirmedAt = domain.getConfirmedAt();
        entity.createdAt = domain.getCreatedAt();
        return entity;
    }

    public Reservation toDomain() {
        return Reservation.reconstitute(
                id, requestId, proposalId, ReservationStatus.valueOf(status),
                fulfillmentType, fulfillmentDate,
                fulfillmentSlotKind, fulfillmentSlotValue,
                placeAddressText, placeLat, placeLng,
                confirmedAt, createdAt
        );
    }
}
