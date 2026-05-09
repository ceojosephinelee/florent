# UX 개선 작업 영향 범위 보고서

> 작성 시점: 2026-05-08 | 브랜치: `develop` (커밋 `164b242`)
> 목적: UX 개선 10개 항목 착수 전 코드베이스 현황 파악 및 영향 범위 분석

---

## 예정된 변경 사항 (10개)

| # | 변경 | 약칭 |
|---|------|------|
| 1 | 헤더 텍스트 '꽃다발' -> '꽃' 일괄 변경 | TEXT_CHANGE |
| 2 | 판매자/구매자 정보 페이지에 뒤로가기 버튼 | BACK_BUTTON |
| 3 | 구매자 꽃 요청 페이지에 추가 요청 필드 | EXTRA_NOTE |
| 4 | 가게 정보에 전화번호 필수 필드 추가 | SHOP_PHONE |
| 5 | 제안서에 판매자 전화번호 + 전화 버튼 | SELLER_PHONE |
| 6 | 제안서 폼 라벨 변경 + 사진 가이드 텍스트 | FORM_LABELS |
| 7 | 결제 페이지 제거 -> 판매자 연락 페이지 생성 | REMOVE_PAYMENT |
| 8 | 예약 상태 확장 (판매자 확인 단계) + FCM | RESERVATION_STATUS |
| 9 | 프로덕션 빌드에서 개발 로그인 버튼 숨김 | HIDE_DEV_LOGIN |
| 10 | 앱스토어 심사용 테스트 계정 시드 데이터 | SEED_DATA |

---

## A. 도메인 모델 인벤토리

### 현황

#### 핵심 엔티티 관계도
```
User (1) --o| Buyer (1) --o{ CurationRequest --o{ Proposal
User (1) --o| Seller (1) --|| FlowerShop --o{ Proposal
CurationRequest --o| Reservation --o| Payment
Proposal --o| Reservation
User --o{ Notification --o{ OutboxEvent
User --o{ UserDevice
```

#### 도메인 클래스 (backend/src/main/java/com/florent/domain/)

| 도메인 | 클래스 | 핵심 필드 | 상태 enum |
|--------|--------|-----------|-----------|
| User | `User` | id, kakaoId, email, passwordHash, role, refreshToken | - |
| User | `Buyer` | id, userId, nickName | - |
| User | `Seller` | id, userId | - |
| FlowerShop | `FlowerShop` | id, sellerId, name, description, **phone(NULLABLE)**, addressText, lat, lng | - |
| Request | `CurationRequest` | id, buyerId, status, purposeTags, relationTags, moodTags, budgetTier, fulfillmentType, fulfillmentDate, requestedTimeSlots, placeAddress, placeLat/Lng, expiresAt | `OPEN`, `CONFIRMED`, `EXPIRED` |
| Proposal | `Proposal` | id, requestId, flowerShopId, status, conceptTitle, description, imageUrls, availableSlot, price, expiresAt | `DRAFT`, `SUBMITTED`, `EXPIRED`, `SELECTED`, `NOT_SELECTED` |
| Reservation | `Reservation` | id, requestId, proposalId, **status**, fulfillmentType, fulfillmentDate, fulfillmentSlot, placeAddress | **`CONFIRMED` (유일한 값)** |
| Payment | `Payment` | id, reservationId, provider, status, amount, currency, idempotencyKey | `SUCCEEDED`, `FAILED` |
| Notification | `Notification` | id, userId, type, referenceType, referenceId, title, body, isRead | - |

#### CurationRequest에 없는 필드 (EXTRA_NOTE 관련)
- 현재 요청 필드: purposeTags, relationTags, moodTags, budgetTier, fulfillmentType, fulfillmentDate, requestedTimeSlots, placeAddress
- **"추가 요청사항" (자유 텍스트) 필드가 없음** -> 도메인, JPA 엔티티, DB 모두 추가 필요

