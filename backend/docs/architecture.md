# architecture.md — Florent 헥사고날 아키텍처

> 이 문서에 정의된 패키지 구조와 레이어 규칙을 반드시 준수한다.
> 임의로 패키지를 추가하거나 레이어 규칙을 위반하지 않는다.
> 변경이 필요하면 반드시 먼저 질문한다.

---

## 1. 의존성 방향

```
[adapter/in]  Controller
      ↓ UseCase 호출
[domain]      Inbound Port (UseCase 인터페이스)
      ↓
[application] Service (UseCase 구현, @Transactional 경계)
      ↓ Port 호출
[domain]      Outbound Port (Repository / ~Port 인터페이스)
      ↑ Port 구현
[adapter/out] JpaEntity, FcmAdapter, MockPaymentAdapter ...
```

**절대 금지**
- `domain` → `adapter` 의존
- `domain` → `application` 의존
- `application` → `adapter` 직접 의존 (반드시 Port 인터페이스 경유)
- `adapter/in` → `domain` 직접 import (Command/Result 제외)
- Domain 클래스에 `@Entity`, `@Column` 등 JPA 어노테이션

---

## 2. 클래스 역할 → 위치 · 네이밍

| 역할 | 위치 | 네이밍 | 예시 |
|---|---|---|---|
| Inbound Port | `domain/` | `~UseCase` | `CreateRequestUseCase` |
| Outbound Port (DB) | `domain/` | `~Repository` | `CurationRequestRepository` |
| Outbound Port (외부) | `domain/` | `~Port` | `PaymentPort`, `PushNotificationPort` |
| UseCase 입력 | `domain/` | `~Command` (record) | `CreateRequestCommand` |
| UseCase 출력 | `domain/` | `~Result` (record) | `CreateRequestResult` |
| Application Service | `application/` | `~Service` | `BuyerRequestService` |
| Controller | `adapter/in/` | `~Controller` | `BuyerRequestController` |
| Request DTO | `adapter/in/.../dto/` | `~Request` (record) | `CreateRequestRequest` |
| Response DTO | `adapter/in/.../dto/` | `~Response` (record) | `RequestDetailResponse` |
| JPA Entity | `adapter/out/persistence/` | `~JpaEntity` | `CurationRequestJpaEntity` |
| Repository 구현체 | `adapter/out/persistence/` | `~RepositoryImpl` | `CurationRequestRepositoryImpl` |
| 외부 서비스 Adapter | `adapter/out/` | `~Adapter` | `MockPaymentAdapter`, `FcmPushAdapter` |
| Fake 테스트 구현체 | `test/.../fake/` | `Fake~` | `FakePaymentPort` |

---

## 3. 패키지 구조

