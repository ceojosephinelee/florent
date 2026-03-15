package com.florent.domain.reservation;

public interface GetSellerReservationDetailUseCase {
    SellerReservationDetailResult getDetail(Long reservationId, Long sellerId);
}
