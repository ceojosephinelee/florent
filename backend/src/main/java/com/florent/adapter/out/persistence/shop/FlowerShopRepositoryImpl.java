package com.florent.adapter.out.persistence.shop;

import com.florent.domain.shop.FlowerShop;
import com.florent.domain.shop.FlowerShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class FlowerShopRepositoryImpl implements FlowerShopRepository {

    private final FlowerShopJpaRepository jpaRepository;

    @Override
    public Optional<FlowerShop> findById(Long id) {
        return jpaRepository.findById(id)
                .map(FlowerShopJpaEntity::toDomain);
    }

    @Override
    public List<FlowerShop> findAll() {
        return jpaRepository.findAll().stream()
                .map(FlowerShopJpaEntity::toDomain)
                .toList();
    }

    @Override
    public List<FlowerShop> findAllByIds(List<Long> ids) {
        return jpaRepository.findAllById(ids).stream()
                .map(FlowerShopJpaEntity::toDomain)
                .toList();
    }

    @Override
    public Optional<FlowerShop> findBySellerId(Long sellerId) {
        return jpaRepository.findBySellerId(sellerId)
                .map(FlowerShopJpaEntity::toDomain);
    }
}
