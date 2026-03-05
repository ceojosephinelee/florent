# architecture.md — Florent 아키텍처 설계 (순수 헥사고날)

> AI는 이 문서에 정의된 패키지 구조와 레이어 규칙을 반드시 준수한다.
> 임의로 패키지를 추가하거나 레이어 규칙을 위반하지 않는다.
> 변경이 필요하면 반드시 먼저 질문한다.

---

## 1. 아키텍처 선택: 순수 헥사고날

### 선택 이유
- 외부 연동 5개 (카카오 OAuth, 카카오 로컬 API, FCM, S3, Mock PG → 실 PG 전환 예정)
- Domain이 JPA, FCM, HTTP 등 인프라 기술을 전혀 모르게 격리
- Inbound Port(UseCase 인터페이스) 유지 → Controller 테스트 시 Service 구현체와 완전 분리
- Outbound Port 유지 → Fake 구현체로 DB/FCM/S3 없이 Service 단위 테스트 가능
- 혼자 개발하므로 팀 러닝커브 비용 없음

### 타협 없음
- **Inbound Port 생략 없음** — Controller는 UseCase 인터페이스만 안다
- **Domain Entity에 JPA 어노테이션 없음** — JPA Entity는 adapter/out에만 존재
- 비용: 엔티티마다 JpaEntity + RepositoryImpl + Domain 클래스 3개 필요
- 대가: 완전한 도메인 격리 + 테스트 완전 격리

---

## 2. 의존성 방향

```
┌─────────────────────────────────────────────────────────┐
│                      외부 세계                            │
│   HTTP           FCM / S3 / 카카오 / PG                  │
│    ↓                      ↑                              │
│ [adapter/in]          [adapter/out]                      │
│  Controller          JpaEntity, FcmAdapter, S3Adapter    │
│    ↓ 호출                 ↑ Port 구현                    │
│ [Inbound Port]        [Outbound Port]                    │
│  UseCase 인터페이스    Repository, PaymentPort 등 인터페이스│
│         ↓                   ↑                            │
│      [application]          │                            │
│       Service               │                            │
│         ↓ 조작              │                            │
│       [domain]              │                            │
│    순수 Java 클래스          │                            │
│    비즈니스 로직만            │                            │
└─────────────────────────────────────────────────────────┘
```

**절대 금지**
- `domain` → `adapter` 의존 금지
- `domain` → `application` 의존 금지
- `application` → `adapter` 직접 의존 금지 (반드시 Port 인터페이스 경유)
- `adapter/in` → `domain` 직접 의존 금지 (반드시 UseCase 인터페이스 경유)
- Domain 클래스에 `@Entity`, `@Column` 등 JPA 어노테이션 사용 금지

---

## 3. 용어 정의

| 용어 | 정의 | Florent 예시 |
|---|---|---|
| **Inbound Port** | 외부에서 앱을 호출하는 계약 (인터페이스) | `CreateRequestUseCase` |
| **Outbound Port** | 앱이 외부를 호출하는 계약 (인터페이스) | `PaymentPort`, `CurationRequestRepository` |
| **Inbound Adapter** | Inbound Port를 호출하는 구현체 | `BuyerRequestController` |
| **Outbound Adapter** | Outbound Port를 구현하는 구현체 | `MockPaymentAdapter`, `FcmPushAdapter` |
| **Application (UseCase)** | Inbound Port의 구현체, 트랜잭션 경계 | `BuyerRequestService` |
| **Domain** | 순수 비즈니스 모델, 외부 기술 의존 없음 | `CurationRequest`, `Proposal` |

---

## 4. 전체 패키지 구조

