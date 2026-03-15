package com.florent.domain.reservation;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository {
    Reservation save(Reservation reservation);
    Optional<Reservation> findById(Long id);
    boolean existsByRequestId(Long requestId);
    List<Reservation> findAllByBuyerId(Long buyerId);
    List<Reservation> findAllBySellerId(Long sellerId);
}
