package com.florent.adapter.in.seller;

import com.florent.domain.seller.SellerStatsResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record SellerStatsResponse(
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

    public static SellerStatsResponse from(SellerStatsResult result) {
        List<RecentReservationItem> items = result.recentReservations().stream()
                .map(r -> new RecentReservationItem(
                        r.reservationId(), r.conceptTitle(),
                        r.price(), r.fulfillmentType(), r.confirmedAt()))
                .toList();
        return new SellerStatsResponse(
                result.monthlyReceivedRequestCount(),
                result.monthlySubmittedProposalCount(),
                result.monthlyConfirmedReservationCount(),
                items
        );
    }
}
