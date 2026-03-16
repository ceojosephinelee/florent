package com.florent.domain.image;

public interface StoragePort {
    PresignedUrlResult generatePresignedUrl(String fileName, String contentType, ImageTarget target);
}
