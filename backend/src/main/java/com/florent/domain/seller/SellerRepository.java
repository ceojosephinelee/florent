package com.florent.domain.seller;

import java.util.Optional;

public interface SellerRepository {
    Seller save(Seller seller);
    Optional<Seller> findById(Long id);
    Optional<Seller> findByUserId(Long userId);
}