```
src/main/java/com/florent/
│
├── FlorentApplication.java
│
├── domain/                                      # 순수 Java. JPA/HTTP 의존 없음.
│   │
│   ├── user/
│   │   ├── User.java                            # 순수 도메인 클래스
│   │   ├── UserRole.java                        # enum: BUYER, SELLER
│   │   └── UserRepository.java                  # Outbound Port (인터페이스)
│   │
│   ├── buyer/
│   │   ├── Buyer.java
│   │   └── BuyerRepository.java                 # Outbound Port
│   │
│   ├── seller/
│   │   ├── Seller.java
│   │   └── SellerRepository.java                # Outbound Port
│   │
│   ├── shop/
│   │   ├── FlowerShop.java
│   │   └── FlowerShopRepository.java            # Outbound Port
│   │
│   ├── request/
│   │   ├── CurationRequest.java                 # 상태전이 메서드 포함
│   │   ├── RequestStatus.java                   # enum: OPEN, EXPIRED, CONFIRMED
│   │   ├── BudgetTier.java                      # enum: TIER1~4
│   │   ├── FulfillmentType.java                 # enum: PICKUP, DELIVERY
│   │   ├── TimeSlot.java                        # Value Object (kind + value)
│   │   ├── CurationRequestRepository.java       # Outbound Port
│   │   └── CreateRequestUseCase.java            # Inbound Port
│   │
│   ├── proposal/
│   │   ├── Proposal.java                        # 상태전이 메서드 포함
│   │   ├── ProposalStatus.java                  # enum: DRAFT, SUBMITTED, EXPIRED, SELECTED, NOT_SELECTED
│   │   ├── SlotKind.java                        # enum: PICKUP_30M, DELIVERY_WINDOW
│   │   ├── ProposalRepository.java              # Outbound Port
│   │   ├── StartProposalUseCase.java            # Inbound Port
│   │   ├── SaveProposalUseCase.java             # Inbound Port
│   │   └── SubmitProposalUseCase.java           # Inbound Port
│   │
│   ├── reservation/
│   │   ├── Reservation.java
│   │   ├── ReservationStatus.java               # enum: CONFIRMED
│   │   ├── ReservationRepository.java           # Outbound Port
│   │   └── ConfirmReservationUseCase.java       # Inbound Port
│   │
│   ├── payment/
│   │   ├── Payment.java
│   │   ├── PaymentStatus.java                   # enum: SUCCEEDED, FAILED
│   │   ├── PaymentPort.java                     # Outbound Port ← PG 교체 핵심
│   │   └── PaymentRepository.java               # Outbound Port
│   │
│   └── notification/
│       ├── Notification.java
│       ├── NotificationType.java                # enum: REQUEST_ARRIVED, PROPOSAL_ARRIVED, RESERVATION_CONFIRMED
│       ├── NotificationRepository.java          # Outbound Port
│       ├── OutboxEvent.java
│       ├── OutboxStatus.java                    # enum: PENDING, SENT, FAILED
│       ├── OutboxEventRepository.java           # Outbound Port
│       ├── PushNotificationPort.java            # Outbound Port ← FCM 교체 핵심
│       ├── SaveNotificationUseCase.java         # Inbound Port
│       ├── UserDevice.java
│       └── UserDeviceRepository.java            # Outbound Port
│
├── application/                                 # Inbound Port 구현체. 트랜잭션 경계.
│   ├── auth/
│   │   └── AuthService.java
│   ├── buyer/
│   │   ├── BuyerRequestService.java             # CreateRequestUseCase 구현
│   │   └── BuyerProposalService.java            # StartProposalUseCase, SaveProposalUseCase, SubmitProposalUseCase 구현
│   ├── seller/
│   │   ├── SellerRequestService.java
│   │   ├── SellerProposalService.java
│   │   └── SellerShopService.java
│   ├── reservation/
│   │   └── ReservationService.java              # ConfirmReservationUseCase 구현
│   ├── notification/
│   │   └── NotificationService.java             # SaveNotificationUseCase 구현
│   └── image/
│       └── ImageService.java
│
├── adapter/
│   │
│   ├── in/                                      # Inbound Adapter. UseCase 인터페이스만 호출.
│   │   ├── auth/
│   │   │   ├── AuthController.java
│   │   │   └── dto/
│   │   │       ├── KakaoLoginRequest.java
│   │   │       ├── KakaoLoginResponse.java
│   │   │       ├── TokenReissueRequest.java
│   │   │       └── RoleSetupRequest.java
│   │   ├── buyer/
│   │   │   ├── BuyerHomeController.java
│   │   │   ├── BuyerRequestController.java
│   │   │   ├── BuyerProposalController.java
│   │   │   ├── BuyerReservationController.java
│   │   │   └── dto/
│   │   │       ├── BuyerHomeResponse.java
│   │   │       ├── CreateRequestRequest.java
│   │   │       ├── RequestSummaryResponse.java
│   │   │       ├── RequestDetailResponse.java
│   │   │       ├── ProposalSummaryResponse.java
│   │   │       ├── ProposalDetailResponse.java
│   │   │       ├── SelectProposalRequest.java
│   │   │       ├── ReservationSummaryResponse.java
│   │   │       └── ReservationDetailResponse.java
│   │   ├── seller/
│   │   │   ├── SellerHomeController.java
│   │   │   ├── SellerRequestController.java
│   │   │   ├── SellerProposalController.java
│   │   │   ├── SellerReservationController.java
│   │   │   ├── SellerShopController.java
│   │   │   └── dto/
│   │   │       ├── SellerHomeResponse.java
│   │   │       ├── SellerRequestSummaryResponse.java
│   │   │       ├── SellerRequestDetailResponse.java
│   │   │       ├── SaveProposalRequest.java
│   │   │       ├── SellerProposalSummaryResponse.java
│   │   │       ├── SellerReservationSummaryResponse.java
│   │   │       ├── SellerReservationDetailResponse.java
│   │   │       ├── RegisterShopRequest.java
│   │   │       └── UpdateShopRequest.java
│   │   ├── notification/
│   │   │   ├── NotificationController.java
│   │   │   └── dto/
│   │   │       └── NotificationResponse.java
│   │   ├── device/
│   │   │   ├── DeviceController.java
│   │   │   └── dto/
│   │   │       └── RegisterDeviceRequest.java
│   │   └── image/
│   │       ├── ImageController.java
│   │       └── dto/
│   │           ├── PresignedUrlRequest.java
│   │           └── PresignedUrlResponse.java
│   │
│   └── out/                                     # Outbound Adapter. Port 인터페이스 구현.
│       ├── persistence/                         # JPA 구현체
│       │   ├── request/
│       │   │   ├── CurationRequestJpaEntity.java        # @Entity
│       │   │   ├── CurationRequestJpaRepository.java    # Spring Data JPA
│       │   │   └── CurationRequestRepositoryImpl.java   # CurationRequestRepository Port 구현
│       │   ├── proposal/
│       │   │   ├── ProposalJpaEntity.java
│       │   │   ├── ProposalJpaRepository.java
│       │   │   └── ProposalRepositoryImpl.java
│       │   ├── reservation/
│       │   │   ├── ReservationJpaEntity.java
│       │   │   ├── ReservationJpaRepository.java
│       │   │   └── ReservationRepositoryImpl.java
│       │   ├── payment/
│       │   │   ├── PaymentJpaEntity.java
│       │   │   ├── PaymentJpaRepository.java
│       │   │   └── PaymentRepositoryImpl.java
│       │   ├── notification/
│       │   │   ├── NotificationJpaEntity.java
│       │   │   ├── OutboxEventJpaEntity.java
│       │   │   ├── NotificationJpaRepository.java
│       │   │   ├── OutboxEventJpaRepository.java
│       │   │   ├── NotificationRepositoryImpl.java
│       │   │   └── OutboxEventRepositoryImpl.java
│       │   ├── user/
│       │   │   ├── UserJpaEntity.java
│       │   │   ├── UserDeviceJpaEntity.java
│       │   │   ├── UserJpaRepository.java
│       │   │   ├── UserDeviceJpaRepository.java
│       │   │   ├── UserRepositoryImpl.java
│       │   │   └── UserDeviceRepositoryImpl.java
│       │   ├── buyer/
│       │   │   ├── BuyerJpaEntity.java
│       │   │   ├── BuyerJpaRepository.java
│       │   │   └── BuyerRepositoryImpl.java
│       │   ├── seller/
│       │   │   ├── SellerJpaEntity.java
│       │   │   ├── SellerJpaRepository.java
│       │   │   └── SellerRepositoryImpl.java
│       │   └── shop/
│       │       ├── FlowerShopJpaEntity.java
│       │       ├── FlowerShopJpaRepository.java
│       │       └── FlowerShopRepositoryImpl.java
│       ├── payment/
│       │   └── MockPaymentAdapter.java          # PaymentPort 구현
│       ├── push/
│       │   └── FcmPushAdapter.java              # PushNotificationPort 구현
│       ├── kakao/
│       │   ├── KakaoAuthPort.java               # Outbound Port
│       │   ├── KakaoAuthAdapter.java            # KakaoAuthPort 구현
│       │   ├── KakaoLocalPort.java              # Outbound Port
│       │   └── KakaoLocalAdapter.java           # KakaoLocalPort 구현
│       ├── storage/
│       │   ├── ImageStoragePort.java            # Outbound Port
│       │   └── S3ImageAdapter.java              # ImageStoragePort 구현
│       └── scheduler/
│           ├── ExpiryScheduler.java             # 요청/제안 만료 처리
│           └── OutboxWorker.java                # Outbox → FCM 전송 Worker
│
└── common/                                      # 어느 레이어도 의존 가능
    ├── exception/
    │   ├── BusinessException.java
    │   ├── ErrorCode.java
    │   └── GlobalExceptionHandler.java
    ├── response/
    │   └── ApiResponse.java
    ├── security/
    │   ├── JwtProvider.java
    │   ├── JwtAuthFilter.java
    │   ├── SecurityConfig.java
    │   └── UserPrincipal.java
    └── util/
        └── HaversineUtil.java
```

