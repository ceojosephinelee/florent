package com.florent.domain.reservation;

import java.util.List;

public interface GetBuyerReservationListUseCase {
    List<BuyerReservationSummaryResult> getList(Long buyerId);
}
