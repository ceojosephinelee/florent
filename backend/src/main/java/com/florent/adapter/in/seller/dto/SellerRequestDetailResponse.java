package com.florent.adapter.in.seller.dto;

import com.florent.domain.request.SellerRequestDetailResult;
import com.florent.domain.request.TimeSlot;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record SellerRequestDetailResponse(
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
        LocalDateTime expiresAt,
        Long myProposalId,
        String myProposalStatus
) {
    public static SellerRequestDetailResponse from(SellerRequestDetailResult r) {
        return new SellerRequestDetailResponse(
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
                r.expiresAt(),
                r.myProposalId(),
                r.myProposalStatus());
    }

    public record TimeSlotDto(String kind, String value) {
        public static TimeSlotDto from(TimeSlot slot) {
            return new TimeSlotDto(slot.kind().name(), slot.value());
        }
    }
}
