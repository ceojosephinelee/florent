package com.florent.adapter.in.auth.dto;

import com.florent.domain.auth.ReissueTokenResult;

public record ReissueTokenResponse(
        String accessToken,
        String refreshToken,
        String role,
        boolean hasFlowerShop
) {
    public static ReissueTokenResponse from(ReissueTokenResult result) {
        return new ReissueTokenResponse(
                result.accessToken(), result.refreshToken(),
                result.role(), result.hasFlowerShop());
    }
}
