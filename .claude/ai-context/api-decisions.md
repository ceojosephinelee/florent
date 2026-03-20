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

## [AD-012] SaveNotificationPort 삭제 → SaveNotificationUseCase로 통합

- **결정일**: 2026-03-16
- **결정 내용**: `SaveNotificationPort`(outbound) 인터페이스 삭제. 기존 3개 서비스가 `SaveNotificationUseCase`(inbound)를 직접 주입받도록 변경. `NotificationService`는 `SaveNotificationUseCase`만 implements.
- **이유**: 코드 리뷰에서 outbound/inbound 이중 구현의 의미적 혼동 지적. SaveNotificationPort를 제거하고 단일 인터페이스로 통합. 기존 3개 서비스의 import + 필드 타입만 변경 (메서드 시그니처 동일).
- **영향 파일**: `NotificationService.java`, `BuyerRequestService.java`, `BuyerReservationService.java`, `SellerProposalService.java`, `FakeSaveNotificationPort.java`, `TestNotificationConfig.java`

---

## [AD-013] Notification 조회 API — userId 기반 (role 무관)

- **결정일**: 2026-03-16
- **결정 내용**: `GET /api/v1/notifications`는 buyer/seller 구분 없이 `userId`로 조회. 경로에 `/buyer/` 또는 `/seller/` prefix 없음.
- **이유**: api-spec.md §14에서 공통 경로로 정의. 알림은 역할과 무관하게 사용자 단위로 관리. `UserPrincipal.getUserId()`로 인증된 사용자 식별.
- **영향 파일**: `NotificationController.java`, `DeviceController.java`

---

## [AD-014] NotificationPageResult에 NotificationItem 내부 record 도입

- **결정일**: 2026-03-16
- **결정 내용**: `NotificationPageResult` 내부에 `NotificationItem` record 추가. Adapter/in의 `NotificationResponse`가 Domain `Notification` 대신 `NotificationItem`만 참조.
- **이유**: Adapter 레이어에서 Domain 클래스 직접 import를 제거하여 레이어 의존성 순수화. Domain → DTO 변환이 Domain 내부에서 완결됨.
- **영향 파일**: `NotificationPageResult.java`, `NotificationResponse.java`, `NotificationListResponse.java`

---

## [AD-015] DeviceService 분리 (NotificationService에서 추출)

- **결정일**: 2026-03-16
- **결정 내용**: `RegisterDeviceUseCase` 구현을 `NotificationService`에서 `DeviceService`로 분리.
- **이유**: 디바이스 등록은 알림 저장/조회와 별개 Bounded Context. SRP 원칙 적용. `DeviceController` → `DeviceService` → `UserDeviceRepository` 의존 체인이 명확해짐.
- **영향 파일**: `DeviceService.java` (신규), `NotificationService.java`, `DeviceServiceTest.java` (신규), `NotificationServiceTest.java`

---

## [AD-016] Testcontainers 1.21.0 + api.version=1.44 (Docker 29.x 호환)

- **결정일**: 2026-03-16
- **결정 내용**: Testcontainers 1.19.8 → 1.21.0 업그레이드. `api.version=1.44` 시스템 프로퍼티 추가.
- **이유**: Docker Engine 29.x (2026-02 릴리스)가 최소 API 버전을 1.44로 상향. Testcontainers 1.x는 기본 API 1.32를 사용하여 연결 실패. 2.x는 패키지 구조 전면 변경이므로 1.x + workaround 채택.
- **트레이드오프**: Docker 28.x 이하 환경에서도 api.version=1.44가 동작하는지 미검증. Docker 28.x는 1.24~1.46 범위를 지원하므로 1.44는 호환됨.
- **영향 파일**: `build.gradle.kts`

---

## [AD-017] UserRole enum 도입 — String role 대체

