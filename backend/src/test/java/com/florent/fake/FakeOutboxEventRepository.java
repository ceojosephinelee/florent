package com.florent.fake;

import com.florent.domain.notification.OutboxEvent;
import com.florent.domain.notification.OutboxEventRepository;
import com.florent.domain.notification.OutboxStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class FakeOutboxEventRepository implements OutboxEventRepository {

    private final List<OutboxEvent> store = new ArrayList<>();
    private final AtomicLong sequence = new AtomicLong(1);

    @Override
    public OutboxEvent save(OutboxEvent event) {
        if (event.getId() == null) {
            OutboxEvent saved = OutboxEvent.reconstitute(
                    sequence.getAndIncrement(),
                    event.getNotificationId(),
                    event.getStatus(),
                    event.getDedupKey(),
                    event.getAttemptCount(),
                    event.getAvailableAt(),
                    event.getCreatedAt());
            store.add(saved);
            return saved;
        }
        store.removeIf(e -> e.getId().equals(event.getId()));
        store.add(event);
        return event;
    }

    @Override
    public List<OutboxEvent> findPendingBefore(LocalDateTime now, int limit) {
        return store.stream()
                .filter(e -> e.getStatus() == OutboxStatus.PENDING)
                .filter(e -> !e.getAvailableAt().isAfter(now))
                .sorted(Comparator.comparing(OutboxEvent::getCreatedAt))
                .limit(limit)
                .toList();
    }

    public List<OutboxEvent> findAll() {
        return List.copyOf(store);
    }
}