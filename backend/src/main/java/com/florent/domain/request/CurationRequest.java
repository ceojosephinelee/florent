package com.florent.domain.request;

import com.florent.common.exception.BusinessException;
import com.florent.common.exception.ErrorCode;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class CurationRequest {
    private Long id;
    private Long buyerId;
    private RequestStatus status;
    private List<String> purposeTags;
    private List<String> relationTags;
    private List<String> moodTags;
    private BudgetTier budgetTier;
    private FulfillmentType fulfillmentType;
    private LocalDate fulfillmentDate;
    private List<TimeSlot> requestedTimeSlots;
    private String placeAddressText;
    private BigDecimal placeLat;
    private BigDecimal placeLng;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;

    private CurationRequest() {}

    public static CurationRequest create(CreateRequestCommand cmd) {
        CurationRequest r = new CurationRequest();
        r.buyerId = cmd.buyerId();
        r.status = RequestStatus.OPEN;
        r.purposeTags = List.copyOf(cmd.purposeTags());
        r.relationTags = List.copyOf(cmd.relationTags());
        r.moodTags = List.copyOf(cmd.moodTags());
        r.budgetTier = cmd.budgetTier();
        r.fulfillmentType = cmd.fulfillmentType();
        r.fulfillmentDate = cmd.fulfillmentDate();
        r.requestedTimeSlots = List.copyOf(cmd.requestedTimeSlots());
        r.placeAddressText = cmd.placeAddressText();
        r.placeLat = cmd.placeLat();
        r.placeLng = cmd.placeLng();
        r.createdAt = LocalDateTime.now();
        r.expiresAt = r.createdAt.plusHours(48);
        return r;
    }

    public static CurationRequest reconstitute(
            Long id, Long buyerId, RequestStatus status,
            List<String> purposeTags, List<String> relationTags, List<String> moodTags,
            BudgetTier budgetTier, FulfillmentType fulfillmentType,
            LocalDate fulfillmentDate, List<TimeSlot> requestedTimeSlots,
            String placeAddressText, BigDecimal placeLat, BigDecimal placeLng,
            LocalDateTime createdAt, LocalDateTime expiresAt) {
        CurationRequest r = new CurationRequest();
        r.id = id;
        r.buyerId = buyerId;
        r.status = status;
        r.purposeTags = purposeTags;
        r.relationTags = relationTags;
        r.moodTags = moodTags;
        r.budgetTier = budgetTier;
        r.fulfillmentType = fulfillmentType;
        r.fulfillmentDate = fulfillmentDate;
        r.requestedTimeSlots = requestedTimeSlots;
        r.placeAddressText = placeAddressText;
        r.placeLat = placeLat;
        r.placeLng = placeLng;
        r.createdAt = createdAt;
        r.expiresAt = expiresAt;
        return r;
    }

    public void confirm() {
        if (status != RequestStatus.OPEN) {
            throw new BusinessException(ErrorCode.REQUEST_NOT_OPEN);
        }
        this.status = RequestStatus.CONFIRMED;
    }

    public void expire() {
        if (status != RequestStatus.OPEN) {
            throw new BusinessException(ErrorCode.REQUEST_NOT_OPEN);
        }
        this.status = RequestStatus.EXPIRED;
    }

    public boolean isExpired() {
        return status == RequestStatus.EXPIRED
            || (status == RequestStatus.OPEN && LocalDateTime.now().isAfter(expiresAt));
    }
}
