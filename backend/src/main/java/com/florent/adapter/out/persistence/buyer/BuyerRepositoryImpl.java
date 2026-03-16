package com.florent.adapter.out.persistence.buyer;

import com.florent.domain.buyer.Buyer;
import com.florent.domain.buyer.BuyerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BuyerRepositoryImpl implements BuyerRepository {

    private final BuyerJpaRepository jpaRepository;

    @Override
    public Buyer save(Buyer buyer) {
        BuyerJpaEntity entity = BuyerJpaEntity.from(buyer);
        return jpaRepository.save(entity).toDomain();
    }

    @Override
    public Optional<Buyer> findById(Long id) {
        return jpaRepository.findById(id)
                .map(BuyerJpaEntity::toDomain);
    }

    @Override
    public Optional<Buyer> findByUserId(Long userId) {
        return jpaRepository.findByUserId(userId)
                .map(BuyerJpaEntity::toDomain);
    }

    @Override
    public List<Buyer> findAllByIds(List<Long> ids) {
        return jpaRepository.findAllById(ids).stream()
                .map(BuyerJpaEntity::toDomain)
                .toList();
    }
}
