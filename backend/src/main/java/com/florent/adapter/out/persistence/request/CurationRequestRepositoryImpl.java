package com.florent.adapter.out.persistence.request;

import com.florent.domain.request.CurationRequest;
import com.florent.domain.request.CurationRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CurationRequestRepositoryImpl implements CurationRequestRepository {

    private final CurationRequestJpaRepository jpaRepository;

    @Override
    public CurationRequest save(CurationRequest request) {
        CurationRequestJpaEntity entity = CurationRequestJpaEntity.from(request);
        CurationRequestJpaEntity saved = jpaRepository.save(entity);
        return saved.toDomain();
    }

    @Override
    public Optional<CurationRequest> findById(Long id) {
        return jpaRepository.findById(id)
                .map(CurationRequestJpaEntity::toDomain);
    }
}
