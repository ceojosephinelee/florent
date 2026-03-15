package com.florent.adapter.out.persistence.reservation;

import com.florent.domain.reservation.Reservation;
import com.florent.domain.reservation.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ReservationRepositoryImpl implements ReservationRepository {

    private final ReservationJpaRepository jpaRepository;

    @Override
    public Reservation save(Reservation reservation) {
        ReservationJpaEntity entity = ReservationJpaEntity.from(reservation);
        ReservationJpaEntity saved = jpaRepository.save(entity);
        return saved.toDomain();
    }

    @Override
    public Optional<Reservation> findById(Long id) {
        return jpaRepository.findById(id)
                .map(ReservationJpaEntity::toDomain);
    }

    @Override
    public boolean existsByRequestId(Long requestId) {
        return jpaRepository.existsByRequestId(requestId);
    }

    @Override
    public List<Reservation> findAllByBuyerId(Long buyerId) {
        return jpaRepository.findAllByBuyerId(buyerId).stream()
                .map(ReservationJpaEntity::toDomain)
                .toList();
    }

    @Override
    public List<Reservation> findAllBySellerId(Long sellerId) {
        return jpaRepository.findAllBySellerId(sellerId).stream()
                .map(ReservationJpaEntity::toDomain)
                .toList();
    }
}
