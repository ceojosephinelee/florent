package com.florent.domain.request;

import java.util.Optional;

public interface CurationRequestRepository {
    CurationRequest save(CurationRequest request);
    Optional<CurationRequest> findById(Long id);
    RequestPage findByBuyerId(Long buyerId, int page, int size);
}
