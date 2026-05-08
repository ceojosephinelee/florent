package com.florent.domain.auth;

public interface EmailLoginUseCase {
    EmailAuthResult login(EmailLoginCommand command);
}