#### FlowerShop.phone 현황 (SHOP_PHONE 관련)
- ERD: `varchar phone "NULLABLE"`
- 도메인: `FlowerShop.phone` 필드 존재하나 **NULLABLE**
- 판매자 등록 API (`POST /auth/seller-info`): phone 필드 **없음** (shopName, shopAddress, shopLat, shopLng만)
- 꽃집 등록 API (`POST /seller/shop`): phone 필드 있으나 **선택**
- Flutter `SellerInfoScreen`: phone 입력 필드 **없음**

#### ReservationStatus 현황 (RESERVATION_STATUS 관련)
- enum: **`CONFIRMED` 단 1개**
- 확장 필요: `CONFIRMED` -> `SELLER_CONFIRMED` -> `PREPARING` -> `READY` -> `COMPLETED` (또는 유사)

### 다가올 작업과의 연결

| 변경 | 도메인 영향 |
|------|------------|
| EXTRA_NOTE | `CurationRequest`에 `additionalNote: String?` 필드 추가. DB 마이그레이션(V12) 필요 |
| SHOP_PHONE | `FlowerShop.phone` NOT NULL 변경 불가 (기존 데이터). 등록 시 필수 검증 추가 |
| SELLER_PHONE | 제안 상세 응답에 `shop.phone` 이미 포함 (api-spec 3-2). 프론트만 변경 |
| REMOVE_PAYMENT | `Payment` 도메인 존재는 유지? 아니면 제거? 결제 없이 예약 확정 시 Payment 생성 로직 변경 필요 |
| RESERVATION_STATUS | `ReservationStatus` enum 확장. 상태 전이 메서드 추가. DB 기존 `CONFIRMED` 값 호환 필요 |

### 우려사항/질문

1. **REMOVE_PAYMENT**: 결제 제거 시 `Payment` 테이블/도메인 자체를 삭제하나, 아니면 유지하되 Mock 결제 플로우만 제거하나? 향후 실결제 연동 계획이 있다면 스키마는 유지하는 게 나을 수 있음
2. **RESERVATION_STATUS**: 새 상태값 확정 필요. "판매자 확인" 단계가 예약 생성 직후인가, 아니면 구매자 선택 후 결제 대신 판매자에게 확인 요청을 보내는 플로우인가?
3. **EXTRA_NOTE**: 이 필드가 판매자에게 노출되어야 하는가? (요청 상세 API 응답에 포함?)

---

## B. 결제 플로우 분석

### 현황

#### 현재 결제 흐름
```
구매자: 제안 상세 → "이 제안 선택하기" → 결제 화면 (PaymentScreen)
  → 결제 수단 선택 (Mock UI: 신용카드/카카오페이/애플페이/계좌이체)
  → "N원 결제하기" 버튼
  → POST /buyer/proposals/{id}/select { idempotencyKey }
  → 서버: 단일 트랜잭션 6단계 실행
  → 예약 확정 완료 화면
```

#### 서버 트랜잭션 6단계 (BuyerReservationService)
1. `request.confirm()` -> status = CONFIRMED
2. `proposal.select()` -> status = SELECTED
3. 나머지 SUBMITTED proposals -> NOT_SELECTED
4. `Reservation.create()` -> status = CONFIRMED
5. `Payment.create()` -> provider=MOCK, status=SUCCEEDED
6. `NotificationService.sendReservationConfirmed()` -> NOTIFICATION + OUTBOX_EVENT

#### 관련 파일
- Controller: `BuyerReservationController` (`POST /buyer/proposals/{id}/select`)
- Service: `BuyerReservationService.selectProposal()`
- Domain: `Payment.create()`, `Reservation.create()`
- Frontend: `PaymentScreen` (`buyer/screens/payment_screen.dart`)
- Frontend Provider: `buyerRepositoryProvider.selectProposal()`

#### PaymentScreen 현재 상태
- `_PaymentMethod` 위젯으로 4가지 결제 수단 UI 표시 (실제 결제 없음)
- `🧪 테스트 모드 — 실제 결제가 이루어지지 않아요` 안내 표시
- `Uuid().v4()`로 idempotency_key 생성
- 결제 완료 후 `context.go('/buyer/reservations/$reservationId/done')`

### 다가올 작업과의 연결

