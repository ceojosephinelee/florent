package com.florent.domain.image;

public interface GeneratePresignedUrlUseCase {
    PresignedUrlResult generate(GeneratePresignedUrlCommand command);
}
