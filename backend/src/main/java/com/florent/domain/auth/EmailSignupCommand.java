package com.florent.domain.auth;

public record EmailSignupCommand(
        String email,
        String password,
        String nickname
) {}
