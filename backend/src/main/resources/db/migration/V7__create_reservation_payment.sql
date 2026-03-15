-- V7__create_reservation_payment.sql
-- 예약 확정 + Mock 결제 테이블

CREATE TABLE reservation (
    id                      BIGSERIAL       PRIMARY KEY,
    request_id              BIGINT          NOT NULL UNIQUE REFERENCES curation_request(id),
    proposal_id             BIGINT          NOT NULL UNIQUE REFERENCES proposal(id),
    status                  VARCHAR(20)     NOT NULL,
    fulfillment_type        VARCHAR(20)     NOT NULL,
    fulfillment_date        DATE            NOT NULL,
    fulfillment_slot_kind   VARCHAR(30)     NOT NULL,
    fulfillment_slot_value  VARCHAR(30)     NOT NULL,
    place_address_text      VARCHAR(500)    NOT NULL,
    place_lat               DECIMAL(9,6)    NOT NULL,
    place_lng               DECIMAL(9,6)    NOT NULL,
    confirmed_at            TIMESTAMP       NOT NULL,
    created_at              TIMESTAMP       NOT NULL DEFAULT now(),
    updated_at              TIMESTAMP       NOT NULL DEFAULT now()
);

CREATE TABLE payment (
    id                  BIGSERIAL       PRIMARY KEY,
    reservation_id      BIGINT          NOT NULL UNIQUE REFERENCES reservation(id),
    provider            VARCHAR(20)     NOT NULL,
    status              VARCHAR(20)     NOT NULL,
    amount              DECIMAL         NOT NULL,
    currency            VARCHAR(10)     NOT NULL DEFAULT 'KRW',
    idempotency_key     VARCHAR(255)    NOT NULL UNIQUE,
    paid_at             TIMESTAMP,
    created_at          TIMESTAMP       NOT NULL DEFAULT now()
);

CREATE INDEX idx_reservation_request_id ON reservation(request_id);
CREATE INDEX idx_reservation_proposal_id ON reservation(proposal_id);
CREATE INDEX idx_payment_reservation_id ON payment(reservation_id);
CREATE INDEX idx_payment_idempotency_key ON payment(idempotency_key);
