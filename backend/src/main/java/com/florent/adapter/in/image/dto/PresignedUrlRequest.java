package com.florent.adapter.in.image.dto;

import com.florent.domain.image.GeneratePresignedUrlCommand;
import com.florent.domain.image.ImageTarget;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PresignedUrlRequest(
        @NotBlank String fileName,
        @NotBlank String contentType,
        @NotNull ImageTarget target
) {
    public GeneratePresignedUrlCommand toCommand() {
        return new GeneratePresignedUrlCommand(fileName, contentType, target);
    }
}
