package com.florent.adapter.in.auth.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record DevLoginRequest(
        @NotNull @Pattern(regexp = "BUYER|SELLER") String role
) {}
