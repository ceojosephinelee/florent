package com.florent.adapter.in.seller;

import com.florent.adapter.in.seller.dto.SaveProposalRequest;
import com.florent.adapter.in.seller.dto.SaveProposalResponse;
import com.florent.adapter.in.seller.dto.SellerProposalListResponse;
import com.florent.adapter.in.seller.dto.StartProposalResponse;
import com.florent.adapter.in.seller.dto.SubmitProposalResponse;
import com.florent.common.response.ApiResponse;
import com.florent.common.security.UserPrincipal;
import com.florent.domain.proposal.GetSellerProposalListUseCase;
import com.florent.domain.proposal.SaveProposalResult;
import com.florent.domain.proposal.SaveProposalUseCase;
import com.florent.domain.proposal.SellerProposalListResult;
import com.florent.domain.proposal.StartProposalCommand;
import com.florent.domain.proposal.StartProposalResult;
import com.florent.domain.proposal.StartProposalUseCase;
import com.florent.domain.proposal.SubmitProposalCommand;
import com.florent.domain.proposal.SubmitProposalResult;
import com.florent.domain.proposal.SubmitProposalUseCase;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/seller")
@RequiredArgsConstructor
@Validated
public class SellerProposalController {

    private final StartProposalUseCase startProposalUseCase;
    private final SaveProposalUseCase saveProposalUseCase;
    private final SubmitProposalUseCase submitProposalUseCase;
    private final GetSellerProposalListUseCase getSellerProposalListUseCase;

    @PostMapping("/requests/{requestId}/proposals")
    public ResponseEntity<ApiResponse<StartProposalResponse>> start(
            @PathVariable Long requestId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        StartProposalResult result = startProposalUseCase.start(
                new StartProposalCommand(requestId, principal.getSellerId()));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(StartProposalResponse.from(result)));
    }

    @PatchMapping("/proposals/{proposalId}")
    public ResponseEntity<ApiResponse<SaveProposalResponse>> save(
            @PathVariable Long proposalId,
            @RequestBody SaveProposalRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        SaveProposalResult result = saveProposalUseCase.save(
                request.toCommand(proposalId, principal.getSellerId()));
        return ResponseEntity.ok(ApiResponse.success(SaveProposalResponse.from(result)));
    }

    @PostMapping("/proposals/{proposalId}/submit")
    public ResponseEntity<ApiResponse<SubmitProposalResponse>> submit(
            @PathVariable Long proposalId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        SubmitProposalResult result = submitProposalUseCase.submit(
                new SubmitProposalCommand(proposalId, principal.getSellerId()));
        return ResponseEntity.ok(ApiResponse.success(SubmitProposalResponse.from(result)));
    }

    @GetMapping("/proposals")
    public ResponseEntity<ApiResponse<SellerProposalListResponse>> getMyProposals(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        SellerProposalListResult result = getSellerProposalListUseCase.getMyProposals(
                principal.getSellerId(), page, size);
        return ResponseEntity.ok(ApiResponse.success(SellerProposalListResponse.from(result)));
    }
}
