# biz-rules.md — Florent 비즈니스 규칙

> 이 문서의 규칙을 코드에 정확히 반영한다.
> 모호하거나 누락된 경우 임의로 판단하지 않고 반드시 질문한다.

---

## 0. 인증 규칙

### 카카오 로그인 플로우
```
카카오 앱에서 인가 코드 수신
  → POST /api/v1/auth/kakao { kakaoAccessToken }
  → 서버: 카카오 사용자 정보 조회 (kakao_id)
  → 신규 유저: USER 생성, isNewUser=true 반환
  → 기존 유저: JWT 재발급
```

### 역할(Role) 결정
- 신규 유저: 로그인 직후 `/auth/role` 화면으로 이동 → BUYER 또는 SELLER 선택
- SELLER 선택 시: 사업자 정보 입력 화면 추가 진입 (`/auth/seller-info`)
- 역할 결정 후 각 앱의 홈으로 이동

### 사업자 정보 (SELLER 전용)
| 필드 | 제약 |
|---|---|
| shopName | 필수 |
| shopAddress | 필수 |
| shopLat / shopLng | 필수 (카카오 주소 검색으로 자동 입력) |
| businessNumber | 선택 (MVP) |

### JWT 토큰 규칙
- Access Token 유효시간: 1시간
- Refresh Token 유효시간: 30일
- Access Token 만료 시: Refresh Token으로 자동 갱신 (`POST /auth/reissue`)
- Refresh Token 만료 시: 재로그인 (카카오 로그인 화면으로 이동)
- 로그아웃 시: 서버의 Refresh Token 무효화 (`USER.refresh_token = null`)

### 인증 필요 여부
- 인증 불필요: `POST /auth/kakao`, `POST /auth/reissue`
- 인증 필요: 그 외 모든 API

---

## 1. 용어

| 용어 | 정의 |
|---|---|
| 요청 (CurationRequest) | 구매자가 작성하는 큐레이션 요청서. 생성 후 48시간 유효. |
| 제안 (Proposal) | 판매자가 요청에 대해 제출하는 제안서. 생성 후 24시간 유효. |
| 예약 (Reservation) | 구매자가 제안 선택 + 결제 완료 시 생성. |
| PICKUP | 구매자가 가게로 직접 수령. |
| DELIVERY | 구매자가 지정한 주소로 수령. |

---

## 2. 예산 TIER

| enum 값 | 레이블 | 설명 |
|---|---|---|
| TIER1 | 작게 | 한 손에 쏙 |
| TIER2 | 보통 | 기본 꽃다발 |
| TIER3 | 크게 | 풍성하게 |
| TIER4 | 프리미엄 | 매우 풍성 / 고급 |

- 가격 범위를 TIER로 제한하지 않음. 판매자가 자유롭게 제시.
- **UI 고정 문구** (예산 화면 반드시 표시): `"꽃집마다 가격 정책은 다를 수 있어요."`

---

## 3. 타임슬롯 규칙

### 구매자 요청 — 복수 선택 가능

| fulfillmentType | SlotKind | 값 형식 | 범위 |
|---|---|---|---|
| PICKUP | `PICKUP_30M` | `HH:mm` | `10:00` ~ `20:00` (30분 단위) |
| DELIVERY | `DELIVERY_WINDOW` | `MORNING` / `AFTERNOON` / `EVENING` | 9~12 / 12~18 / 18~21시 |

### 판매자 제안 — 단 1개, 자유 선택

| fulfillmentType | SlotKind | 선택 수 | 선택 범위 |
|---|---|---|---|
| PICKUP | `PICKUP_30M` | **1개** | 구매자 요청 슬롯과 무관하게 자유 선택 |
| DELIVERY | `DELIVERY_WINDOW` | **1개** | 구매자 요청 슬롯과 무관하게 자유 선택 |

- 재고 차감/중복 방지 로직 없음.
- **UI 안내 문구** (판매자 제안 작성): `"구매자가 요청한 시간을 고려하여 가능한 시간을 선택해주세요."`

---

## 4. 요청 생성 필드

| 필드 | 타입 | 제약 |
|---|---|---|
| purposeTags | `List<String>` | 필수, 복수 가능 |
| relationTags | `List<String>` | 필수, 복수 가능 |
| moodTags | `List<String>` | 필수, 복수 가능 |
| budgetTier | `BudgetTier` | 필수, TIER1~TIER4 중 1개 |
| fulfillmentType | `FulfillmentType` | 필수, PICKUP / DELIVERY |
| fulfillmentDate | `LocalDate` | 필수, 수령 날짜 1개 |
| requestedTimeSlots | `List<TimeSlot>` | 필수, 복수 선택 가능 |
| placeAddressText | `String` | 필수 |
| placeLat / placeLng | `BigDecimal DECIMAL(9,6)` | 필수 |

---

## 5. 반경 2km 전송 규칙

- 기준 좌표: PICKUP → 픽업 장소 / DELIVERY → 배송 주소
- 반경 **2km 이내** FLOWER_SHOP에만 알림 전송
- 계산: Bounding Box 1차 SQL 필터 + Haversine 2차 서버 필터
- MVP: shop 수 소수 가정 → 전체 조회 후 서버 필터링 허용
- `"반경 n km 내 전송됨"` UI 표시 없음

---

## 6. 요청 만료 (48시간)

- `expiresAt = createdAt + 48h`
- 스케줄러: 1~5분 간격, `OPEN → EXPIRED`
- 만료 후 판매자: "만료" 배지, 제안 작성/제출 불가
- 만료 후 구매자: 상세 진입 가능 (히스토리), 신규 제안 수신/선택 불가

