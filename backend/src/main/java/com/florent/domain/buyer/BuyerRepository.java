package com.florent.domain.buyer;

import java.util.List;
import java.util.Optional;

public interface BuyerRepository {
    Buyer save(Buyer buyer);
    Optional<Buyer> findById(Long id);
    Optional<Buyer> findByUserId(Long userId);
    List<Buyer> findAllByIds(List<Long> ids);
}
