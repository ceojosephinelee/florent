package com.florent.fake;

import com.florent.domain.request.CurationRequest;
import com.florent.domain.request.CurationRequestRepository;

import java.util.HashMap;
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
}
