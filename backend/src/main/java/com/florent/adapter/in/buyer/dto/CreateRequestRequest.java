package com.florent.adapter.in.buyer.dto;

import com.florent.domain.request.BudgetTier;
import com.florent.domain.request.CreateRequestCommand;
import com.florent.domain.request.FulfillmentType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record CreateRequestRequest(
    @NotEmpty List<String> purposeTags,
    @NotEmpty List<String> relationTags,
    @NotEmpty List<String> moodTags,
    @NotNull BudgetTier budgetTier,
    @NotNull FulfillmentType fulfillmentType,
    @NotNull LocalDate fulfillmentDate,
    @NotEmpty @Valid List<TimeSlotRequest> requestedTimeSlots,
    @NotBlank String placeAddressText,
    @NotNull BigDecimal placeLat,
    @NotNull BigDecimal placeLng
) {
    public CreateRequestCommand toCommand(Long buyerId) {
        return new CreateRequestCommand(
            buyerId,
            purposeTags,
            relationTags,
            moodTags,
            budgetTier,
            fulfillmentType,
            fulfillmentDate,
            requestedTimeSlots.stream().map(TimeSlotRequest::toDomain).toList(),
            placeAddressText,
            placeLat,
            placeLng
        );
    }
}
