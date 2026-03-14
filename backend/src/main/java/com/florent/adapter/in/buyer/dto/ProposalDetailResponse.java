package com.florent.adapter.in.buyer.dto;

import com.florent.domain.proposal.ProposalDetail;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ProposalDetailResponse(
        Long proposalId,
        Long requestId,
        String status,
        ShopInfo shop,
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
        BigDecimal price
) {
    public record ShopInfo(Long shopId, String name, String phone, String addressText) {}
    public record AvailableSlot(String kind, String value) {}

    public static ProposalDetailResponse from(ProposalDetail detail) {
        return new ProposalDetailResponse(
                detail.proposalId(),
                detail.requestId(),
                detail.status().name(),
                new ShopInfo(detail.shopId(), detail.shopName(), detail.shopPhone(), detail.shopAddressText()),
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
                detail.price()
        );
    }
}
