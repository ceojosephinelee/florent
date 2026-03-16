package com.florent.adapter.in.image;

import com.florent.adapter.in.image.dto.PresignedUrlRequest;
import com.florent.adapter.in.image.dto.PresignedUrlResponse;
import com.florent.common.response.ApiResponse;
import com.florent.domain.image.GeneratePresignedUrlUseCase;
import com.florent.domain.image.PresignedUrlResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/images")
@RequiredArgsConstructor
public class ImageController {

    private final GeneratePresignedUrlUseCase generatePresignedUrlUseCase;

    @PostMapping("/presigned-url")
    public ResponseEntity<ApiResponse<PresignedUrlResponse>> generatePresignedUrl(
            @RequestBody @Valid PresignedUrlRequest request
    ) {
        PresignedUrlResult result = generatePresignedUrlUseCase.generate(request.toCommand());
        return ResponseEntity.ok(ApiResponse.success(PresignedUrlResponse.from(result)));
    }
}
