package com.florent.adapter.in.buyer;

import com.florent.adapter.in.buyer.dto.CreateRequestRequest;
import com.florent.adapter.in.buyer.dto.CreateRequestResponse;
import com.florent.common.response.ApiResponse;
import com.florent.common.security.UserPrincipal;
import com.florent.domain.request.CreateRequestResult;
import com.florent.domain.request.CreateRequestUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/buyer/requests")
@RequiredArgsConstructor
public class BuyerRequestController {

    private final CreateRequestUseCase createRequestUseCase;

    @PostMapping
    public ResponseEntity<ApiResponse<CreateRequestResponse>> create(
            @RequestBody @Valid CreateRequestRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        CreateRequestResult result = createRequestUseCase.create(
                request.toCommand(principal.getBuyerId()));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(CreateRequestResponse.from(result)));
    }
}
