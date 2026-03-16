package com.florent.domain.auth;

public interface SetRoleUseCase {
    SetRoleResult setRole(Long userId, SetRoleCommand command);
}