---

## 5. 레이어별 코드 규칙

### domain — 순수 Java 클래스

```java
// ✅ CurationRequest.java — JPA 어노테이션 없음
public class CurationRequest {

    private Long id;
    private Long buyerId;
    private RequestStatus status;
    private LocalDateTime expiresAt;
    // ... 나머지 필드

    // 정적 팩토리
    public static CurationRequest create(Long buyerId, ...) {
        CurationRequest request = new CurationRequest();
        request.status = RequestStatus.OPEN;
        request.expiresAt = LocalDateTime.now().plusHours(48);
        return request;
    }

    // 상태 전이 — 비즈니스 규칙 포함
    public void confirm() {
        if (this.status != RequestStatus.OPEN) {
            throw new BusinessException(ErrorCode.REQUEST_NOT_OPEN);
        }
        this.status = RequestStatus.CONFIRMED;
    }

    public void expire() {
        if (this.status == RequestStatus.OPEN) {
            this.status = RequestStatus.EXPIRED;
        }
    }

    public boolean isExpired() {
        return this.status == RequestStatus.EXPIRED
            || LocalDateTime.now().isAfter(this.expiresAt);
    }
}

// ✅ Outbound Port — domain 안에 정의
public interface CurationRequestRepository {
    CurationRequest save(CurationRequest request);
    Optional findById(Long id);
    List findOpenExpired(LocalDateTime now);
}

// ✅ Inbound Port — domain 안에 정의
public interface CreateRequestUseCase {
    CreateRequestResult create(CreateRequestCommand command);
}

// Command 객체 (DTO 아님 — 도메인 개념)
public record CreateRequestCommand(
    Long buyerId,
    List purposeTags,
    BudgetTier budgetTier,
    FulfillmentType fulfillmentType,
    LocalDate fulfillmentDate,
    List requestedTimeSlots,
    String placeAddressText,
    BigDecimal placeLat,
    BigDecimal placeLng
) {}
```

