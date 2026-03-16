package com.florent.domain.proposal;

public interface SubmitProposalUseCase {
    SubmitProposalResult submit(SubmitProposalCommand command);
}
