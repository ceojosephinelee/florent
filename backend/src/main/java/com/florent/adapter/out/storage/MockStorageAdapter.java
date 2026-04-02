package com.florent.adapter.out.storage;

import com.florent.domain.image.ImageTarget;
import com.florent.domain.image.PresignedUrlResult;
import com.florent.domain.image.StoragePort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@org.springframework.context.annotation.Profile("local")
public class MockStorageAdapter implements StoragePort {

    private static final String MOCK_BASE_URL = "https://florent-mock-s3.amazonaws.com";

    @Override
    public PresignedUrlResult generatePresignedUrl(String fileName, String contentType, ImageTarget target) {
        String key = target.name().toLowerCase() + "/" + UUID.randomUUID() + "/" + fileName;
        String imageUrl = MOCK_BASE_URL + "/" + key;
        String presignedUrl = imageUrl + "?X-Amz-SignedHeaders=host&X-Amz-Signature=mock-signature";

        log.info("[MockStorage] presigned URL generated: target={}, fileName={}, contentType={}, key={}",
                target, fileName, contentType, key);

        return new PresignedUrlResult(presignedUrl, imageUrl);
    }
}
