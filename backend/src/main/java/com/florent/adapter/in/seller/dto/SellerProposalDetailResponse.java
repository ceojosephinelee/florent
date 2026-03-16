package com.florent.adapter.in.seller.dto;

import com.florent.domain.proposal.ProposalDetail;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record SellerProposalDetailResponse(
        Long proposalId,
        Long requestId,
        String status,
        String shopName,
        String conceptTitle,
        List<String> moodColors,
        List<String> mainFlowers,
        List<String> wrappingStyle,
        String allergyNote,
        String careTips,
        String description,
        List<String> imageUrls,
        AvailableSlot availableSlot,
        LocalDateTime expiresAt,
        LocalDateTime createdAt,
        BigDecimal price
) {
    public record AvailableSlot(String kind, String value) {}

    public static SellerProposalDetailResponse from(ProposalDetail detail) {
        return new SellerProposalDetailResponse(
                detail.proposalId(),
                detail.requestId(),
                detail.status().name(),
                detail.shopName(),
                detail.conceptTitle(),
                detail.moodColors(),
                detail.mainFlowers(),
                detail.wrappingStyle(),
                detail.allergyNote(),
                detail.careTips(),
                detail.description(),
                detail.imageUrls(),
                new AvailableSlot(detail.availableSlotKind(), detail.availableSlotValue()),
                detail.expiresAt(),
                null,
                detail.price()
        );
    }
}
