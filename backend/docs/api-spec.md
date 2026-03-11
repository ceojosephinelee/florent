# api-spec.md — Florent API 명세

> 이 명세를 기준으로 Controller, DTO, Service를 작성한다.
> 명세에 없는 엔드포인트를 임의로 추가하지 않는다.
> 변경이 필요하면 반드시 먼저 질문한다.

---

## 공통 규칙

- Base URL: `/api/v1`
- 인증: 모든 API (auth 제외) `Authorization: Bearer {accessToken}` 필수
- 응답 래퍼: 항상 `ApiResponse<T>` (`success` + `data` / `error`)
- 페이지네이션 응답 공통 구조: `content[]`, `page`, `size`, `totalElements`, `totalPages`, `last`

| 에러 코드 | HTTP | 설명 |
|---|---|---|
| `UNAUTHORIZED` | 401 | 토큰 없음 / 만료 |
| `FORBIDDEN` | 403 | 권한 없음 |
| `VALIDATION_ERROR` | 400 | 요청 필드 검증 실패 |
| `BUSINESS_ERROR` | 422 | 비즈니스 규칙 위반 |

---

## 1. 인증 (Auth)

### 1-1. 카카오 로그인 / 회원가입
```
POST /api/v1/auth/kakao
```
Request:
```json
{ "kakaoAccessToken": "string" }
```
Response 200:
```json
{
  "accessToken": "string",
  "refreshToken": "string",
  "role": "BUYER | SELLER | null",
  "isNewUser": true
}
```
- 신규 유저: USER 생성, `isNewUser=true` → 클라이언트가 `/auth/role`로 이동
- 기존 유저: JWT 재발급

---

### 1-2. 역할 설정 (최초 1회)
```
POST /api/v1/auth/role
```
Request: `{ "role": "BUYER | SELLER" }`
Response 200: `{ "role": "BUYER" }`

---

### 1-3. Access Token 재발급
```
POST /api/v1/auth/reissue
```
Request: `{ "refreshToken": "string" }`
Response 200: `{ "accessToken": "string", "refreshToken": "string" }`

---

### 1-4. 로그아웃
```
POST /api/v1/auth/logout
```
Response 200 — `USER.refresh_token = null` 처리

---

## 2. 구매자 — 요청

### 2-1. 요청 생성
```
POST /api/v1/buyer/requests
```
Request:
```json
{
  "purposeTags": ["string"],
  "relationTags": ["string"],
  "moodTags": ["string"],
  "budgetTier": "TIER1 | TIER2 | TIER3 | TIER4",
  "fulfillmentType": "PICKUP | DELIVERY",
  "fulfillmentDate": "2025-06-01",
  "requestedTimeSlots": [
    { "kind": "PICKUP_30M | DELIVERY_WINDOW", "value": "14:00 | MORNING | AFTERNOON | EVENING" }
  ],
  "placeAddressText": "string",
  "placeLat": 37.123456,
  "placeLng": 127.123456
}
```
Response 201:
```json
{ "requestId": 1, "status": "OPEN", "expiresAt": "2025-06-03T14:00:00" }
```
- `expiresAt = createdAt + 48h`
- 반경 2km 내 판매자에게 `REQUEST_ARRIVED` NOTIFICATION + OUTBOX_EVENT 저장 (같은 트랜잭션)

---

### 2-2. 요청 목록 조회
```
GET /api/v1/buyer/requests?page=0&size=20
```
Response 200 (페이지네이션):
```json
{
  "requestId": 1,
  "status": "OPEN | EXPIRED | CONFIRMED",
  "budgetTier": "TIER1",
  "fulfillmentType": "PICKUP",
  "fulfillmentDate": "2025-06-01",
  "expiresAt": "2025-06-03T14:00:00",
  "draftProposalCount": 3,
  "submittedProposalCount": 2
}
```

---

