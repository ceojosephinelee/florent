
# api-spec.md — Florent API 명세 (v2)

> AI는 이 문서에 정의된 엔드포인트, 요청/응답 DTO, 상태코드만 사용한다.
> 임의로 엔드포인트를 추가하거나 응답 구조를 변경하지 않는다.
> 변경이 필요하면 반드시 먼저 질문한다.

---

## 변경 이력

| 버전 | 변경 내용 |
|---|---|
| v1 | 초기 작성 |
| v2 | GET /buyer/home 추가, 제안 목록에 EXPIRED 노출 추가, 판매자 요청 목록에 CONFIRMED 마감 배지 추가, 구매자 타임슬롯 복수 선택 명시, 판매자 슬롯 자유 선택 명시 |

---

## 공통 규칙

### Base URL
```
/api/v1
```

### 인증
- 모든 API (auth 제외)는 `Authorization: Bearer {accessToken}` 헤더 필수
- Access Token 만료 시 `401` 반환 → 클라이언트가 `/api/v1/auth/reissue` 호출

### 공통 응답 래퍼
```json
// 성공
{ "success": true, "data": { } }

// 실패
{ "success": false, "error": { "code": "PROPOSAL_NOT_FOUND", "message": "제안을 찾을 수 없습니다." } }
```

### 공통 에러 코드
| code | HTTP | 설명 |
|---|---|---|
| `UNAUTHORIZED` | 401 | 토큰 없음 / 만료 |
| `FORBIDDEN` | 403 | 권한 없음 |
| `NOT_FOUND` | 404 | 리소스 없음 |
| `VALIDATION_ERROR` | 400 | 요청 필드 검증 실패 |
| `BUSINESS_ERROR` | 422 | 비즈니스 규칙 위반 |
| `INTERNAL_ERROR` | 500 | 서버 오류 |

---

## 1. 인증 (Auth)

### 1-1. 카카오 로그인 / 회원가입
```
POST /api/v1/auth/kakao
```
**Request** `{ "kakaoAccessToken": "string" }`

**Response 200**
```json
{
  "success": true,
  "data": {
    "accessToken": "string",
    "refreshToken": "string",
    "role": "BUYER | SELLER | null",
    "isNewUser": true
  }
}
```
> `isNewUser = true` 또는 `role = null`이면 역할 설정 화면으로 이동

---

### 1-2. 역할 설정 (최초 1회)
```
POST /api/v1/auth/role
```
**Request** `{ "role": "BUYER | SELLER" }`

**Response 200** `{ "success": true, "data": { "role": "BUYER" } }`

---

### 1-3. Access Token 재발급
```
POST /api/v1/auth/reissue
```
**Request** `{ "refreshToken": "string" }`

**Response 200** `{ "success": true, "data": { "accessToken": "string", "refreshToken": "string" } }`

---

### 1-4. 로그아웃
```
POST /api/v1/auth/logout
```
**Response 200** — `USER.refresh_token = null` 처리

---

## 2. 구매자 — 홈

### 2-1. 구매자 홈 조회
```
GET /api/v1/buyer/home
```

**Response 200**
```json
{
  "success": true,
  "data": {
    "activeRequest": {
      "requestId": 1,
      "status": "OPEN",
      "budgetTier": "TIER2",
      "fulfillmentType": "PICKUP",
      "fulfillmentDate": "2025-06-01",
      "expiresAt": "2025-06-03T14:00:00",
      "submittedProposalCount": 2,
      "draftProposalCount": 1
    },
    "recentNotifications": [
      {
        "notificationId": 1,
        "type": "PROPOSAL_ARRIVED",
        "title": "새로운 제안이 도착했어요!",
        "isRead": false,
        "createdAt": "2025-05-30T10:00:00"
      }
    ]
  }
}
```
> `activeRequest`: OPEN 상태 요청 중 최신 1건. 없으면 `null`.
> `recentNotifications`: 읽지 않은 알림 최대 3건.

---

## 3. 구매자 — 요청

### 3-1. 요청 생성
```
POST /api/v1/buyer/requests
```

**Request**
```json
{
  "purposeTags": ["생일", "깜짝선물"],
  "relationTags": ["연인"],
  "moodTags": ["화사한", "로맨틱"],
  "budgetTier": "TIER2",
  "fulfillmentType": "PICKUP",
  "fulfillmentDate": "2025-06-01",
  "requestedTimeSlots": [
    { "kind": "PICKUP_30M", "value": "14:00" },
    { "kind": "PICKUP_30M", "value": "14:30" }
  ],
  "placeAddressText": "서울시 마포구 ...",
  "placeLat": 37.123456,
  "placeLng": 127.123456
}
```
> `requestedTimeSlots`: **복수 선택 가능**
> `kind`: 픽업 → `PICKUP_30M` / 배송 → `DELIVERY_WINDOW`
> `value`: 픽업 → `"14:00"` 형식 / 배송 → `"MORNING" | "AFTERNOON" | "EVENING"`

