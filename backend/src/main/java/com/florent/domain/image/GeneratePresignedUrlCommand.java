package com.florent.domain.image;

public record GeneratePresignedUrlCommand(
        String fileName,
        String contentType,
        ImageTarget target
) {
}
