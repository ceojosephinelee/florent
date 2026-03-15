package com.florent.adapter.out.persistence.notification;

import com.florent.domain.notification.OutboxEvent;
import com.florent.domain.notification.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OutboxEventRepositoryImpl implements OutboxEventRepository {

    private final OutboxEventJpaRepository jpaRepository;

    @Override
    public OutboxEvent save(OutboxEvent event) {
        OutboxEventJpaEntity entity = OutboxEventJpaEntity.from(event);
        OutboxEventJpaEntity saved = jpaRepository.save(entity);
        return saved.toDomain();
    }

    @Override
    public List<OutboxEvent> findPendingBefore(LocalDateTime now, int limit) {
        return jpaRepository.findPendingBefore(now, PageRequest.of(0, limit))
                .stream()
                .map(OutboxEventJpaEntity::toDomain)
                .toList();
    }
}