package com.florent.fake;

import com.florent.domain.user.User;
import com.florent.domain.user.UserRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class FakeUserRepository implements UserRepository {

    private final Map<Long, User> store = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public User save(User user) {
        Long id = user.getId();
        if (id == null) {
            id = idGenerator.getAndIncrement();
        }
        User persisted = User.reconstitute(
                id, user.getKakaoId(), user.getEmail(), user.getNickname(),
                user.getRole(), user.getRefreshToken(),
                user.getRefreshTokenExpiresAt(), user.getCreatedAt());
        store.put(id, persisted);
        return persisted;
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Optional<User> findByKakaoId(String kakaoId) {
        return store.values().stream()
                .filter(u -> u.getKakaoId().equals(kakaoId))
                .findFirst();
    }
}
