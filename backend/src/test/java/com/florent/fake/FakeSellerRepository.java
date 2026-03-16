package com.florent.fake;

import com.florent.domain.seller.Seller;
import com.florent.domain.seller.SellerRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class FakeSellerRepository implements SellerRepository {

    private final Map<Long, Seller> store = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Seller save(Seller seller) {
        Long id = seller.getId();
        if (id == null) {
            id = idGenerator.getAndIncrement();
        }
        Seller persisted = Seller.reconstitute(id, seller.getUserId());
        store.put(id, persisted);
        return persisted;
    }

    @Override
    public Optional<Seller> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Optional<Seller> findByUserId(Long userId) {
        return store.values().stream()
                .filter(s -> s.getUserId().equals(userId))
                .findFirst();
    }
}
