# .claude/ai-context/api-decisions.md

> API 설계 시 고민했던 결정 이력.
> "왜 이렇게 만들었는가"를 기록한다.
> 구현 코드 설명이 아닌 설계 이유와 트레이드오프를 남긴다.

---

## [AD-001] 제안 목록에서 가격 비노출

- **결정일**: 초기 설계
- **결정 내용**: `GET /api/v1/buyer/requests/{id}/proposals` 응답에 `price` 필드 제외
- **이유**: biz-rules.md §9 — 구매자가 가격이 아닌 큐레이션 품질(concept_title, 꽃 구성)로 먼저 판단하도록 유도. 가격은 상세 진입 후 맨 마지막에만 노출.
- **영향 파일**: `ProposalSummaryResponse.java`

---

## [AD-002] 예약 확정 API 멱등성 키 — 클라이언트 생성

- **결정일**: 초기 설계
- **결정 내용**: `idempotency_key`를 클라이언트가 UUID로 생성하여 `POST /api/v1/buyer/proposals/{id}/select` 바디에 포함
- **이유**: 네트워크 재시도 시 중복 결제 방지. 서버는 PAYMENT.idempotency_key UNIQUE 제약으로 중복 차단. 클라이언트가 생성하면 재시도 시 동일 키 재사용 가능.
- **트레이드오프**: 클라이언트가 키를 생성하므로 악의적으로 다른 키를 매번 생성 가능. MVP에서는 허용.

---

## [AD-003] CONFIRMED 요청에서 제안 목록 조회 허용

- **결정일**: 초기 설계
- **결정 내용**: `request.status = CONFIRMED` 이후에도 `GET /api/v1/buyer/requests/{id}/proposals` 호출 허용 (히스토리 조회)
- **이유**: 구매자가 어떤 제안들이 있었는지 히스토리 확인 필요. EXPIRED 제안도 만료 배지와 함께 노출.

---

## [AD-004] FlowerShop.phone 도메인 필드 추가

- **결정일**: 2026-03-15
- **결정 내용**: `FlowerShop` 도메인 모델에 `shopPhone` 필드 추가. `ProposalDetailResponse.ShopInfo`에 phone 포함.
- **이유**: api-spec.md §3-2 제안 상세 응답의 `shop.phone` 필드 지원. `FlowerShopJpaEntity`에는 이미 `phone` 컬럼이 존재했으나 `toDomain()`에서 누락되어 있었음.
- **영향 파일**: `FlowerShop.java`, `FlowerShopJpaEntity.java`, `ProposalDetail.java`, `ProposalDetailResponse.java`

---

## [AD-005] select/markNotSelected ErrorCode 분리 — PROPOSAL_NOT_SELECTABLE

- **결정일**: 2026-03-15
- **결정 내용**: `Proposal.select()`, `markNotSelected()`에서 `PROPOSAL_NOT_SUBMITTABLE` 대신 `PROPOSAL_NOT_SELECTABLE`(422) 사용.
- **이유**: submit과 select은 의미적으로 다른 동작. 에러 코드를 분리하면 클라이언트가 제출 불가/선택 불가를 구분 가능.
- **영향 파일**: `Proposal.java`, `ErrorCode.java`

---

## [AD-006] JwtAuthenticationFilter @Profile("!local & !test")

- **결정일**: 2026-03-15
- **결정 내용**: JWT 필터를 `@Profile("prod")`에서 `@Profile("!local & !test")`로 변경.
- **이유**: prod, staging 등 local 이외 모든 환경에서 JWT 검증 활성화. test 프로파일에서는 `TestSecurityConfig` + `TestAuthFilter`가 대체하므로 제외. `@WebMvcTest` 컨텍스트에서는 `@MockBean JwtProvider`로 의존성 해결.
- **영향 파일**: `JwtAuthenticationFilter.java`, `BuyerRequestControllerTest.java`, `BuyerProposalControllerTest.java`

---

## [AD-007] JwtAuthenticationFilter shouldNotFilter 화이트리스트 방식

- **결정일**: 2026-03-15
- **결정 내용**: `Set.of` prefix match 방식에서 exact match + prefix match 혼합 방식으로 변경.
- **이유**: `/api/v1/auth/` prefix로 매칭하면 향후 추가되는 인증 필요 API(`/api/v1/auth/me` 등)가 의도치 않게 제외될 수 있음. `/api/v1/auth/kakao`, `/api/v1/auth/reissue`만 명시적으로 제외.
- **영향 파일**: `JwtAuthenticationFilter.java`

---