| 변경 | 결제 영향 |
|------|----------|
| REMOVE_PAYMENT | **핵심 변경**. PaymentScreen 삭제, 새 "판매자 연락" 페이지 생성. 서버 트랜잭션에서 Payment 생성 단계 제거 또는 변경. `idempotencyKey` 처리 방식 재설계 |
| RESERVATION_STATUS | 결제 없이 예약 생성 시 초기 상태가 `PENDING_SELLER_CONFIRMATION`? 판매자 확인 후 `CONFIRMED`? |

### 우려사항/질문

1. **결제 제거 범위**: 서버 API에서 `POST /buyer/proposals/{id}/select` 엔드포인트 자체는 유지하되, Payment 생성 로직만 빼면 되나? 아니면 엔드포인트 이름/시맨틱도 변경? (예: `POST /buyer/proposals/{id}/confirm`)
2. **idempotency_key**: 결제 없이도 중복 예약 방지용으로 유지해야 하나?
3. **판매자 연락 페이지**: 구매자가 제안 선택 후 바로 "판매자 연락처" 화면으로 이동하는 건가? 아니면 예약 확정 후 판매자 연락처를 보여주는 건가?
4. **라우트 변경**: `/buyer/proposals/:id/pay` 제거, 새 라우트 필요. `app_router.dart` 수정

---

## C. FCM 알림 인벤토리

### 현황

#### 알림 타입 (3개)

| 타입 | 트리거 | 수신자 | title | body |
|------|--------|--------|-------|------|
| `REQUEST_ARRIVED` | 요청 생성 | 반경 2km 판매자들 | "새 요청이 도착했어요" | "새로운 큐레이션 요청이 도착했어요." |
| `PROPOSAL_ARRIVED` | 제안 제출 | 구매자 | "새 제안이 도착했어요" | "새로운 제안서가 도착했어요." |
| `RESERVATION_CONFIRMED` | 예약 확정 | 선택된 판매자 | "예약이 확정되었어요" | "축하해요! 예약이 확정되었어요." |

#### Outbox 패턴 구현
- `NOTIFICATION` + `OUTBOX_EVENT` 동일 트랜잭션에 저장
- `OutboxEventWorker` (스케줄러): PENDING 이벤트 조회 -> FCM 전송 -> SENT 업데이트
- `FcmSender` (adapter/out): 실제 FCM 전송 담당
- 실패 시: `attempt_count` 증가, `available_at` 지연 (재시도)

#### 알림 발송 코드 위치
- `NotificationService.sendRequestArrived()` - 요청 생성 시
- `NotificationService.sendProposalArrived()` - 제안 제출 시
- `NotificationService.sendReservationConfirmed()` - 예약 확정 시

### 다가올 작업과의 연결

| 변경 | FCM 영향 |
|------|----------|
| RESERVATION_STATUS | **새 알림 타입 추가 필요**: 판매자 확인 요청 알림 (구매자 -> 판매자), 판매자 확인 완료 알림 (판매자 -> 구매자), 준비 완료 알림 등 |
| REMOVE_PAYMENT | `RESERVATION_CONFIRMED` 알림 트리거 시점 변경 가능 |

### 우려사항/질문

1. **새 알림 타입**: `NotificationType` enum에 추가할 값 확정 필요. 예: `SELLER_CONFIRMATION_REQUESTED`, `SELLER_CONFIRMED`, `ORDER_READY`?
2. **NotificationMessages**: 새 타입의 title/body 문구 확정 필요
3. **알림 타입 확장 시 DB 영향**: `NOTIFICATION.type` 컬럼이 varchar이므로 마이그레이션 없이 새 값 추가 가능

---

## D. Flutter 앱 구조 분석

### 현황

#### 디렉토리 구조
```
frontend/lib/
├── main.dart
├── core/
│   ├── auth/         # 인증 (AuthProvider, 로그인/회원가입/역할선택/판매자정보 화면)
│   ├── data/         # Repository, API client, Mock data
│   ├── models/       # DTO, enum
│   ├── network/      # Dio 설정
│   ├── router/       # GoRouter (app_router.dart)
│   └── theme/        # 색상, 타이포그래피, radius
├── buyer/
│   ├── screens/      # 18+ 화면
│   ├── widgets/      # 공용 위젯
│   └── providers/    # Riverpod providers
└── seller/
    ├── screens/      # 14+ 화면
    ├── widgets/
    └── providers/
```

