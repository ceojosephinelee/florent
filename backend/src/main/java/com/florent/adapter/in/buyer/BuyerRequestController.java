package com.florent.adapter.in.buyer;

import com.florent.adapter.in.buyer.dto.CreateRequestRequest;
import com.florent.adapter.in.buyer.dto.CreateRequestResponse;
import com.florent.adapter.in.buyer.dto.RequestDetailResponse;
import com.florent.adapter.in.buyer.dto.RequestListResponse;
import com.florent.common.response.ApiResponse;
import com.florent.common.security.UserPrincipal;
import com.florent.domain.request.CreateRequestResult;
import com.florent.domain.request.CreateRequestUseCase;
import com.florent.domain.request.GetRequestDetailUseCase;
import com.florent.domain.request.GetRequestListUseCase;
import com.florent.domain.request.RequestDetailResult;
import com.florent.domain.request.RequestListResult;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/buyer/requests")
@RequiredArgsConstructor
@Validated
public class BuyerRequestController {

    private final CreateRequestUseCase createRequestUseCase;
    private final GetRequestListUseCase getRequestListUseCase;
    private final GetRequestDetailUseCase getRequestDetailUseCase;

    @PostMapping
    public ResponseEntity<ApiResponse<CreateRequestResponse>> create(
            @RequestBody @Valid CreateRequestRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        log.info("[POST /buyer/requests] 요청 진입 — buyerId={}, fulfillmentType={}",
                principal.getBuyerId(), request.fulfillmentType());
        CreateRequestResult result = createRequestUseCase.create(
                request.toCommand(principal.getBuyerId()));
        log.info("[POST /buyer/requests] 요청 생성 완료 — requestId={}", result.requestId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(CreateRequestResponse.from(result)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<RequestListResponse>> getList(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        RequestListResult result = getRequestListUseCase.getList(
                principal.getBuyerId(), page, size);
        return ResponseEntity.ok(ApiResponse.success(RequestListResponse.from(result)));
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ApiResponse<RequestDetailResponse>> getDetail(
            @PathVariable Long requestId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        RequestDetailResult result = getRequestDetailUseCase.getDetail(
                requestId, principal.getBuyerId());
        return ResponseEntity.ok(ApiResponse.success(RequestDetailResponse.from(result)));
    }
}
