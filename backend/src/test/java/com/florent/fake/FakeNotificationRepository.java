package com.florent.fake;

import com.florent.domain.notification.Notification;
import com.florent.domain.notification.NotificationRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class FakeNotificationRepository implements NotificationRepository {

    private final List<Notification> store = new ArrayList<>();
    private final AtomicLong sequence = new AtomicLong(1);

    @Override
    public Notification save(Notification notification) {
        if (notification.getId() == null) {
            Notification saved = Notification.reconstitute(
                    sequence.getAndIncrement(),
                    notification.getUserId(),
                    notification.getType(),
                    notification.getReferenceType(),
                    notification.getReferenceId(),
                    notification.getTitle(),
                    notification.getBody(),
                    notification.isRead(),
                    notification.getCreatedAt(),
                    notification.getUpdatedAt());
            store.add(saved);
            return saved;
        }
        store.removeIf(n -> n.getId().equals(notification.getId()));
        store.add(notification);
        return notification;
    }

    @Override
    public Optional<Notification> findById(Long id) {
        return store.stream()
                .filter(n -> n.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<Notification> findByUserId(Long userId, int page, int size) {
        return store.stream()
                .filter(n -> n.getUserId().equals(userId))
                .sorted(Comparator.comparing(Notification::getCreatedAt).reversed())
                .skip((long) page * size)
                .limit(size)
                .toList();
    }

    @Override
    public long countByUserId(Long userId) {
        return store.stream()
                .filter(n -> n.getUserId().equals(userId))
                .count();
    }
}