#### '꽃다발' 텍스트 위치 (TEXT_CHANGE 관련)

**Frontend .dart 파일 (코드 내 하드코딩)**:

| 파일 | 위치 | 텍스트 |
|------|------|--------|
| `buyer/widgets/home/greeting_section.dart:46` | CTA 버튼 | `'꽃다발 요청하기'` |
| `buyer/widgets/home/active_request_section.dart:46` | 빈 상태 | `'아직 진행 중인 요청이 없어요.\n꽃다발 요청을 시작해 보세요!'` |
| `buyer/widgets/home/active_request_card.dart:25` | 폴백 | `'꽃다발 요청'` |
| `buyer/screens/request_step1_screen.dart:53` | AppNavBar | `'꽃다발 요청하기'` |
| `buyer/screens/request_step2_screen.dart:20,34,57` | 예산 라벨 + NavBar | `'작은 꽃다발'`, `'풍성한 꽃다발'`, `'꽃다발 요청하기'` |
| `buyer/screens/request_step3_pickup_screen.dart:103` | NavBar | `'꽃다발 요청하기'` |
| `buyer/screens/request_step3_delivery_screen.dart:29` | NavBar | `'꽃다발 요청하기'` |
| `buyer/screens/request_step4_pickup_screen.dart:46` | NavBar | `'꽃다발 요청하기'` |
| `buyer/screens/request_step4_delivery_screen.dart:56` | NavBar | `'꽃다발 요청하기'` |
| `buyer/screens/payment_screen.dart:107` | 가격 행 | `'꽃다발 가격'` |
| `buyer/screens/buyer_reservation_detail_screen.dart:57,59` | 섹션 | `'꽃다발 정보'`, `'꽃다발 이름'` |
| `buyer/screens/buyer_requests_tab_screen.dart:71,154,156,225,226` | 목록 | `'꽃다발 요청'`, `'작은 꽃다발'`, `'풍성한 꽃다발'`, 빈 상태 텍스트 |
| `buyer/screens/reservation_done_screen.dart:39` | 완료 | `'소중한 꽃다발을\n정성껏 준비할 거예요'` |
| `seller/screens/seller_proposal_step1_screen.dart:295,298` | 라벨+힌트 | `'꽃다발 이름'`, `'꽃다발'` |
| `seller/screens/seller_reservation_detail_screen.dart:57,59` | 섹션 | `'꽃다발 정보'`, `'꽃다발 이름'` |
| `seller/screens/seller_reservation_done_screen.dart:31` | 완료 | `'소중한 꽃다발을 준비해주세요'` |
| `seller/screens/seller_requests_tab_screen.dart:121,123` | 예산 | `'작은 꽃다발'`, `'풍성한 꽃다발'` |
| `seller/screens/seller_home_screen.dart:92` | 예산 | `'작은 꽃다발'`, `'풍성한 꽃다발'` |
| `core/auth/screens/role_selection_screen.dart:95` | 설명 | `'원하는 꽃다발을 요청하고'` |
| `core/data/mock/*.dart` (다수) | Mock 데이터 | 다수 |

**총 약 30+ 곳** (mock 데이터 제외해도 20+ 곳). 단순 텍스트 치환이지만 문맥에 따라 '꽃' vs '꽃 주문' vs '꽃 요청' 등으로 달라질 수 있음.

**Backend**: `biz-rules.md`에 `기본 꽃다발` (TIER2 설명). 테스트 코드에 다수. **프로덕션 코드에는 '꽃다발' 하드코딩 없음**.

#### 뒤로가기 버튼 현황 (BACK_BUTTON 관련)

| 화면 | 뒤로가기 | 비고 |
|------|---------|------|
| `SellerInfoScreen` | **없음** | AppBar 없음. body만 있음 |
| `RoleSelectionScreen` | **없음** | AppBar 없음 |
| `EmailLoginScreen` | 있음 | `Icons.arrow_back` -> `/login` |
| `EmailSignupScreen` | 있음 | `Icons.arrow_back` -> `/auth/email-login` |

