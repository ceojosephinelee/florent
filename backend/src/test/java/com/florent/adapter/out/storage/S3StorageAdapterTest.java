package com.florent.adapter.out.storage;

import com.florent.common.config.S3Properties;
import com.florent.domain.image.ImageTarget;
import com.florent.domain.image.PresignedUrlResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.net.URI;
import java.net.URL;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class S3StorageAdapterTest {

    private final S3Properties properties = new S3Properties("florent-images", "ap-northeast-2", 600);
    private final S3Presigner presigner = mock(S3Presigner.class);
    private final S3StorageAdapter adapter = new S3StorageAdapter(properties, presigner);

    @Test
    @DisplayName("Presigned URL 결과에 올바른 imageUrl 형식 포함")
    void presigned_URL_결과에_올바른_imageUrl_형식_포함() throws Exception {
        // given
        PresignedPutObjectRequest mockPresigned = mock(PresignedPutObjectRequest.class);
        given(mockPresigned.url()).willReturn(new URL("https://florent-images.s3.ap-northeast-2.amazonaws.com/proposal/uuid/flower.jpg?X-Amz-Signature=abc"));
        given(presigner.presignPutObject(any(PutObjectPresignRequest.class))).willReturn(mockPresigned);

        // when
        PresignedUrlResult result = adapter.generatePresignedUrl("flower.jpg", "image/jpeg", ImageTarget.PROPOSAL);

        // then
        assertThat(result.imageUrl()).startsWith("https://florent-images.s3.ap-northeast-2.amazonaws.com/proposal/");
        assertThat(result.imageUrl()).endsWith("/flower.jpg");
        assertThat(result.presignedUrl()).contains("florent-images");
    }

    @Test
    @DisplayName("서로 다른 호출 시 다른 UUID 키 생성")
    void 서로_다른_호출_시_다른_UUID_키_생성() throws Exception {
        // given
        PresignedPutObjectRequest mockPresigned = mock(PresignedPutObjectRequest.class);
        given(mockPresigned.url()).willReturn(new URL("https://florent-images.s3.amazonaws.com/test"));
        given(presigner.presignPutObject(any(PutObjectPresignRequest.class))).willReturn(mockPresigned);

        // when
        PresignedUrlResult result1 = adapter.generatePresignedUrl("flower.jpg", "image/jpeg", ImageTarget.PROPOSAL);
        PresignedUrlResult result2 = adapter.generatePresignedUrl("flower.jpg", "image/jpeg", ImageTarget.PROPOSAL);

        // then
        assertThat(result1.imageUrl()).isNotEqualTo(result2.imageUrl());
    }
}
