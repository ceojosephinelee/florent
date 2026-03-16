package com.florent.domain.image;

public record PresignedUrlResult(
        String presignedUrl,
        String imageUrl
) {
}