- **결정일**: 2026-03-16
- **결정 내용**: `SetRoleCommand.role`을 `String` 대신 `UserRole` enum으로 변경. `SetRoleRequest.role`도 `@NotNull UserRole` 타입.
- **이유**: 잘못된 role 문자열("ADMIN" 등) 입력 시 Jackson 역직렬화 단계에서 자동 400 반환. 도메인 계층에서 별도 문자열 검증 불필요. User.assignRole()에서 null 검증만 하면 됨.
- **트레이드오프**: 향후 새 역할(ADMIN 등) 추가 시 enum 변경 필요하나, 새 역할은 별도 설계가 필요하므로 enum 변경은 적절한 강제.
- **영향 파일**: `UserRole.java`, `SetRoleCommand.java`, `SetRoleRequest.java`, `User.java`, `AuthService.java`

---

## [AD-018] SecurityConfig /auth/seller-info에 hasRole("SELLER") 적용

- **결정일**: 2026-03-16
- **결정 내용**: `/api/v1/auth/seller-info`에 `.hasRole("SELLER")` 적용. 나머지 auth 경로(`/role`, `/logout`)는 `authenticated()`.
- **이유**: 판매자 정보 등록은 SELLER 역할이 확정된 사용자만 가능. BUYER나 역할 미설정 사용자가 호출하면 403 Forbidden.
- **영향 파일**: `SecurityConfig.java`, `TestSecurityConfig.java`

---

## [AD-019] KakaoOAuthPort 프로파일 전략 — 3계층 분리

- **결정일**: 2026-03-16
- **결정 내용**: KakaoOAuthAdapter(`@Profile("!local & !test")`), MockKakaoOAuthAdapter(`@Profile("local")`), TestKakaoOAuthConfig(test context 전용).
- **이유**: prod는 실제 카카오 API 호출, local은 Mock 데이터 반환, test는 람다 기반 빈으로 각 환경 독립. `@WebMvcTest`에서는 `@MockBean`으로 오버라이드.
- **영향 파일**: `KakaoOAuthAdapter.java`, `MockKakaoOAuthAdapter.java`, `TestKakaoOAuthConfig.java`

---

## [AD-020] seller-shop API를 auth/seller-info와 별도 구현

- **결정일**: 2026-03-16
- **결정 내용**: `POST /seller/shop`, `GET /seller/shop`, `PATCH /seller/shop`을 `SellerShopController` + `SellerShopService`로 독립 구현. 기존 `POST /auth/seller-info`는 온보딩 전용으로 유지.
- **이유**: auth/seller-info는 최소 필드(name, address, lat, lng)만 받는 온보딩 플로우. seller/shop은 description, phone 포함한 전체 프로필 관리. 두 API 모두 `FlowerShopRepository.findBySellerId()` 중복 체크를 수행하므로 동일 seller가 두 경로 모두 호출 시 후순위 호출이 거부됨.
- **영향 파일**: `SellerShopController.java`, `SellerShopService.java`, `RegisterShopUseCase.java`, `GetShopUseCase.java`, `UpdateShopUseCase.java`

---

## [AD-021] FlowerShop 도메인에 description 필드 추가

- **결정일**: 2026-03-16
- **결정 내용**: `FlowerShop` 도메인 모델에 `shopDescription` 필드 추가. `create()`, `reconstitute()` 시그니처 변경. 기존 호출부 16개 파일 일괄 수정.
- **이유**: api-spec.md §12-1에서 description 필드를 요구. `FlowerShopJpaEntity`에는 이미 description 컬럼이 존재했으나 도메인 모델에서 누락되어 있었음. `toDomain()`에서 description을 null로 버리고 있었음.
- **영향 파일**: `FlowerShop.java`, `FlowerShopJpaEntity.java`, 14개 테스트 파일

---

## [AD-022] 판매자 요청 목록 — 전체 요청 로드 후 서버 반경 필터링 + 수동 페이지네이션

