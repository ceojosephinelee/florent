package com.florent.adapter.out.persistence.notification;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserDeviceJpaRepository extends JpaRepository<UserDeviceJpaEntity, Long> {
    Optional<UserDeviceJpaEntity> findByFcmToken(String fcmToken);
    List<UserDeviceJpaEntity> findByUserIdAndIsActiveTrue(Long userId);
}