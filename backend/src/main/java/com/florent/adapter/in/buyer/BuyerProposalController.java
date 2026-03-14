package com.florent.adapter.in.buyer;

import com.florent.adapter.in.buyer.dto.ProposalDetailResponse;
import com.florent.adapter.in.buyer.dto.ProposalSummaryResponse;
import com.florent.common.response.ApiResponse;
import com.florent.common.security.UserPrincipal;
import com.florent.domain.proposal.GetProposalDetailUseCase;
import com.florent.domain.proposal.GetProposalListUseCase;
import com.florent.domain.proposal.ProposalDetail;
import com.florent.domain.proposal.ProposalSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/buyer")
@RequiredArgsConstructor
public class BuyerProposalController {

    private final GetProposalListUseCase getProposalListUseCase;
    private final GetProposalDetailUseCase getProposalDetailUseCase;

    @GetMapping("/requests/{requestId}/proposals")
    public ResponseEntity<ApiResponse<List<ProposalSummaryResponse>>> getProposalList(
            @PathVariable Long requestId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        List<ProposalSummary> summaries = getProposalListUseCase.getProposalsByRequestId(
                requestId, principal.getBuyerId());
        List<ProposalSummaryResponse> response = summaries.stream()
                .map(ProposalSummaryResponse::from)
                .toList();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/proposals/{proposalId}")
    public ResponseEntity<ApiResponse<ProposalDetailResponse>> getProposalDetail(
            @PathVariable Long proposalId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        ProposalDetail detail = getProposalDetailUseCase.getProposalDetail(
                proposalId, principal.getBuyerId());
        return ResponseEntity.ok(ApiResponse.success(ProposalDetailResponse.from(detail)));
    }
}
