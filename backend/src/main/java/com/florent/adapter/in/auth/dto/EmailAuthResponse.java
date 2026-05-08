package com.florent.adapter.in.auth.dto;

import com.florent.domain.auth.EmailAuthResult;

public record EmailAuthResponse(
        String accessToken,
        String refreshToken,
        String role,
        boolean isNewUser,
        boolean hasFlowerShop
) {
    public static EmailAuthResponse from(EmailAuthResult result) {
        return new EmailAuthResponse(
                result.accessToken(), result.refreshToken(),
                result.role(), result.isNewUser(), result.hasFlowerShop());
    }
}
