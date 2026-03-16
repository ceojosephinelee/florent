package com.florent.domain.notification;

import java.util.List;
import java.util.Optional;

public interface UserDeviceRepository {
    UserDevice save(UserDevice device);
    Optional<UserDevice> findByFcmToken(String fcmToken);
    List<UserDevice> findActiveByUserId(Long userId);
}