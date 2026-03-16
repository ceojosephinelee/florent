package com.florent.adapter.in.auth.dto;

import com.florent.domain.auth.SetRoleResult;

public record SetRoleResponse(
        String role,
        String accessToken,
        String refreshToken
) {
    public static SetRoleResponse from(SetRoleResult result) {
        return new SetRoleResponse(
                result.role(), result.accessToken(), result.refreshToken());
    }
}
