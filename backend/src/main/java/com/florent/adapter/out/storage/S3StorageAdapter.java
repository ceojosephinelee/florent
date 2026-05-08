package com.florent.adapter.out.storage;

import com.florent.common.config.S3Properties;
import com.florent.domain.image.ImageTarget;
import com.florent.domain.image.PresignedUrlResult;
import com.florent.domain.image.StoragePort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.UUID;

@Slf4j
@Component
@Profile("prod")
public class S3StorageAdapter implements StoragePort {

    private final S3Presigner presigner;
    private final S3Properties properties;

    public S3StorageAdapter(S3Properties properties) {
        this(properties, S3Presigner.builder()
                .region(Region.of(properties.region()))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build());
    }

    S3StorageAdapter(S3Properties properties, S3Presigner presigner) {
        this.properties = properties;
        this.presigner = presigner;
    }

    @Override
    public PresignedUrlResult generatePresignedUrl(String fileName, String contentType, ImageTarget target) {
        String key = target.name().toLowerCase() + "/" + UUID.randomUUID() + "/" + fileName;

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(properties.bucket())
                .key(key)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofSeconds(properties.presignedUrlExpiration()))
                .putObjectRequest(objectRequest)
                .build();

        String presignedUrl = presigner.presignPutObject(presignRequest).url().toString();
        String imageUrl = String.format("https://%s.s3.%s.amazonaws.com/%s",
                properties.bucket(), properties.region(), key);

        log.info("[S3Storage] presigned URL generated: target={}, key={}", target, key);

        return new PresignedUrlResult(presignedUrl, imageUrl);
    }
}
