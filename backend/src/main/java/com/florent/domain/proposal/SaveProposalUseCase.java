package com.florent.domain.proposal;

public interface SaveProposalUseCase {
    SaveProposalResult save(SaveProposalCommand command);
}
