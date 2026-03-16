package com.florent.adapter.in.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record ReissueTokenRequest(
        @NotBlank String refreshToken
) {}
