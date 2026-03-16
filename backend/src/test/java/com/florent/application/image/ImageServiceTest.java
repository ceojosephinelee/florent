package com.florent.application.image;

import com.florent.domain.image.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ImageServiceTest {

    private final StoragePort storagePort = (fileName, contentType, target) ->
            new PresignedUrlResult(
                    "https://s3.amazonaws.com/presigned/" + fileName,
                    "https://s3.amazonaws.com/images/" + fileName
            );

    private final ImageService imageService = new ImageService(storagePort);

    @Test
    @DisplayName("presigned URL 생성 성공")
    void presigned_URL_생성_성공() {
        // given
        GeneratePresignedUrlCommand command = new GeneratePresignedUrlCommand(
                "flower.jpg", "image/jpeg", ImageTarget.PROPOSAL
        );

        // when
        PresignedUrlResult result = imageService.generate(command);

        // then
        assertThat(result.presignedUrl()).contains("flower.jpg");
        assertThat(result.imageUrl()).contains("flower.jpg");
    }
}
