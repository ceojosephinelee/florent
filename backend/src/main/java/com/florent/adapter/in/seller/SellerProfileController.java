package com.florent.adapter.in.seller;

import com.florent.common.response.ApiResponse;
import com.florent.common.security.UserPrincipal;
import com.florent.domain.seller.GetSellerHomeUseCase;
import com.florent.domain.seller.GetSellerProfileUseCase;
import com.florent.domain.seller.GetSellerStatsUseCase;
import com.florent.domain.seller.SellerHomeResult;
import com.florent.domain.seller.SellerProfileResult;
import com.florent.domain.seller.SellerStatsResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/seller")
@RequiredArgsConstructor
public class SellerProfileController {

    private final GetSellerProfileUseCase getSellerProfileUseCase;
    private final GetSellerHomeUseCase getSellerHomeUseCase;
    private final GetSellerStatsUseCase getSellerStatsUseCase;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<SellerProfileResponse>> getProfile(
            @AuthenticationPrincipal UserPrincipal principal) {
        SellerProfileResult result = getSellerProfileUseCase.getProfile(principal.getSellerId());
        return ResponseEntity.ok(ApiResponse.success(SellerProfileResponse.from(result)));
    }

    @GetMapping("/home")
    public ResponseEntity<ApiResponse<SellerHomeResponse>> getHome(
            @AuthenticationPrincipal UserPrincipal principal) {
        SellerHomeResult result = getSellerHomeUseCase.getHome(principal.getSellerId());
        return ResponseEntity.ok(ApiResponse.success(SellerHomeResponse.from(result)));
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<SellerStatsResponse>> getStats(
            @AuthenticationPrincipal UserPrincipal principal) {
        SellerStatsResult result = getSellerStatsUseCase.getStats(principal.getSellerId());
        return ResponseEntity.ok(ApiResponse.success(SellerStatsResponse.from(result)));
    }
}
