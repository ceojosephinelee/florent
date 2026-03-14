package com.florent.adapter.out.persistence.request;

import com.florent.domain.request.BudgetTier;
import com.florent.domain.request.CurationRequest;
import com.florent.domain.request.FulfillmentType;
import com.florent.domain.request.RequestStatus;
import com.florent.domain.request.TimeSlot;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import java.util.List;

@Entity
@Table(name = "curation_request")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class CurationRequestJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long buyerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status;

    @Convert(converter = StringListConverter.class)
    @Column(name = "purpose_tags_json", nullable = false)
    private List<String> purposeTags;

    @Convert(converter = StringListConverter.class)
    @Column(name = "relation_tags_json", nullable = false)
    private List<String> relationTags;

    @Convert(converter = StringListConverter.class)
    @Column(name = "mood_tags_json", nullable = false)
    private List<String> moodTags;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BudgetTier budgetTier;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FulfillmentType fulfillmentType;

    @Column(nullable = false)
    private LocalDate fulfillmentDate;

    @Convert(converter = TimeSlotListConverter.class)
    @Column(name = "requested_time_slots_json", nullable = false)
    private List<TimeSlot> requestedTimeSlots;

    @Column(nullable = false)
    private String placeAddressText;

    @Column(nullable = false, precision = 9, scale = 6)
    private BigDecimal placeLat;

    @Column(nullable = false, precision = 9, scale = 6)
    private BigDecimal placeLng;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

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

    public static CurationRequestJpaEntity from(CurationRequest domain) {
        CurationRequestJpaEntity entity = new CurationRequestJpaEntity();
        entity.id = domain.getId();
        entity.buyerId = domain.getBuyerId();
        entity.status = domain.getStatus();
        entity.purposeTags = domain.getPurposeTags();
        entity.relationTags = domain.getRelationTags();
        entity.moodTags = domain.getMoodTags();
        entity.budgetTier = domain.getBudgetTier();
        entity.fulfillmentType = domain.getFulfillmentType();
        entity.fulfillmentDate = domain.getFulfillmentDate();
        entity.requestedTimeSlots = domain.getRequestedTimeSlots();
        entity.placeAddressText = domain.getPlaceAddressText();
        entity.placeLat = domain.getPlaceLat();
        entity.placeLng = domain.getPlaceLng();
        entity.createdAt = domain.getCreatedAt();
        entity.expiresAt = domain.getExpiresAt();
        return entity;
    }

    public CurationRequest toDomain() {
        return CurationRequest.reconstitute(
                id, buyerId, status,
                purposeTags, relationTags, moodTags,
                budgetTier, fulfillmentType,
                fulfillmentDate, requestedTimeSlots,
                placeAddressText, placeLat, placeLng,
                createdAt, expiresAt
        );
    }
}
