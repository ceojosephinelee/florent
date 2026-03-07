-- Initial baseline for the bootstrap phase.
-- Keep this migration intentionally minimal.
CREATE TABLE IF NOT EXISTS bootstrap_marker (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);
