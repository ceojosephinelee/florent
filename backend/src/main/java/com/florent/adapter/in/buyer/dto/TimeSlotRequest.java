package com.florent.adapter.in.buyer.dto;

import com.florent.domain.request.SlotKind;
import com.florent.domain.request.TimeSlot;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TimeSlotRequest(
    @NotNull SlotKind kind,
    @NotBlank String value
) {
    public TimeSlot toDomain() {
        return new TimeSlot(kind, value);
    }
}
