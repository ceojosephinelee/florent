package com.florent.adapter.out.persistence.proposal;

import com.florent.domain.proposal.Proposal;
import com.florent.domain.proposal.ProposalRepository;
import lombok.RequiredArgsConstructor;
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
    public List<Proposal> findByRequestId(Long requestId) {
        return jpaRepository.findByRequestId(requestId).stream()
                .map(ProposalJpaEntity::toDomain)
                .toList();
    }
}
