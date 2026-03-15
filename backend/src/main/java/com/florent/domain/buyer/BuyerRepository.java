package com.florent.domain.buyer;

import java.util.List;
import java.util.Optional;

public interface BuyerRepository {
    Optional<Buyer> findById(Long id);
    List<Buyer> findAllByIds(List<Long> ids);
}
