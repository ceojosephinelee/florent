package com.florent.adapter.in.image.dto;

import com.florent.domain.image.PresignedUrlResult;

public record PresignedUrlResponse(
        String presignedUrl,
        String imageUrl
) {
    public static PresignedUrlResponse from(PresignedUrlResult result) {
        return new PresignedUrlResponse(result.presignedUrl(), result.imageUrl());
    }
}
