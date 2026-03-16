package com.florent.adapter.out.persistence.seller;

import com.florent.domain.seller.Seller;
import com.florent.domain.seller.SellerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SellerRepositoryImpl implements SellerRepository {

    private final SellerJpaRepository jpaRepository;

    @Override
    public Seller save(Seller seller) {
        SellerJpaEntity entity = SellerJpaEntity.from(seller);
        return jpaRepository.save(entity).toDomain();
    }

    @Override
    public Optional<Seller> findById(Long id) {
        return jpaRepository.findById(id)
                .map(SellerJpaEntity::toDomain);
    }

    @Override
    public Optional<Seller> findByUserId(Long userId) {
        return jpaRepository.findByUserId(userId)
                .map(SellerJpaEntity::toDomain);
    }
}
