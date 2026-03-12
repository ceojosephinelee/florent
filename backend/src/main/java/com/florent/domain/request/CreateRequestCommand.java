package com.florent.domain.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record CreateRequestCommand(
    Long buyerId,
    List<String> purposeTags,
    List<String> relationTags,
    List<String> moodTags,
    BudgetTier budgetTier,
    FulfillmentType fulfillmentType,
    LocalDate fulfillmentDate,
    List<TimeSlot> requestedTimeSlots,
    String placeAddressText,
    BigDecimal placeLat,
    BigDecimal placeLng
) {}