---

## 7. 제안 규칙

### 작성 시작

- 판매자 "제안서 작성하기" 클릭 → `PROPOSAL` 생성 (`status = DRAFT`)
- `PROPOSAL.created_at` = 작성 시작 시점 (`started_at` 별도 컬럼 없음)
- 작성 즉시 구매자 요청 상세의 `draftProposalCount` 증가
- **UI 경고 문구 (필수)**: `"제안서 작성을 시작한 뒤 정해진 시간 내 제출하지 못하면 패널티(신뢰도 지수 하락)가 있을 수 있어요. 신중하게 버튼을 클릭해주세요."` ← MVP: 패널티 기능 미구현, 문구만 표시

### 필수 필드

| 필드 | 제약 |
|---|---|
| `description` | NOT NULL |
| `availableSlot` (kind + value) | NOT NULL, 단 1개 |
| `price` | NOT NULL, 0 초과 |

### 가격 정책

- 예산 TIER 범위로 제한하지 않음. 판매자 자유 제시.
- **UI 안내 문구**: `"요청서의 예산을 고려하여 가격을 정해주세요."`

### 제출 조건

- `status = DRAFT`인 제안만 제출 가능
- 요청이 `OPEN` 상태일 때만 제출 가능
- 한 요청에 같은 가게가 중복 제안 불가 → DB `(request_id, flower_shop_id)` UNIQUE

---

## 8. 제안 만료 (24시간)

- `expiresAt = createdAt + 24h`
- 스케줄러: `SUBMITTED / DRAFT → EXPIRED` (`SELECTED` 제외)
- 구매자 화면: 제안 카드/상세에 만료까지 남은 시간 표시 (필수)
- EXPIRED 제안: 만료 배지와 함께 구매자에게 노출, 선택 불가

---

## 9. 구매자 제안 선택 UX

- 요청 상세: `"n명의 판매자가 제안서 작성을 시작했어요"` (DRAFT 포함 카운트)
- 제안 목록:
  - SUBMITTED: 정상 노출
  - EXPIRED: 만료 배지와 함께 노출, 선택 버튼 비활성화
  - **가격 비노출** (목록에서)
  - 제목에 `concept_title` 표시
- 제안 상세: 가격은 **맨 마지막**에 표시

---

## 10. Mock 결제

- 결제 수단 입력 UI 없음
- "결제하기" 버튼 → 확인 모달 → 즉시 성공
- `PAYMENT.provider = MOCK`, `status = SUCCEEDED`
- `idempotency_key`: 클라이언트가 UUID 생성하여 전송 (중복 결제 방지)

---

## 11. 예약 확정 — 단일 트랜잭션 6단계

```
1. request.status = CONFIRMED
2. 선택된 proposal.status = SELECTED
3. 나머지 SUBMITTED proposal.status = NOT_SELECTED
4. RESERVATION 생성 (status = CONFIRMED)
5. PAYMENT 생성 (provider = MOCK, status = SUCCEEDED)
6. 선택된 판매자에게 RESERVATION_CONFIRMED NOTIFICATION + OUTBOX_EVENT 저장
```

- 미선택 판매자 알림 없음 (앱 내 NOT_SELECTED 배지로만)
- CONFIRMED 요청에는 추가 제안 불가

---

## 12. 알림 발송

| 이벤트 | 수신자 | 타입 | 처리 시점 |
|---|---|---|---|
| 요청 생성 | 반경 2km 내 판매자 (가게별) | `REQUEST_ARRIVED` | 요청 생성 트랜잭션과 함께 |
| 제안 제출 | 구매자 | `PROPOSAL_ARRIVED` | 제안 제출 트랜잭션과 함께 |
| 예약 확정 | 선택된 판매자 | `RESERVATION_CONFIRMED` | 예약 확정 트랜잭션과 함께 |

- 모든 알림: NOTIFICATION + OUTBOX_EVENT 저장 → Worker가 FCM 전송

---

## 13. 판매자 요청 목록 노출

| 요청 상태 | 노출 방식 |
|---|---|
| `OPEN` | 정상 노출, 제안 작성 가능 |
| `EXPIRED` | "만료" 배지, 제안 불가 |
| `CONFIRMED` | "마감" 배지, 제안 불가 |

---

## 14. 만료 처리 스케줄러

- 주기: 1~5분 (`@Scheduled`)
- `OPEN` 요청 중 `expiresAt < now` → `EXPIRED`
- `SUBMITTED / DRAFT` 제안 중 `expiresAt < now` → `EXPIRED` (`SELECTED` 제외)

---

## 15. S3 이미지 업로드

- 제안서 이미지만 (꽃집 프로필 이미지 MVP 제외)
- 플로우:
  1. `POST /api/v1/images/presigned-url` 호출
  2. 클라이언트가 presignedUrl로 S3에 직접 PUT
  3. `imageUrl`을 제안 임시저장 API에 포함
- `PROPOSAL.image_urls_json`: NULLABLE

---

## 16. MVP 제외

- 실결제 PG 연동
- 채팅, 배송기사/물류 연동
- 실시간 타임슬롯 재고 관리
- 판매자 패널티 기능 (문구만 표시)
- `"반경 n km 내 전송됨"` UI
- `"이미 지난 시간 회색 표시"`
- 꽃집 프로필 이미지
- Redis
- 자체 이메일/비밀번호 로그인