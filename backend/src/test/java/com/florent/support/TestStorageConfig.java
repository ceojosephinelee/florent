package com.florent.support;

import com.florent.domain.image.ImageTarget;
import com.florent.domain.image.PresignedUrlResult;
import com.florent.domain.image.StoragePort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
public class TestStorageConfig {

    @Bean
    public StoragePort storagePort() {
        return (fileName, contentType, target) -> {
            String key = target.name().toLowerCase() + "/" + UUID.randomUUID() + "/" + fileName;
            return new PresignedUrlResult(
                    "https://test-s3.example.com/" + key + "?signature=test",
                    "https://test-s3.example.com/" + key
            );
        };
    }
}