### 2-3. 요청 상세 조회
```
GET /api/v1/buyer/requests/{requestId}
```
Response 200:
```json
{
  "requestId": 1,
  "status": "OPEN",
  "purposeTags": ["string"],
  "relationTags": ["string"],
  "moodTags": ["string"],
  "budgetTier": "TIER1",
  "fulfillmentType": "PICKUP",
  "fulfillmentDate": "2025-06-01",
  "requestedTimeSlots": [{ "kind": "PICKUP_30M", "value": "14:00" }],
  "placeAddressText": "string",
  "placeLat": 37.123456,
  "placeLng": 127.123456,
  "expiresAt": "2025-06-03T14:00:00",
  "draftProposalCount": 3,
  "submittedProposalCount": 2
}
```

---

## 3. 구매자 — 제안

### 3-1. 요청별 제안 목록 조회
```
GET /api/v1/buyer/requests/{requestId}/proposals
```
Response 200:
```json
[{
  "proposalId": 1,
  "shopName": "string",
  "conceptTitle": "string",
  "status": "SUBMITTED | EXPIRED",
  "expiresAt": "2025-06-02T14:00:00"
}]
```
- `SUBMITTED` / `EXPIRED` 제안만 노출
- **가격 비노출**

---

### 3-2. 제안 상세 조회
```
GET /api/v1/buyer/proposals/{proposalId}
```
Response 200:
```json
{
  "proposalId": 1,
  "requestId": 1,
  "status": "SUBMITTED",
  "shop": { "shopId": 1, "name": "string", "phone": "string", "addressText": "string" },
  "conceptTitle": "string",
  "moodColors": ["string"],
  "mainFlowers": ["string"],
  "wrappingStyle": ["string"],
  "allergyNote": "string",
  "careTips": "string",
  "description": "string",
  "imageUrls": ["string"],
  "availableSlot": { "kind": "PICKUP_30M", "value": "14:00" },
  "expiresAt": "2025-06-02T14:00:00",
  "price": 35000
}
```
- 가격(`price`)은 응답 **맨 마지막** 필드

---

### 3-3. 제안 선택 (Mock 결제 + 예약 확정)
```
POST /api/v1/buyer/proposals/{proposalId}/select
```
Request: `{ "idempotencyKey": "UUID string" }`

Response 201:
```json
{ "reservationId": 1, "status": "CONFIRMED", "paymentStatus": "SUCCEEDED", "amount": 35000 }
```
- 단일 트랜잭션: request CONFIRMED → proposal SELECTED → 나머지 NOT_SELECTED → RESERVATION 생성 → PAYMENT 생성 → RESERVATION_CONFIRMED 알림
- 에러 422: 이미 CONFIRMED된 요청, 만료된 제안, 만료된 요청

---

## 4. 구매자 — 예약

### 4-1. 예약 목록 조회
```
GET /api/v1/buyer/reservations
```
Response 200:
```json
[{
  "reservationId": 1,
  "status": "CONFIRMED",
  "shopName": "string",
  "fulfillmentType": "PICKUP",
  "fulfillmentDate": "2025-06-01",
  "fulfillmentSlot": { "kind": "PICKUP_30M", "value": "14:00" },
  "confirmedAt": "2025-05-30T10:00:00"
}]
```

---

### 4-2. 예약 상세 조회
```
GET /api/v1/buyer/reservations/{reservationId}
```
Response 200:
```json
{
  "reservationId": 1,
  "status": "CONFIRMED",
  "proposal": { "proposalId": 1, "conceptTitle": "string", "description": "string", "imageUrls": ["string"], "price": 35000 },
  "shop": { "shopId": 1, "name": "string", "phone": "string", "addressText": "string", "lat": 37.123456, "lng": 127.123456 },
  "fulfillmentType": "PICKUP",
  "fulfillmentDate": "2025-06-01",
  "fulfillmentSlot": { "kind": "PICKUP_30M", "value": "14:00" },
  "placeAddressText": "string",
  "confirmedAt": "2025-05-30T10:00:00"
}
```

---

## 5. 판매자 — 요청함

