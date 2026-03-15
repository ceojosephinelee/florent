package com.florent.fake;

import com.florent.domain.reservation.Reservation;
import com.florent.domain.reservation.ReservationRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;

public class FakeReservationRepository implements ReservationRepository {

    private final Map<Long, Reservation> store = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    private final FakeCurationRequestRepository requestRepository;
    private final FakeProposalRepository proposalRepository;
    private final FakeFlowerShopRepository shopRepository;

    public FakeReservationRepository(
            FakeCurationRequestRepository requestRepository,
            FakeProposalRepository proposalRepository,
            FakeFlowerShopRepository shopRepository) {
        this.requestRepository = requestRepository;
        this.proposalRepository = proposalRepository;
        this.shopRepository = shopRepository;
    }

    @Override
    public Reservation save(Reservation reservation) {
        Long id = reservation.getId();
        if (id == null) {
            id = idGenerator.getAndIncrement();
        }
        Reservation persisted = Reservation.reconstitute(
                id, reservation.getRequestId(), reservation.getProposalId(),
                reservation.getStatus(),
                reservation.getFulfillmentType(), reservation.getFulfillmentDate(),
                reservation.getFulfillmentSlotKind(), reservation.getFulfillmentSlotValue(),
                reservation.getPlaceAddressText(),
                reservation.getPlaceLat(), reservation.getPlaceLng(),
                reservation.getConfirmedAt(), reservation.getCreatedAt()
        );
        store.put(id, persisted);
        return persisted;
    }

    @Override
    public Optional<Reservation> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public boolean existsByRequestId(Long requestId) {
        return store.values().stream()
                .anyMatch(r -> r.getRequestId().equals(requestId));
    }

    @Override
    public List<Reservation> findAllByBuyerId(Long buyerId) {
        Predicate<Reservation> belongsToBuyer = r ->
                requestRepository.findById(r.getRequestId())
                        .map(req -> req.getBuyerId().equals(buyerId))
                        .orElse(false);
        return store.values().stream()
                .filter(belongsToBuyer)
                .toList();
    }

    @Override
    public List<Reservation> findAllBySellerId(Long sellerId) {
        Predicate<Reservation> belongsToSeller = r ->
                proposalRepository.findById(r.getProposalId())
                        .flatMap(p -> shopRepository.findById(p.getFlowerShopId()))
                        .map(shop -> shop.getSellerId().equals(sellerId))
                        .orElse(false);
        return store.values().stream()
                .filter(belongsToSeller)
                .toList();
    }
}
