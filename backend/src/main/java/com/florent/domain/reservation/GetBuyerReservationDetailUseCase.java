package com.florent.domain.reservation;

public interface GetBuyerReservationDetailUseCase {
    BuyerReservationDetailResult getDetail(Long reservationId, Long buyerId);
}
