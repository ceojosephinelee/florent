package com.florent.adapter.out.persistence.notification;

import com.florent.domain.notification.Notification;
import com.florent.domain.notification.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class NotificationRepositoryImpl implements NotificationRepository {

    private final NotificationJpaRepository jpaRepository;

    @Override
    public Notification save(Notification notification) {
        NotificationJpaEntity entity = NotificationJpaEntity.from(notification);
        NotificationJpaEntity saved = jpaRepository.save(entity);
        return saved.toDomain();
    }

    @Override
    public Optional<Notification> findById(Long id) {
        return jpaRepository.findById(id)
                .map(NotificationJpaEntity::toDomain);
    }

    @Override
    public List<Notification> findByUserId(Long userId, int page, int size) {
        return jpaRepository.findByUserIdOrderByCreatedAtDesc(userId, PageRequest.of(page, size))
                .getContent().stream()
                .map(NotificationJpaEntity::toDomain)
                .toList();
    }

    @Override
    public long countByUserId(Long userId) {
        return jpaRepository.countByUserId(userId);
    }
}