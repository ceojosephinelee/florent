package com.florent.fake;

import com.florent.domain.proposal.Proposal;
import com.florent.domain.proposal.ProposalRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class FakeProposalRepository implements ProposalRepository {

    private final Map<Long, Proposal> store = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Proposal save(Proposal proposal) {
        Long id = proposal.getId();
        if (id == null) {
            id = idGenerator.getAndIncrement();
        }
        Proposal persisted = Proposal.reconstitute(
                id,
                proposal.getRequestId(),
                proposal.getFlowerShopId(),
                proposal.getStatus(),
                proposal.getConceptTitle(),
                proposal.getMoodColors(),
                proposal.getMainFlowers(),
                proposal.getWrappingStyle(),
                proposal.getAllergyNote(),
                proposal.getCareTips(),
                proposal.getDescription(),
                proposal.getImageUrls(),
                proposal.getAvailableSlotKind(),
                proposal.getAvailableSlotValue(),
                proposal.getPrice(),
                proposal.getCreatedAt(),
                proposal.getExpiresAt(),
                proposal.getSubmittedAt()
        );
        store.put(id, persisted);
        return persisted;
    }

    @Override
    public Optional<Proposal> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Proposal> findByRequestId(Long requestId) {
        return store.values().stream()
                .filter(p -> p.getRequestId().equals(requestId))
                .toList();
    }
}
