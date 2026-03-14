package com.florent.domain.request;

public record TimeSlot(
    SlotKind kind,
    String value
) {
    public TimeSlot {
        if (kind == null || value == null || value.isBlank()) {
            throw new IllegalArgumentException("TimeSlot의 kind와 value는 필수입니다.");
        }
    }
}