### 5-1. 요청 목록 조회
```
GET /api/v1/seller/requests?page=0&size=20
```
Response 200 (페이지네이션):
```json
{
  "requestId": 1,
  "status": "OPEN | EXPIRED | CONFIRMED",
  "purposeTags": ["string"],
  "relationTags": ["string"],
  "moodTags": ["string"],
  "budgetTier": "TIER2",
  "fulfillmentType": "PICKUP",
  "fulfillmentDate": "2025-06-01",
  "expiresAt": "2025-06-03T14:00:00",
  "myProposalStatus": "DRAFT | SUBMITTED | null"
}
```
- 판매자 가게 좌표 기준 반경 2km 요청만 노출

---

### 5-2. 요청 상세 조회
```
GET /api/v1/seller/requests/{requestId}
```
Response 200:
```json
{
  "requestId": 1,
  "status": "OPEN",
  "purposeTags": ["string"],
  "relationTags": ["string"],
  "moodTags": ["string"],
  "budgetTier": "TIER2",
  "fulfillmentType": "PICKUP",
  "fulfillmentDate": "2025-06-01",
  "requestedTimeSlots": [{ "kind": "PICKUP_30M", "value": "14:00" }],
  "placeAddressText": "string",
  "expiresAt": "2025-06-03T14:00:00",
  "myProposalId": null
}
```

---

## 6. 판매자 — 제안

### 6-1. 제안 작성 시작 (DRAFT 생성)
```
POST /api/v1/seller/requests/{requestId}/proposals
```
Response 201:
```json
{ "proposalId": 1, "status": "DRAFT", "expiresAt": "2025-06-02T14:00:00" }
```
- 클릭 시점에 DRAFT 생성 → `draftProposalCount` 증가
- 중복 제안 불가 → 422
- 요청이 OPEN 아니면 422

---

### 6-2. 제안 임시저장 (DRAFT 수정)
```
PATCH /api/v1/seller/proposals/{proposalId}
```
Request:
```json
{
  "conceptTitle": "string",
  "moodColors": ["string"],
  "mainFlowers": ["string"],
  "wrappingStyle": ["string"],
  "allergyNote": "string",
  "careTips": "string",
  "description": "string",
  "imageUrls": ["string"],
  "availableSlot": { "kind": "PICKUP_30M | DELIVERY_WINDOW", "value": "string" },
  "price": 35000
}
```
Response 200: `{ "proposalId": 1, "status": "DRAFT" }`

---

### 6-3. 제안 제출
```
POST /api/v1/seller/proposals/{proposalId}/submit
```
Response 200:
```json
{ "proposalId": 1, "status": "SUBMITTED", "submittedAt": "2025-05-30T10:00:00" }
```
- `description`, `availableSlot`, `price` 필수 검증
- 구매자에게 `PROPOSAL_ARRIVED` NOTIFICATION + OUTBOX_EVENT 저장 (같은 트랜잭션)
- 요청 만료 후 제출 불가 → 422

---

### 6-4. 내 제안 목록 조회
```
GET /api/v1/seller/proposals?page=0&size=20
```
Response 200 (페이지네이션):
```json
{
  "proposalId": 1,
  "requestId": 1,
  "status": "DRAFT | SUBMITTED | EXPIRED | SELECTED | NOT_SELECTED",
  "conceptTitle": "string",
  "price": 35000,
  "expiresAt": "2025-06-02T14:00:00"
}
```

---

## 7. 판매자 — 예약

### 7-1. 예약 목록 조회
```
GET /api/v1/seller/reservations
```
Response 200:
```json
[{
  "reservationId": 1,
  "status": "CONFIRMED",
  "fulfillmentType": "PICKUP",
  "fulfillmentDate": "2025-06-01",
  "fulfillmentSlot": { "kind": "PICKUP_30M", "value": "14:00" },
  "buyerNickName": "string",
  "confirmedAt": "2025-05-30T10:00:00"
}]
```

---

### 7-2. 예약 상세 조회
```
GET /api/v1/seller/reservations/{reservationId}
```
Response 200:
```json
{
  "reservationId": 1,
  "status": "CONFIRMED",
  "proposal": { "proposalId": 1, "conceptTitle": "string", "description": "string", "price": 35000 },
  "fulfillmentType": "PICKUP",
  "fulfillmentDate": "2025-06-01",
  "fulfillmentSlot": { "kind": "PICKUP_30M", "value": "14:00" },
  "placeAddressText": "string",
  "placeLat": 37.123456,
  "placeLng": 127.123456,
  "confirmedAt": "2025-05-30T10:00:00"
}
```

