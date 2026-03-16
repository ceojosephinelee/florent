package com.florent.adapter.out.persistence.proposal;

import com.florent.domain.proposal.Proposal;
import com.florent.domain.proposal.ProposalPage;
import com.florent.domain.proposal.ProposalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProposalRepositoryImpl implements ProposalRepository {

    private final ProposalJpaRepository jpaRepository;

    @Override
    public Proposal save(Proposal proposal) {
        ProposalJpaEntity entity = ProposalJpaEntity.from(proposal);
        ProposalJpaEntity saved = jpaRepository.save(entity);
        return saved.toDomain();
    }

    @Override
    public Optional<Proposal> findById(Long id) {
        return jpaRepository.findById(id)
                .map(ProposalJpaEntity::toDomain);
    }

    @Override
    public List<Proposal> findAllByIds(List<Long> ids) {
        return jpaRepository.findAllById(ids).stream()
                .map(ProposalJpaEntity::toDomain)
                .toList();
    }

    @Override
    public List<Proposal> findByRequestId(Long requestId) {
        return jpaRepository.findByRequestId(requestId).stream()
                .map(ProposalJpaEntity::toDomain)
                .toList();
    }

    @Override
    public ProposalPage findByFlowerShopId(Long flowerShopId, int page, int size) {
        Page<ProposalJpaEntity> jpaPage =
                jpaRepository.findByFlowerShopIdOrderByCreatedAtDesc(
                        flowerShopId, PageRequest.of(page, size));
        return new ProposalPage(
                jpaPage.getContent().stream().map(ProposalJpaEntity::toDomain).toList(),
                jpaPage.getTotalElements(),
                jpaPage.getTotalPages(),
                jpaPage.isLast());
    }

    @Override
    public boolean existsByRequestIdAndFlowerShopId(Long requestId, Long flowerShopId) {
        return jpaRepository.existsByRequestIdAndFlowerShopId(requestId, flowerShopId);
    }

    @Override
    public Optional<Proposal> findByRequestIdAndFlowerShopId(Long requestId, Long flowerShopId) {
        return jpaRepository.findByRequestIdAndFlowerShopId(requestId, flowerShopId)
                .map(ProposalJpaEntity::toDomain);
    }

    @Override
    public List<Proposal> findByRequestIdsAndFlowerShopId(List<Long> requestIds, Long flowerShopId) {
        return jpaRepository.findByRequestIdInAndFlowerShopId(requestIds, flowerShopId).stream()
                .map(ProposalJpaEntity::toDomain)
                .toList();
    }
}