#### 추가 요청 필드 현황 (EXTRA_NOTE 관련)
- `RequestStep1Screen`: 목적/관계/분위기 태그만. **자유 텍스트 필드 없음**
- `RequestFormProvider`: `additionalNote` 없음
- 어느 Step에 넣을지 결정 필요 (Step1에 추가? 별도 Step5?)

#### DEV_MODE 현황 (HIDE_DEV_LOGIN 관련)
- `login_screen.dart:162`: `const bool.fromEnvironment('DEV_MODE', defaultValue: false)`
- **이미 구현됨**: `DEV_MODE=true`일 때만 개발 로그인 버튼 노출
- `Makefile:15`: `--dart-define=DEV_MODE=true` 포함
- **프로덕션 빌드** (`flutter build ipa`): DEV_MODE 미지정 -> false -> 버튼 숨김
- **심사용 빌드** (`flutter build ipa --dart-define=DEV_MODE=true`): 버튼 노출

### 다가올 작업과의 연결

| 변경 | Flutter 영향 |
|------|-------------|
| TEXT_CHANGE | 20+ .dart 파일 텍스트 수정. 단순하나 양이 많음. Mock 데이터도 수정 필요 |
| BACK_BUTTON | `SellerInfoScreen`, `RoleSelectionScreen`에 AppBar + 뒤로가기 추가 |
| EXTRA_NOTE | 요청 생성 플로우에 필드 추가. `RequestFormProvider` 수정, API DTO 수정 |
| FORM_LABELS | `seller_proposal_step1_screen.dart` 라벨/힌트 텍스트 수정 |
| REMOVE_PAYMENT | `payment_screen.dart` 제거, 새 `seller_contact_screen.dart` 생성. `app_router.dart` 수정 |
| HIDE_DEV_LOGIN | **이미 구현됨**. 추가 작업 불필요 (빌드 시 `--dart-define` 제어) |

### 우려사항/질문

1. **TEXT_CHANGE 범위**: '꽃다발' -> '꽃'으로 단순 치환? 아니면 문맥에 따라 다른 단어? (예: '꽃다발 요청하기' -> '꽃 요청하기'? '꽃 주문하기'?)
2. **TEXT_CHANGE - 예산 라벨**: `'작은 꽃다발'` -> `'작은 꽃'`? 이건 어색할 수 있음. 완전히 새로운 라벨이 필요할 수도
3. **BACK_BUTTON**: `RoleSelectionScreen`에서 뒤로가면 어디로? `/login`으로 돌아가고 토큰은 유지? 아니면 로그아웃?
4. **EXTRA_NOTE 위치**: Step1 (목적/관계/분위기)에 추가? Step2 (예산) 이후? 별도 Step?

---

## E. 데이터베이스 마이그레이션 분석

### 현황

| 버전 | 파일명 | 내용 |
|------|--------|------|
| V1 | `V1__init_schema.sql` | USER, BUYER, SELLER, FLOWER_SHOP, CURATION_REQUEST 생성 |
| V2 | `V2__add_indexes.sql` | 인덱스 추가 |
| V3 | `V3__create_proposal.sql` | PROPOSAL 테이블 생성 |
| V4 | `V4__add_proposal_unique_and_request_index.sql` | (request_id, flower_shop_id) UNIQUE |
| V5 | `V5__alter_proposal_draft_nullable.sql` | DRAFT 상태 필드 NULLABLE 변경 |
| V6 | `V6__add_proposal_indexes.sql` | 제안 인덱스 |
| V7 | `V7__create_reservation_payment.sql` | RESERVATION, PAYMENT 테이블 생성 |
| V8 | `V8__create_notification_outbox_device.sql` | NOTIFICATION, OUTBOX_EVENT, USER_DEVICE |
| V9 | `V9__alter_user_role_nullable.sql` | USER.role NULLABLE 변경 |
| V10 | `V10__add_nickname_to_user.sql` | USER.nickname 추가 |
| V11 | `V11__alter_user_for_email_auth.sql` | kakao_id NOT NULL 해제, email UNIQUE INDEX |