**Response 201**
```json
{ "success": true, "data": { "requestId": 1, "status": "OPEN", "expiresAt": "2025-06-03T14:00:00" } }
```

**서버 처리 (단일 트랜잭션)**
1. CURATION_REQUEST 생성
2. 반경 2km 내 FLOWER_SHOP 조회 (Bounding Box + Haversine)
3. 각 판매자에게 `REQUEST_ARRIVED` NOTIFICATION + OUTBOX_EVENT 저장

---

### 3-2. 요청 목록 조회
```
GET /api/v1/buyer/requests?page=0&size=20
```

**Response 200**
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "requestId": 1,
        "status": "OPEN | EXPIRED | CONFIRMED",
        "budgetTier": "TIER2",
        "fulfillmentType": "PICKUP",
        "fulfillmentDate": "2025-06-01",
        "expiresAt": "2025-06-03T14:00:00",
        "draftProposalCount": 3,
        "submittedProposalCount": 2
      }
    ],
    "page": 0, "size": 20, "totalElements": 5, "totalPages": 1, "last": true
  }
}
```

---

### 3-3. 요청 상세 조회
```
GET /api/v1/buyer/requests/{requestId}
```

**Response 200**
```json
{
  "success": true,
  "data": {
    "requestId": 1,
    "status": "OPEN",
    "purposeTags": ["생일"],
    "relationTags": ["연인"],
    "moodTags": ["화사한"],
    "budgetTier": "TIER2",
    "fulfillmentType": "PICKUP",
    "fulfillmentDate": "2025-06-01",
    "requestedTimeSlots": [
      { "kind": "PICKUP_30M", "value": "14:00" },
      { "kind": "PICKUP_30M", "value": "14:30" }
    ],
    "placeAddressText": "서울시 마포구 ...",
    "placeLat": 37.123456,
    "placeLng": 127.123456,
    "expiresAt": "2025-06-03T14:00:00",
    "draftProposalCount": 3,
    "submittedProposalCount": 2
  }
}
```

---

## 4. 구매자 — 제안

### 4-1. 요청별 제안 목록 조회
```
GET /api/v1/buyer/requests/{requestId}/proposals
```

**Response 200**
```json
{
  "success": true,
  "data": [
    {
      "proposalId": 1,
      "shopName": "꽃집 이름",
      "conceptTitle": "봄 햇살 같은 화사함",
      "status": "SUBMITTED | EXPIRED",
      "expiresAt": "2025-06-02T14:00:00"
    }
  ]
}
```
> **노출 대상**: `SUBMITTED` + `EXPIRED` 모두 포함
> `EXPIRED`: 만료 배지 표시, 선택 버튼 비활성화 (클라이언트 처리)
> **가격 비노출** (목록)

---

### 4-2. 제안 상세 조회
```
GET /api/v1/buyer/proposals/{proposalId}
```

**Response 200**
```json
{
  "success": true,
  "data": {
    "proposalId": 1,
    "requestId": 1,
    "status": "SUBMITTED | EXPIRED",
    "shop": {
      "shopId": 1,
      "name": "꽃집 이름",
      "phone": "010-0000-0000",
      "addressText": "서울시 ..."
    },
    "conceptTitle": "봄 햇살 같은 화사함",
    "moodColors": ["핑크", "화이트"],
    "mainFlowers": ["장미", "튤립"],
    "wrappingStyle": ["리본"],
    "allergyNote": "없음",
    "careTips": "서늘한 곳에 보관",
    "description": "상세 큐레이션 설명",
    "imageUrls": ["https://s3.amazonaws.com/..."],
    "availableSlot": { "kind": "PICKUP_30M", "value": "14:00" },
    "expiresAt": "2025-06-02T14:00:00",
    "price": 35000
  }
}
```
> `price`는 응답 필드 **맨 마지막** 배치 (UI 표시 순서 가이드)

---

### 4-3. 제안 선택 (Mock 결제 + 예약 확정)
```
POST /api/v1/buyer/proposals/{proposalId}/select
```

**Request** `{ "idempotencyKey": "uuid-string" }`

**Response 201**
```json
{ "success": true, "data": { "reservationId": 1, "status": "CONFIRMED", "paymentStatus": "SUCCEEDED", "amount": 35000 } }
```

**서버 처리 (단일 트랜잭션)**
1. `request.status = CONFIRMED`
2. 선택된 `proposal.status = SELECTED`
3. 나머지 SUBMITTED `proposal.status = NOT_SELECTED`
4. `RESERVATION` 생성
5. `PAYMENT` 생성 (MOCK, SUCCEEDED)
6. 선택된 판매자에게 `RESERVATION_CONFIRMED` NOTIFICATION + OUTBOX_EVENT 저장

**에러**: 이미 CONFIRMED된 요청 / EXPIRED 제안 선택 / 중복 idempotencyKey → `BUSINESS_ERROR 422`

---

## 5. 구매자 — 예약

### 5-1. 예약 목록 조회
```
GET /api/v1/buyer/reservations
```

**Response 200**
```json
{
  "success": true,
  "data": [
    {
      "reservationId": 1,
      "status": "CONFIRMED",
      "shopName": "꽃집 이름",
      "fulfillmentType": "PICKUP",
      "fulfillmentDate": "2025-06-01",
      "fulfillmentSlot": { "kind": "PICKUP_30M", "value": "14:00" },
      "confirmedAt": "2025-05-30T10:00:00"
    }
  ]
}
```

---

### 5-2. 예약 상세 조회
```
GET /api/v1/buyer/reservations/{reservationId}
```

**Response 200**
```json
{
  "success": true,
  "data": {
    "reservationId": 1,
    "status": "CONFIRMED",
    "proposal": {
      "proposalId": 1,
      "conceptTitle": "봄 햇살 같은 화사함",
      "description": "상세 설명",
      "imageUrls": ["https://s3.amazonaws.com/..."],
      "price": 35000
    },
    "shop": {
      "shopId": 1,
      "name": "꽃집 이름",
      "phone": "010-0000-0000",
      "addressText": "서울시 ...",
      "lat": 37.123456,
      "lng": 127.123456
    },
    "fulfillmentType": "PICKUP",
    "fulfillmentDate": "2025-06-01",
    "fulfillmentSlot": { "kind": "PICKUP_30M", "value": "14:00" },
    "placeAddressText": "서울시 마포구 ...",
    "confirmedAt": "2025-05-30T10:00:00"
  }
}
```

---

## 6. 판매자 — 홈 대시보드

### 6-1. 홈 통계 조회
```
GET /api/v1/seller/home
```

**Response 200**
```json
{
  "success": true,
  "data": {
    "openRequestCount": 5,
    "draftProposalCount": 1,
    "submittedProposalCount": 3,
    "confirmedReservationCount": 2
  }
}
```

---

## 7. 판매자 — 요청함

### 7-1. 요청 목록 조회
```
GET /api/v1/seller/requests?page=0&size=20
```

**Response 200**
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "requestId": 1,
        "status": "OPEN | EXPIRED | CONFIRMED",
        "badgeLabel": "진행중 | 만료 | 마감",
        "purposeTags": ["생일"],
        "relationTags": ["연인"],
        "moodTags": ["화사한"],
        "budgetTier": "TIER2",
        "fulfillmentType": "PICKUP",
        "fulfillmentDate": "2025-06-01",
        "expiresAt": "2025-06-03T14:00:00",
        "myProposalStatus": "DRAFT | SUBMITTED | null"
      }
    ],
    "page": 0, "size": 20, "totalElements": 10, "totalPages": 1, "last": true
  }
}
```
> 판매자 가게 좌표 기준 반경 2km 내 요청 전체 노출 (OPEN + EXPIRED + CONFIRMED)
> `OPEN`: 제안 작성 가능 / `EXPIRED`: 만료 배지 / `CONFIRMED`: 마감 배지

