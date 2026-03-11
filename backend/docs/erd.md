# erd.md — Florent ERD 명세

> ERD에 없는 컬럼/테이블을 임의로 추가하지 않는다.
> 변경이 필요하면 반드시 먼저 질문한다.


## ERD 다이어그램

```mermaid
erDiagram
    USER {
        bigint id PK
        varchar kakao_id "UNIQUE, NOT NULL — 카카오 OAuth 식별자"
        varchar email "NULLABLE (카카오 미제공 가능)"
        varchar password_hash "NULLABLE (카카오 로그인 불필요)"
        varchar role "BUYER|SELLER, NOT NULL"
        varchar refresh_token "NULLABLE"
        datetime refresh_token_expires_at "NULLABLE"
        datetime created_at "NOT NULL"
        datetime updated_at "NOT NULL"
    }

    BUYER {
        bigint id PK
        bigint user_id "FK UNIQUE, NOT NULL -> USER.id"
        varchar nick_name "NULLABLE"
        datetime created_at "NOT NULL"
        datetime updated_at "NOT NULL"
    }

    SELLER {
        bigint id PK
        bigint user_id "FK UNIQUE, NOT NULL -> USER.id"
        datetime created_at "NOT NULL"
        datetime updated_at "NOT NULL"
    }

    FLOWER_SHOP {
        bigint id PK
        bigint seller_id "FK UNIQUE, NOT NULL -> SELLER.id (MVP: 1 seller = 1 shop)"
        varchar name "NOT NULL"
        text description "NULLABLE"
        varchar phone "NULLABLE"
        varchar address_text "NOT NULL"
        decimal lat "NOT NULL DECIMAL(9,6)"
        decimal lng "NOT NULL DECIMAL(9,6)"
        datetime created_at "NOT NULL"
        datetime updated_at "NOT NULL"
    }

    CURATION_REQUEST {
        bigint id PK
        bigint buyer_id "FK, NOT NULL -> BUYER.id"
        varchar status "OPEN|EXPIRED|CONFIRMED, NOT NULL DEFAULT OPEN"
        text purpose_tags_json "NOT NULL — 복수 선택/입력 가능"
        text relation_tags_json "NOT NULL — 복수 선택/입력 가능"
        text mood_tags_json "NOT NULL — 복수 선택/입력 가능"
        varchar budget_tier "TIER1|TIER2|TIER3|TIER4, NOT NULL"
        varchar fulfillment_type "PICKUP|DELIVERY, NOT NULL"
        date fulfillment_date "NOT NULL"
        text requested_time_slots_json "NOT NULL — 복수 선택 가능 [{kind,value},...]"
        varchar place_address_text "NOT NULL"
        decimal place_lat "NOT NULL DECIMAL(9,6)"
        decimal place_lng "NOT NULL DECIMAL(9,6)"
        datetime created_at "NOT NULL"
        datetime expires_at "NOT NULL — created_at + 48h"
        datetime updated_at "NOT NULL"
    }

    PROPOSAL {
        bigint id PK
        bigint request_id "FK, NOT NULL -> CURATION_REQUEST.id (요청당 가게 1개 제안, MVP)"
        bigint flower_shop_id "FK, NOT NULL -> FLOWER_SHOP.id"
        varchar status "DRAFT|SUBMITTED|EXPIRED|SELECTED|NOT_SELECTED, NOT NULL DEFAULT DRAFT"
        varchar concept_title "NULLABLE"
        text mood_color_json "NULLABLE"
        text main_flowers_json "NULLABLE"
        text wrapping_style_json "NULLABLE"
        varchar allergy_note "NULLABLE"
        text care_tips "NULLABLE"
        text description "NOT NULL — 필수"
        text image_urls_json "NULLABLE — S3 이미지 URL 목록"
        varchar available_slot_kind "NOT NULL — PICKUP_30M|DELIVERY_WINDOW (단 1개)"
        varchar available_slot_value "NOT NULL — ex) 14:00 or MORNING (단 1개)"
        decimal price "NOT NULL CHECK > 0"
        datetime created_at "NOT NULL"
        datetime expires_at "NOT NULL — created_at + 24h"
        datetime submitted_at "NULLABLE — 제출 시점"
        datetime updated_at "NOT NULL"
    }

    RESERVATION {
        bigint id PK
        bigint request_id "FK UNIQUE, NOT NULL -> CURATION_REQUEST.id (요청당 1개 예약)"
        bigint proposal_id "FK UNIQUE, NOT NULL -> PROPOSAL.id (제안당 1개 예약)"
        varchar status "CONFIRMED, NOT NULL"
        varchar fulfillment_type "PICKUP|DELIVERY, NOT NULL"
        date fulfillment_date "NOT NULL"
        varchar fulfillment_slot_kind "NOT NULL — PICKUP_30M|DELIVERY_WINDOW"
        varchar fulfillment_slot_value "NOT NULL — ex) 14:00 or MORNING"
        varchar place_address_text "NOT NULL"
        decimal place_lat "NOT NULL DECIMAL(9,6)"
        decimal place_lng "NOT NULL DECIMAL(9,6)"
        datetime confirmed_at "NOT NULL"
        datetime created_at "NOT NULL"
        datetime updated_at "NOT NULL"
    }

    PAYMENT {
        bigint id PK
        bigint reservation_id "FK UNIQUE, NOT NULL -> RESERVATION.id"
        varchar provider "MOCK, NOT NULL"
        varchar status "SUCCEEDED|FAILED, NOT NULL"
        decimal amount "NOT NULL — 스냅샷: proposal.price"
        varchar currency "KRW, NOT NULL"
        varchar idempotency_key "UNIQUE, NOT NULL — 중복 결제 방지"
        datetime paid_at "NULLABLE"
        datetime created_at "NOT NULL"
    }

    NOTIFICATION {
        bigint id PK
        bigint user_id "FK, NOT NULL -> USER.id"
        varchar type "REQUEST_ARRIVED|PROPOSAL_ARRIVED|RESERVATION_CONFIRMED, NOT NULL"
        varchar reference_type "REQUEST|PROPOSAL|RESERVATION, NOT NULL"
        bigint reference_id "NOT NULL"
        varchar title "NOT NULL"
        boolean is_read "NOT NULL DEFAULT FALSE"
        datetime created_at "NOT NULL"
        datetime updated_at "NOT NULL"
    }

    OUTBOX_EVENT {
        bigint id PK
        bigint notification_id "FK, NOT NULL -> NOTIFICATION.id"
        varchar status "PENDING|SENT|FAILED, NOT NULL DEFAULT PENDING"
        varchar dedup_key "UNIQUE, NOT NULL — 중복 전송 방지"
        int attempt_count "NOT NULL DEFAULT 0"
        datetime available_at "NOT NULL DEFAULT now() — 다음 재시도 가능 시간"
        datetime created_at "NOT NULL"
    }

    USER_DEVICE {
        bigint id PK
        bigint user_id "FK, NOT NULL -> USER.id"
        varchar platform "IOS|ANDROID, NOT NULL"
        varchar fcm_token "UNIQUE, NOT NULL"
        boolean is_active "NOT NULL DEFAULT TRUE"
        datetime created_at "NOT NULL"
        datetime updated_at "NOT NULL"
    }

    USER ||--o| BUYER : has
    USER ||--o| SELLER : has
    SELLER ||--|| FLOWER_SHOP : owns
    BUYER ||--o{ CURATION_REQUEST : creates
    CURATION_REQUEST ||--o{ PROPOSAL : receives
    FLOWER_SHOP ||--o{ PROPOSAL : submits
    CURATION_REQUEST ||--o| RESERVATION : results_in
    PROPOSAL ||--o| RESERVATION : selected_for
    RESERVATION ||--o| PAYMENT : paid_by
    USER ||--o{ NOTIFICATION : receives
    USER ||--o{ USER_DEVICE : has
    NOTIFICATION ||--o{ OUTBOX_EVENT : push_jobs
```

---


