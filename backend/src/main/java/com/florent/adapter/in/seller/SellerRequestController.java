package com.florent.adapter.in.seller;

import com.florent.adapter.in.seller.dto.SellerRequestDetailResponse;
import com.florent.adapter.in.seller.dto.SellerRequestListResponse;
import com.florent.common.response.ApiResponse;
import com.florent.common.security.UserPrincipal;
import com.florent.domain.request.GetSellerRequestDetailUseCase;
import com.florent.domain.request.GetSellerRequestListUseCase;
import com.florent.domain.request.SellerRequestDetailResult;
import com.florent.domain.request.SellerRequestListResult;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/seller/requests")
@RequiredArgsConstructor
@Validated
public class SellerRequestController {

    private final GetSellerRequestListUseCase getSellerRequestListUseCase;
    private final GetSellerRequestDetailUseCase getSellerRequestDetailUseCase;

    @GetMapping
    public ResponseEntity<ApiResponse<SellerRequestListResponse>> getList(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        SellerRequestListResult result = getSellerRequestListUseCase.getSellerRequests(
                principal.getSellerId(), page, size);
        return ResponseEntity.ok(ApiResponse.success(SellerRequestListResponse.from(result)));
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ApiResponse<SellerRequestDetailResponse>> getDetail(
            @PathVariable Long requestId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        SellerRequestDetailResult result = getSellerRequestDetailUseCase.getSellerRequestDetail(
                requestId, principal.getSellerId());
        return ResponseEntity.ok(ApiResponse.success(SellerRequestDetailResponse.from(result)));
    }
}