---

### 7-2. 요청 상세 조회
```
GET /api/v1/seller/requests/{requestId}
```

**Response 200**
```json
{
  "success": true,
  "data": {
    "requestId": 1,
    "status": "OPEN",
    "purposeTags": ["생일"],
    "relationTags": ["연인"],
    "moodTags": ["화사한"],
    "budgetTier": "TIER2",
    "fulfillmentType": "PICKUP",
    "fulfillmentDate": "2025-06-01",
    "requestedTimeSlots": [
      { "kind": "PICKUP_30M", "value": "14:00" },
      { "kind": "PICKUP_30M", "value": "14:30" }
    ],
    "placeAddressText": "서울시 마포구 ...",
    "expiresAt": "2025-06-03T14:00:00",
    "myProposalId": null
  }
}
```
> `requestedTimeSlots`: 참고용. 판매자는 이와 무관하게 자유롭게 슬롯 선택.
> `myProposalId`: 이미 제안했으면 proposalId 반환, 없으면 null.

---

## 8. 판매자 — 제안

### 8-1. 제안 작성 시작 (DRAFT 생성)
```
POST /api/v1/seller/requests/{requestId}/proposals
```

**Response 201**
```json
{ "success": true, "data": { "proposalId": 1, "status": "DRAFT", "expiresAt": "2025-06-02T14:00:00" } }
```
**에러**: 요청 OPEN 아님 / 중복 제안 → `BUSINESS_ERROR 422`