**domain 규칙**
- `@Entity`, `@Column`, `@Table` 등 JPA 어노테이션 절대 금지
- `@Transactional` 금지
- `adapter`, `application`, `org.springframework` import 금지
- 상태 전이 메서드는 반드시 Entity 안에
- 비즈니스 검증 실패 시 `BusinessException` throw

---

### application — UseCase 구현, 트랜잭션 경계

```java
// ✅ BuyerRequestService.java
@Service
@RequiredArgsConstructor
@Transactional
public class BuyerRequestService implements CreateRequestUseCase {

    private final CurationRequestRepository requestRepository;   // Outbound Port
    private final FlowerShopRepository shopRepository;           // Outbound Port
    private final SaveNotificationUseCase notificationUseCase;   // Inbound Port

    @Override
    public CreateRequestResult create(CreateRequestCommand command) {

        // 도메인 객체 생성
        CurationRequest request = CurationRequest.create(
            command.buyerId(), command.budgetTier(), ...);
        requestRepository.save(request);

        // 반경 2km 가게 조회 + 필터링 (HaversineUtil은 common)
        List nearbyShops = shopRepository.findAll().stream()
            .filter(shop -> HaversineUtil.isWithin2km(
                command.placeLat(), command.placeLng(),
                shop.getLat(), shop.getLng()))
            .toList();

        // 알림 저장 (같은 트랜잭션)
        nearbyShops.forEach(shop ->
            notificationUseCase.saveRequestArrived(shop.getSellerId(), request.getId()));

        return CreateRequestResult.from(request);
    }
}
```

**application 규칙**
- `@Transactional`은 Service 메서드에만
- 조회 전용은 `@Transactional(readOnly = true)`
- Outbound Port(인터페이스)만 의존, Adapter 구현체 직접 참조 금지
- `adapter` 패키지 import 금지

---

### adapter/in — Controller, Inbound Port 호출

