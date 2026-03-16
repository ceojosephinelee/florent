package com.florent.support;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

public final class TestFixtures {

    public static final Clock FIXED_CLOCK =
            Clock.fixed(Instant.parse("2026-03-15T10:00:00Z"), ZoneId.of("Asia/Seoul"));

    private TestFixtures() {}
}