---

### 8-2. 제안 임시저장
```
PATCH /api/v1/seller/proposals/{proposalId}
```

**Request**
```json
{
  "conceptTitle": "봄 햇살 같은 화사함",
  "moodColors": ["핑크", "화이트"],
  "mainFlowers": ["장미", "튤립"],
  "wrappingStyle": ["리본"],
  "allergyNote": "없음",
  "careTips": "서늘한 곳에 보관",
  "description": "상세 큐레이션 설명",
  "imageUrls": ["https://s3.amazonaws.com/..."],
  "availableSlot": { "kind": "PICKUP_30M", "value": "14:00" },
  "price": 35000
}
```
> `availableSlot`: 구매자 요청 슬롯과 무관하게 **자유 선택**
> `kind`: `PICKUP_30M | DELIVERY_WINDOW` / `value`: 픽업 `"14:00"` / 배송 `"MORNING" | "AFTERNOON" | "EVENING"`

**Response 200** `{ "success": true, "data": { "proposalId": 1, "status": "DRAFT" } }`

---

### 8-3. 제안 제출
```
POST /api/v1/seller/proposals/{proposalId}/submit
```

**Response 200**
```json
{ "success": true, "data": { "proposalId": 1, "status": "SUBMITTED", "submittedAt": "2025-05-30T10:00:00" } }
```

**필수 검증**: `description`, `availableSlot`, `price`

**서버 처리 (단일 트랜잭션)**
1. `proposal.status = SUBMITTED`, `submittedAt` 기록
2. 구매자에게 `PROPOSAL_ARRIVED` NOTIFICATION + OUTBOX_EVENT 저장

**에러**: 필수 필드 누락 → `VALIDATION_ERROR` / 요청 만료 후 제출 → `BUSINESS_ERROR`

---

### 8-4. 내 제안 목록 조회
```
GET /api/v1/seller/proposals?page=0&size=20
```

**Response 200**
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "proposalId": 1,
        "requestId": 1,
        "status": "DRAFT | SUBMITTED | EXPIRED | SELECTED | NOT_SELECTED",
        "conceptTitle": "봄 햇살 같은 화사함",
        "price": 35000,
        "expiresAt": "2025-06-02T14:00:00"
      }
    ],
    "page": 0, "size": 20, "totalElements": 5, "totalPages": 1, "last": true
  }
}
```

---

## 9. 판매자 — 예약

### 9-1. 예약 목록 조회
```
GET /api/v1/seller/reservations
```

**Response 200**
```json
{
  "success": true,
  "data": [
    {
      "reservationId": 1,
      "status": "CONFIRMED",
      "fulfillmentType": "PICKUP",
      "fulfillmentDate": "2025-06-01",
      "fulfillmentSlot": { "kind": "PICKUP_30M", "value": "14:00" },
      "buyerNickName": "닉네임",
      "confirmedAt": "2025-05-30T10:00:00"
    }
  ]
}
```

---

### 9-2. 예약 상세 조회
```
GET /api/v1/seller/reservations/{reservationId}
```

**Response 200**
```json
{
  "success": true,
  "data": {
    "reservationId": 1,
    "status": "CONFIRMED",
    "proposal": { "proposalId": 1, "conceptTitle": "봄 햇살 같은 화사함", "description": "상세 설명", "price": 35000 },
    "fulfillmentType": "PICKUP",
    "fulfillmentDate": "2025-06-01",
    "fulfillmentSlot": { "kind": "PICKUP_30M", "value": "14:00" },
    "placeAddressText": "서울시 마포구 ...",
    "placeLat": 37.123456,
    "placeLng": 127.123456,
    "confirmedAt": "2025-05-30T10:00:00"
  }
}
```

---

## 10. 꽃집

### 10-1. 꽃집 등록 (최초 1회)
```
POST /api/v1/seller/shop
```
**Request** `{ "name": "string", "description": "string", "phone": "string", "addressText": "string", "lat": 37.123456, "lng": 127.123456 }`

**Response 201** `{ "success": true, "data": { "shopId": 1, "name": "string" } }`

---

### 10-2. 꽃집 정보 조회
```
GET /api/v1/seller/shop
```

---

### 10-3. 꽃집 정보 수정
```
PATCH /api/v1/seller/shop
```
**Request** — 변경할 필드만 포함

---

## 11. S3 이미지 업로드

### 11-1. Presigned URL 발급
```
POST /api/v1/images/presigned-url
```
**Request** `{ "fileName": "flower.jpg", "contentType": "image/jpeg", "target": "PROPOSAL" }`

**Response 200**
```json
{ "success": true, "data": { "presignedUrl": "https://s3.amazonaws.com/...", "imageUrl": "https://s3.amazonaws.com/..." } }
```

**플로우**: presigned-url 발급 → S3에 직접 PUT → imageUrl을 제안 임시저장 API에 포함

---

## 12. 알림

### 12-1. 알림 목록 조회
```
GET /api/v1/notifications?page=0&size=20
```

**Response 200**
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "notificationId": 1,
        "type": "PROPOSAL_ARRIVED | REQUEST_ARRIVED | RESERVATION_CONFIRMED",
        "referenceType": "PROPOSAL | REQUEST | RESERVATION",
        "referenceId": 1,
        "title": "새로운 제안이 도착했어요!",
        "isRead": false,
        "createdAt": "2025-05-30T10:00:00"
      }
    ],
    "page": 0, "size": 20, "totalElements": 3, "totalPages": 1, "last": true
  }
}
```

