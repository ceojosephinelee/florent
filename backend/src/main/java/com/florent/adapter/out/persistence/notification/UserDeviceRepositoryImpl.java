package com.florent.adapter.out.persistence.notification;

import com.florent.domain.notification.UserDevice;
import com.florent.domain.notification.UserDeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserDeviceRepositoryImpl implements UserDeviceRepository {

    private final UserDeviceJpaRepository jpaRepository;

    @Override
    public UserDevice save(UserDevice device) {
        UserDeviceJpaEntity entity = UserDeviceJpaEntity.from(device);
        UserDeviceJpaEntity saved = jpaRepository.save(entity);
        return saved.toDomain();
    }

    @Override
    public Optional<UserDevice> findByFcmToken(String fcmToken) {
        return jpaRepository.findByFcmToken(fcmToken)
                .map(UserDeviceJpaEntity::toDomain);
    }

    @Override
    public List<UserDevice> findActiveByUserId(Long userId) {
        return jpaRepository.findByUserIdAndIsActiveTrue(userId).stream()
                .map(UserDeviceJpaEntity::toDomain)
                .toList();
    }
}