- **결정일**: 2026-03-16
- **결정 내용**: `SellerRequestService.getSellerRequests()`에서 `findAll()` → Haversine 필터 → Java 수동 페이지네이션 방식 채택.
- **이유**: MVP에서 요청 수가 소수이므로 전체 로드 허용. DB 레벨 Bounding Box + Spring Data Page 방식은 PostGIS 없이 구현 복잡도가 높음. `BuyerRequestService.notifyNearbyShops()`와 동일 패턴.
- **트레이드오프**: 요청 수 증가 시 성능 저하. DEBT-030 기록.
- **영향 파일**: `SellerRequestService.java`, `CurationRequestRepository.java`(findAll 추가)

---

## [AD-023] 판매자 요청 상세 — 반경 밖 접근 시 404 반환 (403 아님)

- **결정일**: 2026-03-16
- **결정 내용**: 판매자가 반경 2km 밖 요청 상세를 조회하면 `REQUEST_NOT_FOUND(404)` 반환.
- **이유**: 보안 관점에서 리소스 존재 여부를 노출하지 않는 설계. 403을 반환하면 "요청이 존재하지만 접근 권한이 없다"는 정보가 누출됨.
- **영향 파일**: `SellerRequestService.java`

---

## [AD-024] ProposalRepository에 findByRequestIdsAndFlowerShopId 배치 조회 추가

- **결정일**: 2026-03-16
- **결정 내용**: 판매자 요청 목록에서 `myProposalStatus` 필드를 채우기 위해 `findByRequestIdsAndFlowerShopId(List<Long>, Long)` 배치 조회 메서드 추가.
- **이유**: 루프 내에서 개별 조회하면 N+1 발생. 페이지 내 요청 ID 목록으로 한 번에 조회하여 Map으로 변환 후 O(1) 매핑.
- **영향 파일**: `ProposalRepository.java`, `ProposalRepositoryImpl.java`, `ProposalJpaRepository.java`, `FakeProposalRepository.java`

---

## [AD-025] 판매자 프로필/홈/통계를 SellerProfileController에 통합

- **결정일**: 2026-03-16
- **결정 내용**: `/seller/me`, `/seller/home`, `/seller/stats` 3개 GET 엔드포인트를 단일 `SellerProfileController`에서 처리. 각각 별도 UseCase + Service로 분리.
- **이유**: 3개 모두 판매자 자신에 관한 조회 전용 API이며 `/seller` prefix 공유. 하지만 Service는 관심사가 다르므로(프로필 조회, 대시보드 집계, 통계 집계) 별도 클래스로 분리하여 SRP 준수.
- **영향 파일**: `SellerProfileController.java`, `SellerProfileService.java`, `SellerHomeService.java`, `SellerStatsService.java`

---

## [AD-026] 구매자 프로필 조회 시 User + Buyer 조합 조회

- **결정일**: 2026-03-16
- **결정 내용**: `GET /buyer/me` 응답에 email, createdAt은 User 테이블에서, nickName은 Buyer 테이블에서 각각 조회.
- **이유**: api-spec.md §5-1에서 email, createdAt, nickName 모두 요구. 카카오 로그인 시 email은 User에, nickName은 Buyer에 저장되는 기존 설계 유지.
- **영향 파일**: `BuyerProfileService.java`, `BuyerProfileResult.java`

---

## [AD-027] S3 Presigned URL — MockStorageAdapter (MVP)

- **결정일**: 2026-03-17
- **결정 내용**: `StoragePort` outbound port 인터페이스 + `MockStorageAdapter` 구현. 실 S3 연동 없이 Mock URL 반환.
- **이유**: MVP에서 AWS SDK 의존성 없이 API 계약만 확정. `StoragePort` 인터페이스로 추상화되어 있으므로 실 S3 어댑터 추가 시 Service 코드 변경 불필요. MockPaymentAdapter(AD-010)와 동일 전략.
- **트레이드오프**: 실 S3 presigned URL은 만료 시간이 있으나 Mock은 없음. 프론트엔드 연동 시 S3 PUT 업로드는 불가.
- **영향 파일**: `StoragePort.java`, `MockStorageAdapter.java`, `ImageService.java`, `ImageController.java`

