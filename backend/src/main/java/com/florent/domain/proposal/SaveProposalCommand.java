package com.florent.domain.proposal;

import java.math.BigDecimal;
import java.util.List;

public record SaveProposalCommand(
        Long proposalId,
        Long sellerId,
        String conceptTitle,
        List<String> moodColors,
        List<String> mainFlowers,
        List<String> wrappingStyle,
        String allergyNote,
        String careTips,
        String description,
        List<String> imageUrls,
        String availableSlotKind,
        String availableSlotValue,
        BigDecimal price
) {}
