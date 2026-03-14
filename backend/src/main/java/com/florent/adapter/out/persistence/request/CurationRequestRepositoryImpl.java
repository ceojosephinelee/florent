package com.florent.adapter.out.persistence.request;

import com.florent.domain.request.CurationRequest;
import com.florent.domain.request.CurationRequestRepository;
import com.florent.domain.request.RequestPage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

    @Override
    public RequestPage findByBuyerId(Long buyerId, int page, int size) {
        Page<CurationRequestJpaEntity> jpaPage =
                jpaRepository.findByBuyerIdOrderByCreatedAtDesc(buyerId, PageRequest.of(page, size));
        return new RequestPage(
                jpaPage.getContent().stream().map(CurationRequestJpaEntity::toDomain).toList(),
                jpaPage.getTotalElements(),
                jpaPage.getTotalPages(),
                jpaPage.isLast());
    }
}
