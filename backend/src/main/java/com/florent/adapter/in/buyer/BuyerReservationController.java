package com.florent.adapter.in.buyer;

import com.florent.adapter.in.buyer.dto.BuyerReservationDetailResponse;
import com.florent.adapter.in.buyer.dto.BuyerReservationSummaryResponse;
import com.florent.adapter.in.buyer.dto.ConfirmReservationRequest;
import com.florent.adapter.in.buyer.dto.ConfirmReservationResponse;
import com.florent.common.response.ApiResponse;
import com.florent.common.security.UserPrincipal;
import com.florent.domain.reservation.BuyerReservationDetailResult;
import com.florent.domain.reservation.BuyerReservationSummaryResult;
import com.florent.domain.reservation.ConfirmReservationCommand;
import com.florent.domain.reservation.ConfirmReservationResult;
import com.florent.domain.reservation.ConfirmReservationUseCase;
import com.florent.domain.reservation.GetBuyerReservationDetailUseCase;
import com.florent.domain.reservation.GetBuyerReservationListUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/buyer")
@RequiredArgsConstructor
public class BuyerReservationController {

    private final ConfirmReservationUseCase confirmReservationUseCase;
    private final GetBuyerReservationListUseCase getBuyerReservationListUseCase;
    private final GetBuyerReservationDetailUseCase getBuyerReservationDetailUseCase;

    @PostMapping("/proposals/{proposalId}/select")
    public ResponseEntity<ApiResponse<ConfirmReservationResponse>> confirmReservation(
            @PathVariable Long proposalId,
            @RequestBody @Valid ConfirmReservationRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        ConfirmReservationResult result = confirmReservationUseCase.confirm(
                new ConfirmReservationCommand(
                        principal.getBuyerId(), proposalId, request.idempotencyKey()));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(ConfirmReservationResponse.from(result)));
    }

    @GetMapping("/reservations")
    public ResponseEntity<ApiResponse<List<BuyerReservationSummaryResponse>>> getReservationList(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        List<BuyerReservationSummaryResult> results =
                getBuyerReservationListUseCase.getList(principal.getBuyerId());
        List<BuyerReservationSummaryResponse> response = results.stream()
                .map(BuyerReservationSummaryResponse::from)
                .toList();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/reservations/{reservationId}")
    public ResponseEntity<ApiResponse<BuyerReservationDetailResponse>> getReservationDetail(
            @PathVariable Long reservationId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        BuyerReservationDetailResult result =
                getBuyerReservationDetailUseCase.getDetail(
                        reservationId, principal.getBuyerId());
        return ResponseEntity.ok(ApiResponse.success(
                BuyerReservationDetailResponse.from(result)));
    }
}
