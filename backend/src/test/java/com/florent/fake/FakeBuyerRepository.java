package com.florent.fake;

import com.florent.domain.buyer.Buyer;
import com.florent.domain.buyer.BuyerRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class FakeBuyerRepository implements BuyerRepository {

    private final Map<Long, Buyer> store = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Buyer save(Buyer buyer) {
        Long id = buyer.getId();
        if (id == null) {
            id = idGenerator.getAndIncrement();
        }
        Buyer persisted = Buyer.reconstitute(id, buyer.getUserId(), buyer.getNickName());
        store.put(id, persisted);
        return persisted;
    }

    @Override
    public Optional<Buyer> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Optional<Buyer> findByUserId(Long userId) {
        return store.values().stream()
                .filter(b -> b.getUserId().equals(userId))
                .findFirst();
    }

    @Override
    public List<Buyer> findAllByIds(List<Long> ids) {
        return ids.stream()
                .map(store::get)
                .filter(Objects::nonNull)
                .toList();
    }
}