```java
// ✅ BuyerRequestController.java
@RestController
@RequestMapping("/api/v1/buyer/requests")
@RequiredArgsConstructor
public class BuyerRequestController {

    private final CreateRequestUseCase createRequestUseCase;  // 인터페이스만 의존

    @PostMapping
    public ResponseEntity<ApiResponse> create(
        @RequestBody @Valid CreateRequestRequest request,
        @AuthenticationPrincipal UserPrincipal principal
    ) {
        CreateRequestCommand command = request.toCommand(principal.getUserId());
        CreateRequestResult result = createRequestUseCase.create(command);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(RequestSummaryResponse.from(result)));
    }
}
```

**adapter/in 규칙**
- Controller에 `if`, `for`, 비즈니스 판단 로직 금지
- `@Valid`로 요청 DTO 검증 필수
- `BuyerRequestService` 같은 구현체 직접 주입 금지 — 반드시 UseCase 인터페이스로
- `domain` 패키지 직접 import 금지

---

### adapter/out — Port 구현, JPA Entity 변환

```java
// ✅ CurationRequestRepositoryImpl.java
@Repository
@RequiredArgsConstructor
public class CurationRequestRepositoryImpl implements CurationRequestRepository {

    private final CurationRequestJpaRepository jpaRepository;

    @Override
    public CurationRequest save(CurationRequest domain) {
        CurationRequestJpaEntity entity = CurationRequestJpaEntity.from(domain);
        CurationRequestJpaEntity saved = jpaRepository.save(entity);
        return saved.toDomain();
    }

    @Override
    public Optional findById(Long id) {
        return jpaRepository.findById(id)
            .map(CurationRequestJpaEntity::toDomain);
    }
}

// ✅ CurationRequestJpaEntity.java — @Entity는 여기만
@Entity
@Table(name = "curation_request")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CurationRequestJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    // ... 나머지 컬럼

    // JPA Entity → Domain 변환
    public CurationRequest toDomain() {
        return CurationRequest.reconstitute(this.id, this.status, ...);
    }

    // Domain → JPA Entity 변환
    public static CurationRequestJpaEntity from(CurationRequest domain) {
        CurationRequestJpaEntity entity = new CurationRequestJpaEntity();
        entity.id = domain.getId();
        entity.status = domain.getStatus();
        return entity;
    }
}

// ✅ MockPaymentAdapter.java
@Component
public class MockPaymentAdapter implements PaymentPort {

    private final PaymentRepository paymentRepository;

    @Override
    public PaymentResult pay(Long reservationId, BigDecimal amount, String idempotencyKey) {
        Payment payment = Payment.createMock(reservationId, amount, idempotencyKey);
        paymentRepository.save(payment);
        return PaymentResult.success(payment.getId());
    }
}

// ✅ 나중에 토스 연동 시 — application 코드 한 줄도 안 바뀜
@Component
public class TossPaymentAdapter implements PaymentPort {
    @Override
    public PaymentResult pay(Long reservationId, BigDecimal amount, String idempotencyKey) {
        // 토스 API 호출
    }
}
```

---

## 6. 주요 플로우별 레이어 통과

### 요청 생성
```
POST /api/v1/buyer/requests
  → [adapter/in]   BuyerRequestController
  → [domain]       CreateRequestUseCase (Inbound Port 인터페이스)
  → [application]  BuyerRequestService.create()          @Transactional
      → CurationRequest.create()                         도메인 팩토리
      → [domain]   CurationRequestRepository.save()      Outbound Port
          → [adapter/out] CurationRequestRepositoryImpl  실제 JPA 저장
      → HaversineUtil 반경 2km 필터
      → SaveNotificationUseCase.saveRequestArrived()     같은 트랜잭션
  → ApiResponse.success()
```

### 제안 선택 + 예약 확정
```
POST /api/v1/buyer/proposals/{id}/select
  → [adapter/in]   BuyerProposalController
  → [domain]       ConfirmReservationUseCase (Inbound Port)
  → [application]  ReservationService.confirm()          @Transactional
      → proposal.select()                                도메인 메서드
      → request.confirm()                                도메인 메서드
      → 나머지 proposal.notSelect()                       도메인 메서드
      → Reservation.create() 저장
      → [domain]   PaymentPort.pay()                     Outbound Port
          → [adapter/out] MockPaymentAdapter.pay()
      → SaveNotificationUseCase.saveReservationConfirmed() 같은 트랜잭션
  → ApiResponse.success()
```

