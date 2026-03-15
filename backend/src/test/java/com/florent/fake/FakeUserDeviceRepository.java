package com.florent.fake;

import com.florent.domain.notification.UserDevice;
import com.florent.domain.notification.UserDeviceRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class FakeUserDeviceRepository implements UserDeviceRepository {

    private final List<UserDevice> store = new ArrayList<>();
    private final AtomicLong sequence = new AtomicLong(1);

    @Override
    public UserDevice save(UserDevice device) {
        if (device.getId() == null) {
            UserDevice saved = UserDevice.reconstitute(
                    sequence.getAndIncrement(),
                    device.getUserId(),
                    device.getPlatform(),
                    device.getFcmToken(),
                    device.isActive(),
                    device.getCreatedAt(),
                    device.getUpdatedAt());
            store.add(saved);
            return saved;
        }
        store.removeIf(d -> d.getId().equals(device.getId()));
        store.add(device);
        return device;
    }

    @Override
    public Optional<UserDevice> findByFcmToken(String fcmToken) {
        return store.stream()
                .filter(d -> d.getFcmToken().equals(fcmToken))
                .findFirst();
    }

    @Override
    public List<UserDevice> findActiveByUserId(Long userId) {
        return store.stream()
                .filter(d -> d.getUserId().equals(userId) && d.isActive())
                .toList();
    }
}