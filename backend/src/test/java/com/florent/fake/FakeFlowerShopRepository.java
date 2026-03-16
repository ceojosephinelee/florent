package com.florent.fake;

import com.florent.domain.shop.FlowerShop;
import com.florent.domain.shop.FlowerShopRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class FakeFlowerShopRepository implements FlowerShopRepository {

    private final Map<Long, FlowerShop> store = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public FlowerShop save(FlowerShop shop) {
        Long id = shop.getId();
        if (id == null) {
            id = idGenerator.getAndIncrement();
        }
        FlowerShop persisted = FlowerShop.reconstitute(
                id, shop.getSellerId(), shop.getShopName(), shop.getShopPhone(),
                shop.getShopAddress(), shop.getShopLat(), shop.getShopLng());
        store.put(id, persisted);
        return persisted;
    }

    @Override
    public Optional<FlowerShop> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<FlowerShop> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public List<FlowerShop> findAllByIds(List<Long> ids) {
        return ids.stream()
                .map(store::get)
                .filter(java.util.Objects::nonNull)
                .toList();
    }

    @Override
    public Optional<FlowerShop> findBySellerId(Long sellerId) {
        return store.values().stream()
                .filter(shop -> shop.getSellerId().equals(sellerId))
                .findFirst();
    }
}