---

## 8. 판매자 — 홈 대시보드

```
GET /api/v1/seller/home
```
Response 200:
```json
{
  "openRequestCount": 5,
  "draftProposalCount": 1,
  "submittedProposalCount": 3,
  "confirmedReservationCount": 2
}
```

---

## 9. 꽃집

### 9-1. 꽃집 등록 (최초 1회)
```
POST /api/v1/seller/shop
```
Request:
```json
{ "name": "string", "description": "string", "phone": "string", "addressText": "string", "lat": 37.123456, "lng": 127.123456 }
```
Response 201: `{ "shopId": 1, "name": "string" }`

---

### 9-2. 꽃집 정보 조회
```
GET /api/v1/seller/shop
```

---

### 9-3. 꽃집 정보 수정
```
PATCH /api/v1/seller/shop
```
Request: 9-1과 동일 구조 (변경할 필드만 포함)

---

## 10. S3 이미지 업로드

### 10-1. Presigned URL 발급
```
POST /api/v1/images/presigned-url
```
Request: `{ "fileName": "image.jpg", "contentType": "image/jpeg", "target": "PROPOSAL" }`

Response 200:
```json
{
  "presignedUrl": "https://s3.amazonaws.com/...",
  "imageUrl": "https://s3.amazonaws.com/..."
}
```
플로우: presignedUrl로 클라이언트가 S3 PUT → imageUrl을 `PATCH /proposals/{id}` 의 `imageUrls`에 포함

---

## 11. 알림

### 11-1. 알림 목록 조회
```
GET /api/v1/notifications?page=0&size=20
```
Response 200 (페이지네이션):
```json
{
  "notificationId": 1,
  "type": "REQUEST_ARRIVED | PROPOSAL_ARRIVED | RESERVATION_CONFIRMED",
  "referenceType": "REQUEST | PROPOSAL | RESERVATION",
  "referenceId": 1,
  "title": "string",
  "isRead": false,
  "createdAt": "2025-05-30T10:00:00"
}
```

---

### 11-2. 알림 읽음 처리
```
PATCH /api/v1/notifications/{notificationId}/read
```
Response 200: `{ "notificationId": 1, "isRead": true }`

---

## 12. FCM 디바이스 토큰

### 12-1. FCM 토큰 등록/갱신
```
POST /api/v1/devices
```
Request: `{ "fcmToken": "string", "platform": "IOS | ANDROID" }`

Response 200: `{ "deviceId": 1 }`
- 기존 토큰 존재 시 UPDATE (`is_active = true`)
- 앱 실행 시마다 호출하여 토큰 최신 상태 유지

---

## 엔드포인트 전체 목록

| Method | Path | 설명 | 역할 |
|---|---|---|---|
| POST | `/auth/kakao` | 카카오 로그인/회원가입 | 공통 |
| POST | `/auth/role` | 역할 설정 | 공통 |
| POST | `/auth/reissue` | 토큰 재발급 | 공통 |
| POST | `/auth/logout` | 로그아웃 | 공통 |
| POST | `/buyer/requests` | 요청 생성 | 구매자 |
| GET | `/buyer/requests` | 요청 목록 | 구매자 |
| GET | `/buyer/requests/{id}` | 요청 상세 | 구매자 |
| GET | `/buyer/requests/{id}/proposals` | 요청별 제안 목록 | 구매자 |
| GET | `/buyer/proposals/{id}` | 제안 상세 | 구매자 |
| POST | `/buyer/proposals/{id}/select` | 제안 선택 + Mock 결제 | 구매자 |
| GET | `/buyer/reservations` | 예약 목록 | 구매자 |
| GET | `/buyer/reservations/{id}` | 예약 상세 | 구매자 |
| GET | `/seller/home` | 홈 대시보드 | 판매자 |
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
