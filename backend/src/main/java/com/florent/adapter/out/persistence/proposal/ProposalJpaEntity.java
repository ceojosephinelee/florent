package com.florent.adapter.out.persistence.proposal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "proposal")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ProposalJpaEntity {

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
}
