package com.florent.domain.notification;

import java.time.LocalDateTime;
import java.util.List;

public interface OutboxEventRepository {
    OutboxEvent save(OutboxEvent event);
    boolean existsByDedupKey(String dedupKey);
    List<OutboxEvent> findPendingBefore(LocalDateTime now, int limit);
}