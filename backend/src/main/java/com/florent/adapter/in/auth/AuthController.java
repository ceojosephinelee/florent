package com.florent.adapter.in.auth;

import com.florent.adapter.in.auth.dto.KakaoLoginRequest;
import com.florent.adapter.in.auth.dto.KakaoLoginResponse;
import com.florent.adapter.in.auth.dto.ReissueTokenRequest;
import com.florent.adapter.in.auth.dto.ReissueTokenResponse;
import com.florent.adapter.in.auth.dto.RegisterSellerInfoRequest;
import com.florent.adapter.in.auth.dto.RegisterSellerInfoResponse;
import com.florent.adapter.in.auth.dto.SetRoleRequest;
import com.florent.adapter.in.auth.dto.SetRoleResponse;
import com.florent.common.response.ApiResponse;
import com.florent.common.security.UserPrincipal;
import com.florent.domain.auth.KakaoLoginCommand;
import com.florent.domain.auth.KakaoLoginResult;
import com.florent.domain.auth.KakaoLoginUseCase;
import com.florent.domain.auth.LogoutUseCase;
import com.florent.domain.auth.ReissueTokenCommand;
import com.florent.domain.auth.ReissueTokenResult;
import com.florent.domain.auth.ReissueTokenUseCase;
import com.florent.domain.auth.RegisterSellerInfoCommand;
import com.florent.domain.auth.RegisterSellerInfoResult;
import com.florent.domain.auth.RegisterSellerInfoUseCase;
import com.florent.domain.auth.SetRoleCommand;
import com.florent.domain.auth.SetRoleResult;
import com.florent.domain.auth.SetRoleUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final KakaoLoginUseCase kakaoLoginUseCase;
    private final SetRoleUseCase setRoleUseCase;
    private final ReissueTokenUseCase reissueTokenUseCase;
    private final LogoutUseCase logoutUseCase;
    private final RegisterSellerInfoUseCase registerSellerInfoUseCase;

    @PostMapping("/kakao")
    public ResponseEntity<ApiResponse<KakaoLoginResponse>> kakaoLogin(
            @RequestBody @Valid KakaoLoginRequest request) {
        KakaoLoginResult result = kakaoLoginUseCase.login(
                new KakaoLoginCommand(request.kakaoAccessToken()));
        return ResponseEntity.ok(ApiResponse.success(KakaoLoginResponse.from(result)));
    }

    @PostMapping("/role")
    public ResponseEntity<ApiResponse<SetRoleResponse>> setRole(
            @RequestBody @Valid SetRoleRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        SetRoleResult result = setRoleUseCase.setRole(
                principal.getUserId(),
                new SetRoleCommand(request.role()));
        return ResponseEntity.ok(ApiResponse.success(SetRoleResponse.from(result)));
    }

    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<ReissueTokenResponse>> reissue(
            @RequestBody @Valid ReissueTokenRequest request) {
        ReissueTokenResult result = reissueTokenUseCase.reissue(
                new ReissueTokenCommand(request.refreshToken()));
        return ResponseEntity.ok(ApiResponse.success(ReissueTokenResponse.from(result)));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @AuthenticationPrincipal UserPrincipal principal) {
        logoutUseCase.logout(principal.getUserId());
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/seller-info")
    public ResponseEntity<ApiResponse<RegisterSellerInfoResponse>> registerSellerInfo(
            @RequestBody @Valid RegisterSellerInfoRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        RegisterSellerInfoResult result = registerSellerInfoUseCase.register(
                principal.getSellerId(),
                new RegisterSellerInfoCommand(
                        request.shopName(), request.shopAddress(),
                        request.shopLat(), request.shopLng(),
                        request.businessNumber()));
        return ResponseEntity.ok(ApiResponse.success(RegisterSellerInfoResponse.from(result)));
    }
}
