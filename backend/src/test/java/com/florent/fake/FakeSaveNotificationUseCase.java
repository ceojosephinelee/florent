package com.florent.fake;

import com.florent.domain.notification.SaveNotificationUseCase;

import java.util.ArrayList;
import java.util.List;

public class FakeSaveNotificationUseCase implements SaveNotificationUseCase {

    private final List<NotificationRecord> records = new ArrayList<>();

    @Override
    public void saveRequestArrived(Long sellerId, Long requestId) {
        records.add(new NotificationRecord(sellerId, requestId));
    }

    public List<NotificationRecord> getRecords() {
        return List.copyOf(records);
    }

    public void clear() {
        records.clear();
    }

    public record NotificationRecord(Long sellerId, Long requestId) {}
}
