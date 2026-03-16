package com.florent.adapter.in.seller;

import com.florent.adapter.in.seller.dto.RegisterShopRequest;
import com.florent.adapter.in.seller.dto.RegisterShopResponse;
import com.florent.adapter.in.seller.dto.ShopDetailResponse;
import com.florent.adapter.in.seller.dto.UpdateShopRequest;
import com.florent.common.response.ApiResponse;
import com.florent.common.security.UserPrincipal;
import com.florent.domain.shop.GetShopUseCase;
import com.florent.domain.shop.RegisterShopResult;
import com.florent.domain.shop.RegisterShopUseCase;
import com.florent.domain.shop.ShopDetailResult;
import com.florent.domain.shop.UpdateShopUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/seller/shop")
@RequiredArgsConstructor
public class SellerShopController {

    private final RegisterShopUseCase registerShopUseCase;
    private final GetShopUseCase getShopUseCase;
    private final UpdateShopUseCase updateShopUseCase;

    @PostMapping
    public ResponseEntity<ApiResponse<RegisterShopResponse>> register(
            @RequestBody @Valid RegisterShopRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        RegisterShopResult result = registerShopUseCase.register(
                principal.getSellerId(), request.toCommand());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(RegisterShopResponse.from(result)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<ShopDetailResponse>> getShop(
            @AuthenticationPrincipal UserPrincipal principal) {
        ShopDetailResult result = getShopUseCase.getShop(principal.getSellerId());
        return ResponseEntity.ok(ApiResponse.success(ShopDetailResponse.from(result)));
    }

    @PatchMapping
    public ResponseEntity<ApiResponse<ShopDetailResponse>> update(
            @RequestBody UpdateShopRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        ShopDetailResult result = updateShopUseCase.update(
                principal.getSellerId(), request.toCommand());
        return ResponseEntity.ok(ApiResponse.success(ShopDetailResponse.from(result)));
    }
}
