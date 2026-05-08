package com.florent.domain.auth;

public interface EmailSignupUseCase {
    EmailAuthResult signup(EmailSignupCommand command);
}
