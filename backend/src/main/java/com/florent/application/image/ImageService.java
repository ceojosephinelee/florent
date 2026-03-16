package com.florent.application.image;

import com.florent.domain.image.GeneratePresignedUrlCommand;
import com.florent.domain.image.GeneratePresignedUrlUseCase;
import com.florent.domain.image.PresignedUrlResult;
import com.florent.domain.image.StoragePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ImageService implements GeneratePresignedUrlUseCase {

    private final StoragePort storagePort;

    @Override
    public PresignedUrlResult generate(GeneratePresignedUrlCommand command) {
        return storagePort.generatePresignedUrl(
                command.fileName(),
                command.contentType(),
                command.target()
        );
    }
}