```
src/main/java/com/florent/
│
├── domain/
│   ├── user/
│   │   ├── User.java
│   │   ├── UserRole.java                        # enum: BUYER, SELLER
│   │   └── UserRepository.java                  # Outbound Port
│   ├── buyer/
│   │   ├── Buyer.java
│   │   └── BuyerRepository.java
│   ├── seller/
│   │   ├── Seller.java
│   │   └── SellerRepository.java
│   ├── shop/
│   │   ├── FlowerShop.java
│   │   └── FlowerShopRepository.java
│   ├── request/
│   │   ├── CurationRequest.java                 # 상태전이 메서드 포함
│   │   ├── RequestStatus.java                   # enum: OPEN, EXPIRED, CONFIRMED
│   │   ├── BudgetTier.java                      # enum: TIER1~TIER4
│   │   ├── FulfillmentType.java                 # enum: PICKUP, DELIVERY
│   │   ├── TimeSlot.java                        # Value Object: kind + value
│   │   ├── CurationRequestRepository.java
│   │   └── CreateRequestUseCase.java
│   ├── proposal/
│   │   ├── Proposal.java                        # 상태전이 메서드 포함
│   │   ├── ProposalStatus.java                  # enum: DRAFT, SUBMITTED, EXPIRED, SELECTED, NOT_SELECTED
│   │   ├── SlotKind.java                        # enum: PICKUP_30M, DELIVERY_WINDOW
│   │   ├── ProposalRepository.java
│   │   ├── StartProposalUseCase.java
│   │   ├── SaveProposalUseCase.java
│   │   └── SubmitProposalUseCase.java
│   ├── reservation/
│   │   ├── Reservation.java
│   │   ├── ReservationStatus.java               # enum: CONFIRMED
│   │   ├── ReservationRepository.java
│   │   └── ConfirmReservationUseCase.java
│   ├── payment/
│   │   ├── Payment.java
│   │   ├── PaymentStatus.java                   # enum: SUCCEEDED, FAILED
│   │   ├── PaymentPort.java                     # Outbound Port (PG 교체 핵심)
│   │   └── PaymentRepository.java
│   └── notification/
│       ├── Notification.java
│       ├── NotificationType.java                # enum: REQUEST_ARRIVED, PROPOSAL_ARRIVED, RESERVATION_CONFIRMED
│       ├── NotificationRepository.java
│       ├── OutboxEvent.java
│       ├── OutboxStatus.java                    # enum: PENDING, SENT, FAILED
│       ├── OutboxEventRepository.java
│       ├── PushNotificationPort.java            # Outbound Port (FCM 교체 핵심)
│       ├── SaveNotificationUseCase.java
│       ├── UserDevice.java
│       └── UserDeviceRepository.java
│
├── application/
│   ├── auth/AuthService.java
│   ├── buyer/
│   │   ├── BuyerRequestService.java             # CreateRequestUseCase 구현
│   │   ├── BuyerProposalService.java            # Start/Save/SubmitProposalUseCase 구현
│   │   └── BuyerReservationService.java         # ConfirmReservationUseCase + GetBuyerReservation*UseCase 구현
│   ├── seller/
│   │   ├── SellerRequestService.java
│   │   ├── SellerProposalService.java
│   │   ├── SellerReservationService.java        # GetSellerReservation*UseCase 구현
│   │   └── SellerShopService.java
│   ├── notification/NotificationService.java    # SaveNotificationUseCase 구현
│   └── image/ImageService.java
│
├── adapter/
│   ├── in/
│   │   ├── auth/AuthController.java + dto/
│   │   ├── buyer/
│   │   │   ├── BuyerHomeController.java
│   │   │   ├── BuyerRequestController.java
│   │   │   ├── BuyerProposalController.java
│   │   │   ├── BuyerReservationController.java
│   │   │   └── dto/
│   │   ├── seller/
│   │   │   ├── SellerHomeController.java
│   │   │   ├── SellerRequestController.java
│   │   │   ├── SellerProposalController.java
│   │   │   ├── SellerReservationController.java
│   │   │   ├── SellerShopController.java
│   │   │   └── dto/
│   │   ├── notification/NotificationController.java + dto/
│   │   ├── device/DeviceController.java + dto/
│   │   └── image/ImageController.java + dto/
│   │
│   └── out/
│       ├── persistence/
│       │   ├── request/    CurationRequestJpaEntity, JpaRepository, RepositoryImpl
│       │   ├── proposal/   ProposalJpaEntity, JpaRepository, RepositoryImpl
│       │   ├── reservation/ ReservationJpaEntity, JpaRepository, RepositoryImpl
│       │   ├── payment/    PaymentJpaEntity, JpaRepository, RepositoryImpl
│       │   ├── notification/ NotificationJpaEntity, OutboxEventJpaEntity, ...
│       │   ├── user/       UserJpaEntity, UserDeviceJpaEntity, ...
│       │   ├── buyer/      BuyerJpaEntity, ...
│       │   ├── seller/     SellerJpaEntity, ...
│       │   └── shop/       FlowerShopJpaEntity, ...
│       ├── payment/MockPaymentAdapter.java       # PaymentPort 구현
│       ├── push/FcmPushAdapter.java              # PushNotificationPort 구현
│       ├── kakao/KakaoAuthAdapter.java + KakaoLocalAdapter.java
│       ├── storage/S3ImageAdapter.java           # ImageStoragePort 구현
│       └── scheduler/
│           ├── ExpiryScheduler.java             # 요청/제안 만료 처리
│           └── OutboxWorker.java                # Outbox → FCM 전송
│
└── common/
    ├── exception/BusinessException.java, ErrorCode.java, GlobalExceptionHandler.java
    ├── response/ApiResponse.java
    ├── security/JwtProvider.java, JwtAuthFilter.java, SecurityConfig.java, UserPrincipal.java
    └── util/HaversineUtil.java
```

## 3. 리소스 구조

```
src/main/resources/
├── application.yml
├── application-local.yml   # Docker Compose PostgreSQL, Mock FCM
├── application-prod.yml    # AWS RDS, 실 FCM
└── db/migration/
    ├── V1__init_schema.sql
    └── V{n}__description.sql
```
