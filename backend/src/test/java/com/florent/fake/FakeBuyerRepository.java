package com.florent.fake;

import com.florent.domain.buyer.Buyer;
import com.florent.domain.buyer.BuyerRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class FakeBuyerRepository implements BuyerRepository {

    private final Map<Long, Buyer> store = new HashMap<>();

    public void save(Buyer buyer) {
        store.put(buyer.getId(), buyer);
    }

    @Override
    public Optional<Buyer> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Buyer> findAllByIds(List<Long> ids) {
        return ids.stream()
                .map(store::get)
                .filter(Objects::nonNull)
                .toList();
    }
}
