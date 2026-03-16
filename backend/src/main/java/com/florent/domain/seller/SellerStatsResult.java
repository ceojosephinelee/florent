package com.florent.domain.seller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record SellerStatsResult(
        int monthlyReceivedRequestCount,
        int monthlySubmittedProposalCount,
        int monthlyConfirmedReservationCount,
        List<RecentReservationItem> recentReservations
) {
    public record RecentReservationItem(
            Long reservationId,
            String conceptTitle,
            BigDecimal price,
            String fulfillmentType,
            LocalDateTime confirmedAt
    ) {}
}
