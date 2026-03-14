package com.florent.adapter.in.buyer.dto;

import com.florent.domain.request.RequestDetailResult;
import com.florent.domain.request.TimeSlot;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record RequestDetailResponse(
        Long requestId,
        String status,
        List<String> purposeTags,
        List<String> relationTags,
        List<String> moodTags,
        String budgetTier,
        String fulfillmentType,
        LocalDate fulfillmentDate,
        List<TimeSlotDto> requestedTimeSlots,
        String placeAddressText,
        BigDecimal placeLat,
        BigDecimal placeLng,
        LocalDateTime expiresAt,
        int draftProposalCount,
        int submittedProposalCount
) {
    public static RequestDetailResponse from(RequestDetailResult r) {
        return new RequestDetailResponse(
                r.requestId(),
                r.status().name(),
                r.purposeTags(),
                r.relationTags(),
                r.moodTags(),
                r.budgetTier().name(),
                r.fulfillmentType().name(),
                r.fulfillmentDate(),
                r.requestedTimeSlots().stream().map(TimeSlotDto::from).toList(),
                r.placeAddressText(),
                r.placeLat(),
                r.placeLng(),
                r.expiresAt(),
                r.draftProposalCount(),
                r.submittedProposalCount());
    }

    public record TimeSlotDto(String kind, String value) {
        public static TimeSlotDto from(TimeSlot slot) {
            return new TimeSlotDto(slot.kind().name(), slot.value());
        }
    }
}
