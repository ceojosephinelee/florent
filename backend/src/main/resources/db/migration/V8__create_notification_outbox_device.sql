-- V8__create_notification_outbox_device.sql
-- 알림, Outbox, FCM 디바이스 테이블

CREATE TABLE notification (
    id              BIGSERIAL       PRIMARY KEY,
    user_id         BIGINT          NOT NULL REFERENCES "user"(id),
    type            VARCHAR(30)     NOT NULL,
    reference_type  VARCHAR(30)     NOT NULL,
    reference_id    BIGINT          NOT NULL,
    title           VARCHAR(255)    NOT NULL,
    body            VARCHAR(500),
    is_read         BOOLEAN         NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP       NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT now()
);

CREATE INDEX idx_notification_user_id ON notification(user_id);
CREATE INDEX idx_notification_user_created ON notification(user_id, created_at DESC);

CREATE TABLE outbox_event (
    id              BIGSERIAL       PRIMARY KEY,
    notification_id BIGINT          NOT NULL REFERENCES notification(id),
    status          VARCHAR(20)     NOT NULL DEFAULT 'PENDING',
    dedup_key       VARCHAR(255)    NOT NULL UNIQUE,
    attempt_count   INT             NOT NULL DEFAULT 0,
    available_at    TIMESTAMP       NOT NULL DEFAULT now(),
    created_at      TIMESTAMP       NOT NULL DEFAULT now()
);

CREATE INDEX idx_outbox_event_status_available ON outbox_event(status, available_at);

CREATE TABLE user_device (
    id              BIGSERIAL       PRIMARY KEY,
    user_id         BIGINT          NOT NULL REFERENCES "user"(id),
    platform        VARCHAR(20)     NOT NULL,
    fcm_token       VARCHAR(512)    NOT NULL UNIQUE,
    is_active       BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP       NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT now()
);

CREATE INDEX idx_user_device_user_id ON user_device(user_id);