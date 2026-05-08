package com.florent.domain.auth;

public record EmailLoginCommand(
        String email,
        String password
) {}