## [AD-008] 판매자 제안 상세 응답 — buyer와 별도 DTO

- **결정일**: 2026-03-15
- **결정 내용**: `SellerProposalDetailResponse`를 buyer의 `ProposalDetailResponse`와 별도로 생성. 도메인 모델 `ProposalDetail`은 공유.
- **이유**: 판매자 응답은 PATCH 필드 구성과 일치해야 하므로 ShopInfo 중첩 레코드 대신 shopName만 포함. buyer는 shop 연락처(phone, address)가 필요하지만 seller는 자기 가게이므로 간략화. createdAt 등 seller 전용 필드 확장 가능성 확보.
- **영향 파일**: `SellerProposalDetailResponse.java`, `SellerProposalController.java`

---

## [AD-009] Payment를 별도 도메인 패키지(domain/payment/)로 분리

- **결정일**: 2026-03-15
- **결정 내용**: `Payment`, `PaymentStatus`, `PaymentProvider`, `PaymentRepository`를 `domain/reservation/`에서 `domain/payment/`로 이동
- **이유**: Payment는 Reservation과 생명주기가 다르고, 향후 PG 연동 시 독립적으로 확장 가능해야 함. Bounded Context 분리 원칙 적용.
- **영향 파일**: `domain/payment/` 패키지 전체, `PaymentJpaEntity.java`, `PaymentRepositoryImpl.java`

---

## [AD-010] PaymentPort 인터페이스 도입 — Mock/실PG 교체 가능

- **결정일**: 2026-03-15
- **결정 내용**: `PaymentPort` outbound port 인터페이스 생성, `MockPaymentAdapter`가 구현. Service는 `PaymentPort.pay()`만 호출.
- **이유**: 기존에는 `Payment.createSucceeded()`를 Service에서 직접 호출하여 Mock 결제가 Service에 하드코딩. Port/Adapter 패턴으로 분리하면 토스/카카오 PG 어댑터 추가 시 Service 코드 변경 없음.
- **영향 파일**: `PaymentPort.java`, `MockPaymentAdapter.java`, `FakePaymentPort.java`, `BuyerReservationService.java`

---

## [AD-011] SelectProposal → ConfirmReservation 네이밍 변경

- **결정일**: 2026-03-15
- **결정 내용**: `SelectProposalCommand/Result/UseCase` → `ConfirmReservationCommand/Result/UseCase`로 전면 리네이밍. 메서드도 `select()` → `confirm()`으로 변경.
- **이유**: "제안 선택"은 UI 관점의 액션이지만 실제 도메인 동작은 "예약 확정 + 결제". UseCase명은 도메인 의미를 반영해야 함. API endpoint(`POST /proposals/{id}/select`)는 클라이언트 호환을 위해 유지.
- **영향 파일**: Command/Result/UseCase 3개 생성, Request/Response DTO 2개 생성, 기존 5개 삭제, 12개 참조 파일 수정

---

## [AD-012] NotificationService가 SaveNotificationPort + SaveNotificationUseCase 이중 구현

- **결정일**: 2026-03-16
- **결정 내용**: `NotificationService`가 기존 outbound port(`SaveNotificationPort`)와 새 inbound port(`SaveNotificationUseCase`)를 모두 implements
- **이유**: 기존 3개 서비스가 `SaveNotificationPort`를 주입받아 알림 저장을 호출. `SaveNotificationPort`를 제거/리네이밍하면 16개 파일 변경 필요. 대신 같은 3개 메서드를 `SaveNotificationUseCase`에도 선언하고 `NotificationService`가 둘 다 구현. 기존 코드 변경 0.
- **트레이드오프**: Port 하나가 inbound/outbound 두 역할을 하므로 의미적 혼동 가능. MVP 이후 SaveNotificationPort를 제거하고 SaveNotificationUseCase로 통합 리팩토링 권장.
- **영향 파일**: `NotificationService.java`, `SaveNotificationUseCase.java`

---

## [AD-013] Notification 조회 API — userId 기반 (role 무관)

- **결정일**: 2026-03-16
- **결정 내용**: `GET /api/v1/notifications`는 buyer/seller 구분 없이 `userId`로 조회. 경로에 `/buyer/` 또는 `/seller/` prefix 없음.
- **이유**: api-spec.md §14에서 공통 경로로 정의. 알림은 역할과 무관하게 사용자 단위로 관리. `UserPrincipal.getUserId()`로 인증된 사용자 식별.
- **영향 파일**: `NotificationController.java`, `DeviceController.java`

---

> 새 결정이 발생하면 [AD-{N}] 형식으로 추가한다.