### Outbox Worker
```
OutboxWorker @Scheduled(fixedDelay = 10_000)
  → OutboxEventRepository.findPendingJobs()
  → UserDeviceRepository.findActiveByUserId()
  → [domain] PushNotificationPort.send()                 Outbound Port
      → [adapter/out] FcmPushAdapter.send()
  → OutboxEvent.markSent() or incrementAttempt() or markFailed()
```

---

## 7. 테스트 전략

### 테스트 구조
```
src/test/java/com/florent/
├── domain/
│   ├── request/
│   │   └── CurationRequestTest.java         # 순수 도메인 단위 테스트 (의존 없음)
│   └── proposal/
│       └── ProposalTest.java
├── application/
│   ├── buyer/
│   │   └── BuyerRequestServiceTest.java     # Fake Port 주입, DB/FCM 없이 테스트
│   └── reservation/
│       └── ReservationServiceTest.java
├── adapter/
│   ├── in/
│   │   └── buyer/
│   │       └── BuyerRequestControllerTest.java  # @WebMvcTest + UseCase Mock
│   └── out/
│       └── persistence/
│           └── CurationRequestRepositoryTest.java  # Testcontainers
└── fake/                                    # Fake 구현체 모음
    ├── FakeCurationRequestRepository.java   # CurationRequestRepository Fake
    ├── FakeProposalRepository.java
    ├── FakePaymentPort.java                 # PaymentPort Fake
    ├── FakePushNotificationPort.java        # PushNotificationPort Fake
    └── FakeSaveNotificationUseCase.java

src/test/resources/features/
├── request_creation.feature                 # Cucumber BDD
├── proposal_submission.feature
└── reservation_confirmation.feature
```

### 테스트 예시

```java
// ✅ 도메인 단위 테스트 — 의존 없음, 가장 빠름
class CurationRequestTest {

    @Test
    void OPEN_상태_요청을_confirm하면_CONFIRMED_상태가_된다() {
        // given
        CurationRequest request = CurationRequest.create(...);

        // when
        request.confirm();

        // then
        assertThat(request.getStatus()).isEqualTo(RequestStatus.CONFIRMED);
    }

    @Test
    void OPEN이_아닌_요청을_confirm하면_예외가_발생한다() {
        // given
        CurationRequest request = CurationRequest.create(...);
        request.expire();

        // when & then
        assertThatThrownBy(request::confirm)
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.REQUEST_NOT_OPEN);
    }
}

// ✅ Service 단위 테스트 — Fake Port 주입, DB 없음
class ReservationServiceTest {

    private ReservationService reservationService;
    private FakePaymentPort fakePaymentPort;

    @BeforeEach
    void setUp() {
        fakePaymentPort = new FakePaymentPort();
        reservationService = new ReservationService(
            new FakeCurationRequestRepository(),
            new FakeProposalRepository(),
            new FakeReservationRepository(),
            fakePaymentPort,
            new FakeSaveNotificationUseCase()
        );
    }

    @Test
    void 제안_선택_시_요청이_CONFIRMED_되고_결제가_처리된다() {
        // given
        // when
        // then
    }

    @Test
    void 이미_CONFIRMED된_요청의_제안_선택_시_예외가_발생한다() {
        // given
        // when & then
    }
}

// ✅ Controller 테스트 — UseCase 인터페이스 Mock (구현체와 무관)
@WebMvcTest(BuyerRequestController.class)
class BuyerRequestControllerTest {

    @MockBean
    private CreateRequestUseCase createRequestUseCase;  // 인터페이스 Mock

    @Test
    void 요청_생성_성공() throws Exception {
        // given
        given(createRequestUseCase.create(any())).willReturn(...);

        // when & then
        mockMvc.perform(post("/api/v1/buyer/requests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(...))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.success").value(true));
    }
}
```

**테스트 규칙**
- 모든 테스트 `// given / // when / // then` 주석 필수
- 성공 케이스 + Unhappy Path 최소 1개 이상
- Repository 테스트: Testcontainers(PostgreSQL)
- Controller 테스트: `@WebMvcTest` + UseCase 인터페이스 `@MockBean`
- Service 단위 테스트: `fake/` 패키지의 Fake 구현체 사용 (Mockito 지양)

---

## 8. 리소스 구조

```
src/main/resources/
├── application.yml
├── application-local.yml     # Docker Compose PostgreSQL, Mock FCM
├── application-prod.yml      # AWS RDS, 실 FCM
└── db/
    └── migration/
        ├── V1__init_schema.sql
        ├── V2__add_notification.sql
        └── ...
```