---

### 12-2. 알림 읽음 처리
```
PATCH /api/v1/notifications/{notificationId}/read
```
**Response 200** `{ "success": true, "data": { "notificationId": 1, "isRead": true } }`

---

## 13. FCM 디바이스 토큰

### 13-1. FCM 토큰 등록/갱신
```
POST /api/v1/devices
```
**Request** `{ "fcmToken": "string", "platform": "IOS | ANDROID" }`

**Response 200** `{ "success": true, "data": { "deviceId": 1 } }`

> fcmToken 존재 시 UPDATE (`is_active = true`). 앱 실행 시마다 호출.

---

## 엔드포인트 전체 목록 (29개)

| Method | Path | 설명 | 역할 |
|---|---|---|---|
| POST | `/auth/kakao` | 카카오 로그인/회원가입 | 공통 |
| POST | `/auth/role` | 역할 설정 | 공통 |
| POST | `/auth/reissue` | 토큰 재발급 | 공통 |
| POST | `/auth/logout` | 로그아웃 | 공통 |
| GET | `/buyer/home` | 구매자 홈 | 구매자 |
| POST | `/buyer/requests` | 요청 생성 | 구매자 |
| GET | `/buyer/requests` | 요청 목록 | 구매자 |
| GET | `/buyer/requests/{id}` | 요청 상세 | 구매자 |
| GET | `/buyer/requests/{id}/proposals` | 요청별 제안 목록 | 구매자 |
| GET | `/buyer/proposals/{id}` | 제안 상세 | 구매자 |
| POST | `/buyer/proposals/{id}/select` | 제안 선택 + Mock 결제 | 구매자 |
| GET | `/buyer/reservations` | 예약 목록 | 구매자 |
| GET | `/buyer/reservations/{id}` | 예약 상세 | 구매자 |
| GET | `/seller/home` | 판매자 홈 대시보드 | 판매자 |
| GET | `/seller/requests` | 요청함 목록 | 판매자 |
| GET | `/seller/requests/{id}` | 요청 상세 | 판매자 |
| POST | `/seller/requests/{id}/proposals` | 제안 작성 시작 | 판매자 |
| PATCH | `/seller/proposals/{id}` | 제안 임시저장 | 판매자 |
| POST | `/seller/proposals/{id}/submit` | 제안 제출 | 판매자 |
| GET | `/seller/proposals` | 내 제안 목록 | 판매자 |
| GET | `/seller/reservations` | 예약 목록 | 판매자 |
| GET | `/seller/reservations/{id}` | 예약 상세 | 판매자 |
| POST | `/seller/shop` | 꽃집 등록 | 판매자 |
| GET | `/seller/shop` | 꽃집 정보 조회 | 판매자 |
| PATCH | `/seller/shop` | 꽃집 정보 수정 | 판매자 |
| POST | `/images/presigned-url` | S3 Presigned URL 발급 | 공통 |
| GET | `/notifications` | 알림 목록 | 공통 |
| PATCH | `/notifications/{id}/read` | 알림 읽음 처리 | 공통 |
| POST | `/devices` | FCM 토큰 등록/갱신 | 공통 |
