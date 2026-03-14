package com.florent.fake;

import com.florent.domain.shop.FlowerShop;
import com.florent.domain.shop.FlowerShopRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class FakeFlowerShopRepository implements FlowerShopRepository {

    private final Map<Long, FlowerShop> store = new HashMap<>();

    public void save(FlowerShop shop) {
        store.put(shop.getId(), shop);
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
}
