package com.florent.adapter.out.persistence.user;

import com.florent.domain.user.User;
import com.florent.domain.user.UserRepository;
import com.florent.domain.user.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository jpaRepository;

    @Override
    public User save(User user) {
        UserJpaEntity entity = UserJpaEntity.from(user);
        return jpaRepository.save(entity).toDomain();
    }

    @Override
    public Optional<User> findById(Long id) {
        return jpaRepository.findById(id)
                .map(UserJpaEntity::toDomain);
    }

    @Override
    public Optional<User> findByKakaoId(String kakaoId) {
        return jpaRepository.findByKakaoId(kakaoId)
                .map(UserJpaEntity::toDomain);
    }

    @Override
    public Optional<User> findFirstByRole(UserRole role) {
        return jpaRepository.findFirstByRole(role.name())
                .map(UserJpaEntity::toDomain);
    }
}
