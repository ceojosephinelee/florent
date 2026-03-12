package com.florent.domain.notification;

public interface SaveNotificationUseCase {
    void saveRequestArrived(Long sellerId, Long requestId);
}
