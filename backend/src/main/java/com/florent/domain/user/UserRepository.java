package com.florent.domain.user;

import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(Long id);
    Optional<User> findByKakaoId(String kakaoId);
    Optional<User> findFirstByRole(UserRole role);
}
