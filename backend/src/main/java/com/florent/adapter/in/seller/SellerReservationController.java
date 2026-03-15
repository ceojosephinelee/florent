package com.florent.adapter.in.seller;

import com.florent.adapter.in.seller.dto.SellerReservationDetailResponse;
import com.florent.adapter.in.seller.dto.SellerReservationSummaryResponse;
import com.florent.common.response.ApiResponse;
import com.florent.common.security.UserPrincipal;
import com.florent.domain.reservation.GetSellerReservationDetailUseCase;
import com.florent.domain.reservation.GetSellerReservationListUseCase;
import com.florent.domain.reservation.SellerReservationDetailResult;
import com.florent.domain.reservation.SellerReservationSummaryResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/seller")
@RequiredArgsConstructor
public class SellerReservationController {

    private final GetSellerReservationListUseCase getSellerReservationListUseCase;
    private final GetSellerReservationDetailUseCase getSellerReservationDetailUseCase;

    @GetMapping("/reservations")
    public ResponseEntity<ApiResponse<List<SellerReservationSummaryResponse>>> getReservationList(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        List<SellerReservationSummaryResult> results =
                getSellerReservationListUseCase.getList(principal.getSellerId());
        List<SellerReservationSummaryResponse> response = results.stream()
                .map(SellerReservationSummaryResponse::from)
                .toList();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/reservations/{reservationId}")
    public ResponseEntity<ApiResponse<SellerReservationDetailResponse>> getReservationDetail(
            @PathVariable Long reservationId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        SellerReservationDetailResult result =
                getSellerReservationDetailUseCase.getDetail(
                        reservationId, principal.getSellerId());
        return ResponseEntity.ok(ApiResponse.success(
                SellerReservationDetailResponse.from(result)));
    }
}
