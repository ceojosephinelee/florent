package com.florent.fake;

import com.florent.domain.request.CurationRequest;
import com.florent.domain.request.CurationRequestRepository;
import com.florent.domain.request.RequestPage;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class FakeCurationRequestRepository implements CurationRequestRepository {

    private final Map<Long, CurationRequest> store = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public CurationRequest save(CurationRequest request) {
        Long id = request.getId();
        if (id == null) {
            id = idGenerator.getAndIncrement();
        }
        CurationRequest persisted = CurationRequest.reconstitute(
                id,
                request.getBuyerId(),
                request.getStatus(),
                request.getPurposeTags(),
                request.getRelationTags(),
                request.getMoodTags(),
                request.getBudgetTier(),
                request.getFulfillmentType(),
                request.getFulfillmentDate(),
                request.getRequestedTimeSlots(),
                request.getPlaceAddressText(),
                request.getPlaceLat(),
                request.getPlaceLng(),
                request.getCreatedAt(),
                request.getExpiresAt()
        );
        store.put(id, persisted);
        return persisted;
    }

    @Override
    public Optional<CurationRequest> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<CurationRequest> findAllByIds(List<Long> ids) {
        return ids.stream()
                .map(store::get)
                .filter(java.util.Objects::nonNull)
                .toList();
    }

    @Override
    public RequestPage findByBuyerId(Long buyerId, int page, int size) {
        List<CurationRequest> filtered = store.values().stream()
                .filter(r -> r.getBuyerId().equals(buyerId))
                .sorted(Comparator.comparing(CurationRequest::getCreatedAt).reversed())
                .toList();

        int totalElements = filtered.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        int fromIndex = Math.min(page * size, totalElements);
        int toIndex = Math.min(fromIndex + size, totalElements);
        List<CurationRequest> content = filtered.subList(fromIndex, toIndex);
        boolean last = (page + 1) >= totalPages;

        return new RequestPage(content, totalElements, totalPages, last);
    }
}
