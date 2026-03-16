package com.florent.fake;

import com.florent.domain.proposal.Proposal;
import com.florent.domain.proposal.ProposalRepository;
import com.florent.domain.proposal.ProposalStatus;

import com.florent.domain.proposal.ProposalPage;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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
    public List<Proposal> findAllByIds(List<Long> ids) {
        return ids.stream()
                .map(store::get)
                .filter(java.util.Objects::nonNull)
                .toList();
    }

    @Override
    public List<Proposal> findByRequestId(Long requestId) {
        return store.values().stream()
                .filter(p -> p.getRequestId().equals(requestId))
                .toList();
    }

    @Override
    public ProposalPage findByFlowerShopId(Long flowerShopId, int page, int size) {
        List<Proposal> all = store.values().stream()
                .filter(p -> p.getFlowerShopId().equals(flowerShopId))
                .sorted(Comparator.comparing(Proposal::getCreatedAt).reversed())
                .toList();
        int start = page * size;
        int end = Math.min(start + size, all.size());
        List<Proposal> content = start < all.size() ? all.subList(start, end) : List.of();
        int totalPages = (int) Math.ceil((double) all.size() / size);
        return new ProposalPage(content, all.size(), totalPages, page >= totalPages - 1);
    }

    @Override
    public boolean existsByRequestIdAndFlowerShopId(Long requestId, Long flowerShopId) {
        return store.values().stream()
                .anyMatch(p -> p.getRequestId().equals(requestId)
                        && p.getFlowerShopId().equals(flowerShopId));
    }

    @Override
    public Optional<Proposal> findByRequestIdAndFlowerShopId(Long requestId, Long flowerShopId) {
        return store.values().stream()
                .filter(p -> p.getRequestId().equals(requestId)
                        && p.getFlowerShopId().equals(flowerShopId))
                .findFirst();
    }

    @Override
    public List<Proposal> findByRequestIdsAndFlowerShopId(List<Long> requestIds, Long flowerShopId) {
        return store.values().stream()
                .filter(p -> requestIds.contains(p.getRequestId())
                        && p.getFlowerShopId().equals(flowerShopId))
                .toList();
    }

    @Override
    public List<Proposal> findAllByFlowerShopId(Long flowerShopId) {
        return store.values().stream()
                .filter(p -> p.getFlowerShopId().equals(flowerShopId))
                .toList();
    }

    @Override
    public List<Proposal> findExpirableBefore(LocalDateTime now) {
        Set<ProposalStatus> expirableStatuses = Set.of(ProposalStatus.DRAFT, ProposalStatus.SUBMITTED);
        return store.values().stream()
                .filter(p -> expirableStatuses.contains(p.getStatus()))
                .filter(p -> p.getExpiresAt().isBefore(now))
                .toList();
    }
}
