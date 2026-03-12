package com.florent.domain.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BudgetTier {
    TIER1("작게"),
    TIER2("보통"),
    TIER3("크게"),
    TIER4("프리미엄");

    private final String label;
}
