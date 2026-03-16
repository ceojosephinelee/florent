package com.florent.domain.auth;

public interface ReissueTokenUseCase {
    ReissueTokenResult reissue(ReissueTokenCommand command);
}
