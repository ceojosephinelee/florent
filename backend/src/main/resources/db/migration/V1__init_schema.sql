-- V1__init_schema.sql
-- 요청 생성 기능에 필요한 테이블 (user, buyer, seller, flower_shop, curation_request)

CREATE TABLE "user" (
    id              BIGSERIAL       PRIMARY KEY,
    kakao_id        VARCHAR(255)    NOT NULL UNIQUE,
    email           VARCHAR(255),
    password_hash   VARCHAR(255),
    role            VARCHAR(20)     NOT NULL,
    refresh_token   VARCHAR(512),
    refresh_token_expires_at TIMESTAMP,
    created_at      TIMESTAMP       NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT now()
);

CREATE TABLE buyer (
    id              BIGSERIAL       PRIMARY KEY,
    user_id         BIGINT          NOT NULL UNIQUE REFERENCES "user"(id),
    nick_name       VARCHAR(255),
    created_at      TIMESTAMP       NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT now()
);

CREATE TABLE seller (
    id              BIGSERIAL       PRIMARY KEY,
    user_id         BIGINT          NOT NULL UNIQUE REFERENCES "user"(id),
    created_at      TIMESTAMP       NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT now()
);

CREATE TABLE flower_shop (
    id              BIGSERIAL       PRIMARY KEY,
    seller_id       BIGINT          NOT NULL UNIQUE REFERENCES seller(id),
    name            VARCHAR(255)    NOT NULL,
    description     TEXT,
    phone           VARCHAR(50),
    address_text    VARCHAR(500)    NOT NULL,
    lat             DECIMAL(9,6)    NOT NULL,
    lng             DECIMAL(9,6)    NOT NULL,
    created_at      TIMESTAMP       NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT now()
);

CREATE TABLE curation_request (
    id                          BIGSERIAL       PRIMARY KEY,
    buyer_id                    BIGINT          NOT NULL REFERENCES buyer(id),
    status                      VARCHAR(20)     NOT NULL DEFAULT 'OPEN',
    purpose_tags_json           TEXT            NOT NULL,
    relation_tags_json          TEXT            NOT NULL,
    mood_tags_json              TEXT            NOT NULL,
    budget_tier                 VARCHAR(20)     NOT NULL,
    fulfillment_type            VARCHAR(20)     NOT NULL,
    fulfillment_date            DATE            NOT NULL,
    requested_time_slots_json   TEXT            NOT NULL,
    place_address_text          VARCHAR(500)    NOT NULL,
    place_lat                   DECIMAL(9,6)    NOT NULL,
    place_lng                   DECIMAL(9,6)    NOT NULL,
    created_at                  TIMESTAMP       NOT NULL DEFAULT now(),
    expires_at                  TIMESTAMP       NOT NULL,
    updated_at                  TIMESTAMP       NOT NULL DEFAULT now()
);
