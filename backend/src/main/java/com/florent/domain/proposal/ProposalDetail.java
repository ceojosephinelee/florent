package com.florent.domain.proposal;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ProposalDetail(
        Long proposalId,
        Long requestId,
        ProposalStatus status,
        Long shopId,
        String shopName,
        String shopPhone,
        String shopAddressText,
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
        LocalDateTime expiresAt,
        BigDecimal price
) {}
