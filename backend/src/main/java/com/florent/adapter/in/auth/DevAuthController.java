package com.florent.adapter.in.auth;

import com.florent.adapter.in.auth.dto.DevLoginRequest;
import com.florent.adapter.in.auth.dto.KakaoLoginResponse;
import com.florent.common.response.ApiResponse;
import com.florent.domain.auth.DevLoginUseCase;
import com.florent.domain.auth.KakaoLoginResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Profile({"local", "prod"})
@RequiredArgsConstructor
public class DevAuthController {

    private final DevLoginUseCase devLoginUseCase;

    @PostMapping("/dev-login")
    public ResponseEntity<ApiResponse<KakaoLoginResponse>> devLogin(
            @RequestBody @Valid DevLoginRequest request) {
        KakaoLoginResult result = devLoginUseCase.devLogin(request.role());
        return ResponseEntity.ok(ApiResponse.success(KakaoLoginResponse.from(result)));
    }
}
