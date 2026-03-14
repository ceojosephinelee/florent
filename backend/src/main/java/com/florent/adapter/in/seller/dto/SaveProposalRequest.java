package com.florent.adapter.in.seller.dto;

import com.florent.domain.proposal.SaveProposalCommand;

import java.math.BigDecimal;
import java.util.List;

public record SaveProposalRequest(
        String conceptTitle,
        List<String> moodColors,
        List<String> mainFlowers,
        List<String> wrappingStyle,
        String allergyNote,
        String careTips,
        String description,
        List<String> imageUrls,
        AvailableSlotRequest availableSlot,
        BigDecimal price
) {
    public SaveProposalCommand toCommand(Long proposalId, Long sellerId) {
        return new SaveProposalCommand(
                proposalId, sellerId,
                conceptTitle, moodColors, mainFlowers, wrappingStyle,
                allergyNote, careTips, description, imageUrls,
                availableSlot != null ? availableSlot.kind() : null,
                availableSlot != null ? availableSlot.value() : null,
                price);
    }
}