---

## [AD-028] /images/presigned-url 접근 제어 — authenticated (role 무관)

- **결정일**: 2026-03-17
- **결정 내용**: `POST /api/v1/images/presigned-url`은 BUYER/SELLER 구분 없이 인증된 모든 사용자 접근 가능.
- **이유**: api-spec.md §13에서 "공통" 역할로 정의. SecurityConfig의 `.anyRequest().authenticated()` 규칙에 자동 포함됨. 별도 role 제한 불필요.
- **영향 파일**: `ImageController.java` (SecurityConfig 변경 없음)

---

## [AD-029] Flutter Mock → Real API 전환 — Repository 패턴

- **결정일**: 2026-03-17
- **결정 내용**: Flutter 앱의 Mock 데이터를 실제 API 호출로 전면 교체. `ApiBuyerRepository`, `ApiSellerRepository`, `ApiNotificationRepository`, `ApiAuthRepository` 4개 클래스 신규 생성. Provider에서 Mock → Real 주입 교체.
- **이유**: MVP 프론트엔드가 Mock 데이터로 UI만 구현된 상태. 백엔드 API 완성에 따라 실제 연동 필요.
- **트레이드오프**: Mock 파일은 삭제하지 않고 유지 (테스트/오프라인 개발 시 재활용 가능). Provider에서 주입만 교체.
- **영향 파일**: `lib/core/data/api/` 4개 파일 신규, `lib/core/auth/` 2개 파일 신규, Provider 4개 파일 수정, Screen 8개 파일 수정

---

## [AD-030] Dio interceptor 토큰 갱신 — 순환 의존 회피

- **결정일**: 2026-03-17
- **결정 내용**: `dioProvider` 내부 401 interceptor에서 토큰 갱신 시 `authRepositoryProvider`를 참조하지 않고 plain Dio로 직접 `/auth/reissue` 호출.
- **이유**: `dioProvider` → `authRepositoryProvider` → `dioProvider` 순환 의존 발생. `ApiAuthRepository.reissue()`도 별도 Dio를 사용하지만, Provider 그래프 단계에서 이미 순환이 감지됨. interceptor에서 plain Dio로 직접 호출하여 순환 제거.
- **영향 파일**: `dio_client.dart`

---

## [AD-031] 알림 Provider 상태 타입 — AsyncValue<List<T>> 채택

- **결정일**: 2026-03-17
- **결정 내용**: `BuyerNotificationsNotifier`, `SellerNotificationsNotifier`의 상태를 `List<NotificationItem>` → `AsyncValue<List<NotificationItem>>`으로 변경.
- **이유**: 실제 API 호출은 비동기이므로 loading/error/data 3상태 표현 필요. 화면에서 `.when()` 패턴으로 로딩 스피너/에러/데이터 분기.
- **영향 파일**: `proposal_provider.dart`, `seller_providers.dart`, `buyer_notifications_tab_screen.dart`, `seller_notifications_screen.dart`

---

## [AD-032] 알림 Provider autoDispose 전환

- **결정일**: 2026-03-20
- **결정 내용**: `buyerNotificationsProvider`, `sellerNotificationsProvider`, `sellerUnreadCountProvider`를 `StateNotifierProvider` → `StateNotifierProvider.autoDispose`로 변경.
- **이유**: autoDispose가 아닌 Provider는 앱 생명주기 동안 Notifier 인스턴스가 유지되어, 최초 `_load()` 결과가 캐시됨. 화면 이탈 시 Provider 해제 → 재진입 시 Notifier 재생성으로 최신 데이터 보장.
- **영향 파일**: `proposal_provider.dart`, `seller_providers.dart`

---

> 새 결정이 발생하면 [AD-{N}] 형식으로 추가한다.
