package com.florent.adapter.out.persistence.proposal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.florent.domain.proposal.Proposal;
import com.florent.domain.proposal.ProposalStatus;
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
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "proposal")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ProposalJpaEntity {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long requestId;

    @Column(nullable = false)
    private Long flowerShopId;

    @Column(nullable = false, length = 20)
    private String status;

    private String conceptTitle;

    @Column(columnDefinition = "TEXT")
    private String moodColorJson;

    @Column(columnDefinition = "TEXT")
    private String mainFlowersJson;

    @Column(columnDefinition = "TEXT")
    private String wrappingStyleJson;

    private String allergyNote;

    @Column(columnDefinition = "TEXT")
    private String careTips;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String imageUrlsJson;

    @Column(nullable = false, length = 30)
    private String availableSlotKind;

    @Column(nullable = false, length = 30)
    private String availableSlotValue;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    private LocalDateTime submittedAt;

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

    public static ProposalJpaEntity from(Proposal domain) {
        ProposalJpaEntity entity = new ProposalJpaEntity();
        entity.id = domain.getId();
        entity.requestId = domain.getRequestId();
        entity.flowerShopId = domain.getFlowerShopId();
        entity.status = domain.getStatus().name();
        entity.conceptTitle = domain.getConceptTitle();
        entity.moodColorJson = toJson(domain.getMoodColors());
        entity.mainFlowersJson = toJson(domain.getMainFlowers());
        entity.wrappingStyleJson = toJson(domain.getWrappingStyle());
        entity.allergyNote = domain.getAllergyNote();
        entity.careTips = domain.getCareTips();
        entity.description = domain.getDescription();
        entity.imageUrlsJson = toJson(domain.getImageUrls());
        entity.availableSlotKind = domain.getAvailableSlotKind();
        entity.availableSlotValue = domain.getAvailableSlotValue();
        entity.price = domain.getPrice();
        entity.createdAt = domain.getCreatedAt();
        entity.expiresAt = domain.getExpiresAt();
        entity.submittedAt = domain.getSubmittedAt();
        return entity;
    }

    public Proposal toDomain() {
        return Proposal.reconstitute(
                id, requestId, flowerShopId, ProposalStatus.valueOf(status),
                conceptTitle, fromJson(moodColorJson), fromJson(mainFlowersJson),
                fromJson(wrappingStyleJson), allergyNote, careTips,
                description, fromJson(imageUrlsJson),
                availableSlotKind, availableSlotValue,
                price, createdAt, expiresAt, submittedAt
        );
    }

    private static String toJson(List<String> list) {
        if (list == null) {
            return null;
        }
        try {
            return MAPPER.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("List<String> → JSON 변환 실패", e);
        }
    }

    private static List<String> fromJson(String json) {
        if (json == null) {
            return null;
        }
        try {
            return MAPPER.readValue(json, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("JSON → List<String> 변환 실패", e);
        }
    }
}
