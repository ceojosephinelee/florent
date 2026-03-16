package com.florent.domain.request;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CurationRequestRepository {
    CurationRequest save(CurationRequest request);
    Optional<CurationRequest> findById(Long id);
    List<CurationRequest> findAllByIds(List<Long> ids);
    List<CurationRequest> findAll();
    RequestPage findByBuyerId(Long buyerId, int page, int size);
    List<CurationRequest> findOpenExpiredBefore(LocalDateTime now);
}
