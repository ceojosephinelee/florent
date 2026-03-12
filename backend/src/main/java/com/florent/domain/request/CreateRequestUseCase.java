package com.florent.domain.request;

public interface CreateRequestUseCase {
    CreateRequestResult create(CreateRequestCommand command);
}
