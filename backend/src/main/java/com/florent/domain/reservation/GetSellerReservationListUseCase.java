package com.florent.domain.reservation;

import java.util.List;

public interface GetSellerReservationListUseCase {
    List<SellerReservationSummaryResult> getList(Long sellerId);
}