**다음 마이그레이션: V12**

### 다가올 작업과의 연결

| 변경 | 마이그레이션 필요 |
|------|-----------------|
| EXTRA_NOTE | V12: `ALTER TABLE curation_request ADD COLUMN additional_note TEXT;` (NULLABLE) |
| SHOP_PHONE | 기존 phone 컬럼 NULLABLE이므로 DDL 변경 불필요. 앱/API 레벨에서 필수 검증 |
| RESERVATION_STATUS | 기존 `CONFIRMED` varchar 값 호환. 새 값 추가는 varchar이므로 DDL 불필요 |
| REMOVE_PAYMENT | Payment 테이블 존재 유지 (향후 실결제 대비). 사용하지 않을 뿐 |
| SEED_DATA | V12 또는 별도 seed 스크립트: 테스트 계정 INSERT |

### 우려사항/질문

1. **시드 데이터 방식**: Flyway 마이그레이션으로 넣을 것인가, 별도 `data.sql` / 프로파일별 `afterMigrate.sql`로 넣을 것인가?
2. **RESERVATION_STATUS**: varchar 컬럼이므로 enum 확장에 DDL 불필요하지만, **기존 데이터(`CONFIRMED`)를 새 초기 상태로 매핑할지 여부** 결정 필요

---

## F. 환경/빌드 설정 분석

### 현황

#### 백엔드 프로파일
| 프로파일 | DB | FCM | 결제 |
|---------|----|----|------|
| `local` | Docker Compose PostgreSQL | Mock (로그 출력) | Mock |
| `prod` | AWS RDS | 실 FCM | Mock |

#### 프론트엔드 빌드
| 빌드 | dart-define | DEV_MODE | 결과 |
|------|------------|----------|------|
| 개발 | `DEV_MODE=true`, `KAKAO_NATIVE_KEY=...` | true | 개발 로그인 버튼 표시 |
| 심사용 | `DEV_MODE=true` | true | 개발 로그인 버튼 표시 |
| 프로덕션 | (없음) | false | 개발 로그인 버튼 숨김 |

#### DevAuthController (백엔드)
- `POST /api/v1/auth/dev-login` 엔드포인트 존재
- SecurityConfig에서 `permitAll` 설정됨
- **프로덕션에서도 접근 가능** (프로파일 분리 없음)
- `DevAuthService`: 테스트용 User 생성 -> JWT 발급

### 다가올 작업과의 연결

| 변경 | 환경/빌드 영향 |
|------|--------------|
| HIDE_DEV_LOGIN | **프론트엔드: 이미 완료**. 백엔드: `dev-login` API가 프로덕션에서도 열려 있음 -> 보안 위험 |
| SEED_DATA | 시드 데이터를 어떤 프로파일에서 로드할지 결정 필요 |

### 우려사항/질문

1. **보안**: `POST /api/v1/auth/dev-login`이 프로덕션에서도 열려 있음. `@Profile("local")` 또는 조건부 로딩 필요
2. **심사용 빌드와 프로덕션 빌드의 백엔드 차이**: 심사 때 사용하는 백엔드가 프로덕션과 동일하다면, dev-login API가 프로덕션에서 열려있어야 심사 가능. 별도 환경 또는 조건부 활성화 검토

---

## G. 시드 데이터 / 테스트 계정

### 현황

- **시드 데이터 없음**: `data.sql`, `import.sql`, 별도 시드 스크립트 없음
- **Flyway 마이그레이션에 INSERT 없음**: DDL만 포함
- **테스트 계정**: `DevAuthService.devLogin()`으로 동적 생성 (매번 새 User)
- **Mock 데이터**: `frontend/lib/core/data/mock/` 에 하드코딩된 Mock 데이터 존재 (API 미연동 시 사용)

