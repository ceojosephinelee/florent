package com.florent.adapter.in.auth.dto;

import com.florent.domain.user.UserRole;
import jakarta.validation.constraints.NotNull;

public record SetRoleRequest(
        @NotNull UserRole role
) {}
