package com.florent.domain.proposal;

import com.florent.common.exception.BusinessException;
import com.florent.common.exception.ErrorCode;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class Proposal {
    private Long id;
    private Long requestId;
    private Long flowerShopId;
    private ProposalStatus status;
    private String conceptTitle;
    private List<String> moodColors;
    private List<String> mainFlowers;
    private List<String> wrappingStyle;
    private String allergyNote;
    private String careTips;
    private String description;
    private List<String> imageUrls;
    private String availableSlotKind;
    private String availableSlotValue;
    private BigDecimal price;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private LocalDateTime submittedAt;

    private Proposal() {}

    public static Proposal create(Long requestId, Long flowerShopId, Clock clock) {
        Proposal p = new Proposal();
        p.requestId = requestId;
        p.flowerShopId = flowerShopId;
        p.status = ProposalStatus.DRAFT;
        p.createdAt = LocalDateTime.now(clock);
        p.expiresAt = p.createdAt.plusHours(24);
        return p;
    }

    public static Proposal reconstitute(
            Long id, Long requestId, Long flowerShopId, ProposalStatus status,
            String conceptTitle, List<String> moodColors, List<String> mainFlowers,
            List<String> wrappingStyle, String allergyNote, String careTips,
            String description, List<String> imageUrls,
            String availableSlotKind, String availableSlotValue,
            BigDecimal price, LocalDateTime createdAt, LocalDateTime expiresAt,
            LocalDateTime submittedAt) {
        Proposal p = new Proposal();
        p.id = id;
        p.requestId = requestId;
        p.flowerShopId = flowerShopId;
        p.status = status;
        p.conceptTitle = conceptTitle;
        p.moodColors = moodColors;
        p.mainFlowers = mainFlowers;
        p.wrappingStyle = wrappingStyle;
        p.allergyNote = allergyNote;
        p.careTips = careTips;
        p.description = description;
        p.imageUrls = imageUrls;
        p.availableSlotKind = availableSlotKind;
        p.availableSlotValue = availableSlotValue;
        p.price = price;
        p.createdAt = createdAt;
        p.expiresAt = expiresAt;
        p.submittedAt = submittedAt;
        return p;
    }

    public void submit(Clock clock) {
        if (status != ProposalStatus.DRAFT) {
            throw new BusinessException(ErrorCode.PROPOSAL_NOT_SUBMITTABLE);
        }
        this.status = ProposalStatus.SUBMITTED;
        this.submittedAt = LocalDateTime.now(clock);
    }

    public void select() {
        if (status != ProposalStatus.SUBMITTED) {
            throw new BusinessException(ErrorCode.PROPOSAL_NOT_SELECTABLE);
        }
        this.status = ProposalStatus.SELECTED;
    }

    public void markNotSelected() {
        if (status != ProposalStatus.SUBMITTED) {
            throw new BusinessException(ErrorCode.PROPOSAL_NOT_SELECTABLE);
        }
        this.status = ProposalStatus.NOT_SELECTED;
    }

    public void expire() {
        if (status == ProposalStatus.SELECTED || status == ProposalStatus.NOT_SELECTED) {
            return;
        }
        this.status = ProposalStatus.EXPIRED;
    }

    public boolean isVisibleToBuyer() {
        return status == ProposalStatus.SUBMITTED || status == ProposalStatus.EXPIRED
            || status == ProposalStatus.SELECTED || status == ProposalStatus.NOT_SELECTED;
    }

    public boolean isExpired(Clock clock) {
        return status == ProposalStatus.EXPIRED
            || (status != ProposalStatus.SELECTED
                && status != ProposalStatus.NOT_SELECTED
                && LocalDateTime.now(clock).isAfter(expiresAt));
    }
}
