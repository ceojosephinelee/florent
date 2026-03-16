package com.florent.adapter.in.buyer;

import com.florent.common.response.ApiResponse;
import com.florent.common.security.UserPrincipal;
import com.florent.domain.buyer.BuyerProfileResult;
import com.florent.domain.buyer.GetBuyerProfileUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/buyer")
@RequiredArgsConstructor
public class BuyerProfileController {

    private final GetBuyerProfileUseCase getBuyerProfileUseCase;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<BuyerProfileResponse>> getProfile(
            @AuthenticationPrincipal UserPrincipal principal) {
        BuyerProfileResult result = getBuyerProfileUseCase.getProfile(
                principal.getBuyerId(), principal.getUserId());
        return ResponseEntity.ok(ApiResponse.success(BuyerProfileResponse.from(result)));
    }
}
