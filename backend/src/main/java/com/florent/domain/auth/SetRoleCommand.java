package com.florent.domain.auth;

import com.florent.domain.user.UserRole;

public record SetRoleCommand(UserRole role) {}
