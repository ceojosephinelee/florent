package com.florent.domain.request;

import java.util.Optional;

public interface CurationRequestRepository {
    CurationRequest save(CurationRequest request);
    Optional<CurationRequest> findById(Long id);
}
