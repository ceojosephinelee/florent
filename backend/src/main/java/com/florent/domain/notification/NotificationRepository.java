package com.florent.domain.notification;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository {
    Notification save(Notification notification);
    Optional<Notification> findById(Long id);
    List<Notification> findByUserId(Long userId, int page, int size);
    long countByUserId(Long userId);
    List<Long> findIdsByUserId(Long userId);
    void deleteByUserId(Long userId);
}