#### DevAuthService 동작
```
devLogin("BUYER") ->
  1. User 생성 (kakaoId="DEV_BUYER_{timestamp}", role=BUYER)
  2. Buyer 생성 (nickName="테스트 구매자")
  3. JWT 발급
  4. KakaoLoginResult 반환

devLogin("SELLER") ->
  1. User 생성 (kakaoId="DEV_SELLER_{timestamp}", role=SELLER)
  2. Seller 생성
  3. FlowerShop 생성 (name="테스트 꽃집", 좌표=강남역)
  4. JWT 발급
  5. KakaoLoginResult 반환 (hasFlowerShop=true)
```

### 다가올 작업과의 연결

| 변경 | 시드 데이터 영향 |
|------|----------------|
| SEED_DATA | 앱스토어 심사용 **고정 계정** 필요. 현재 DevAuth는 매번 새 계정 생성 -> 심사관에게 고정 ID/PW 전달 불가 |
| SHOP_PHONE | 시드 데이터 생성 시 phone 필드 포함 필요 |

### 우려사항/질문

1. **심사 계정 방식**: 이메일 로그인으로 고정 계정 (예: `test-buyer@florent.co.kr` / `test-seller@florent.co.kr`) 생성? 아니면 카카오 테스트 계정?
2. **시드 데이터 범위**: 계정만? 아니면 요청/제안/예약 샘플 데이터도? (심사관이 전체 플로우를 볼 수 있게)
3. **시드 데이터 환경 분리**: 프로덕션 DB에 시드가 들어가면 안 됨. `local` + `staging` 프로파일에서만 로딩?
4. **이메일 로그인 활용**: V11에서 이메일 인증 추가됨 -> 이메일로 고정 테스트 계정 생성 가능

---

## 영향 범위 요약 매트릭스

| 변경 | 백엔드 도메인 | 백엔드 API | DB 마이그레이션 | Flutter 화면 | Flutter Provider | 라우터 | FCM | 난이도 |
|------|-------------|-----------|----------------|-------------|-----------------|--------|-----|--------|
| 1. TEXT_CHANGE | - | - | - | 20+ 파일 | - | - | - | 낮음 (양 많음) |
| 2. BACK_BUTTON | - | - | - | 2 파일 | - | - | - | 낮음 |
| 3. EXTRA_NOTE | 1 도메인 | 3 API | V12 | 1-2 화면 | 1 provider | - | - | 중간 |
| 4. SHOP_PHONE | - | 1-2 API | - | 1 화면 | 1 provider | - | - | 낮음 |
| 5. SELLER_PHONE | - | - | - | 1-2 화면 | - | - | - | 낮음 |
| 6. FORM_LABELS | - | - | - | 1 화면 | - | - | - | 낮음 |
| 7. REMOVE_PAYMENT | 2+ 도메인 | 1-2 API | - | 2 화면 (삭제+생성) | 1-2 provider | 1 라우트 | - | **높음** |
| 8. RESERVATION_STATUS | 2+ 도메인 | 4+ API | - | 4+ 화면 | 2+ provider | - | 2+ 타입 | **높음** |
| 9. HIDE_DEV_LOGIN | 1 config | - | - | (완료) | - | - | - | 낮음 |
| 10. SEED_DATA | 1 service | - | V12 or script | - | - | - | - | 중간 |

---

## 권장 작업 순서

쉬운 것부터 처리하고, 의존성이 있는 것을 나중에:

```
Phase 1 — 단순 변경 (의존성 없음)
  9. HIDE_DEV_LOGIN (프론트 완료, 백엔드 보안 처리만)
  2. BACK_BUTTON
  6. FORM_LABELS
  1. TEXT_CHANGE

Phase 2 — 필드 추가 (작은 스키마 변경)
  4. SHOP_PHONE
  5. SELLER_PHONE (4번 이후)
  3. EXTRA_NOTE

Phase 3 — 핵심 플로우 변경 (큰 변경, 순서 중요)
  7. REMOVE_PAYMENT (결제 -> 연락 전환)
  8. RESERVATION_STATUS (7번 이후, 새 상태 플로우)

Phase 4 — 마무리
  10. SEED_DATA (모든 변경 반영 후 시드 생성)
```

---

> **어떤 항목부터 진행하시겠습니까?**
