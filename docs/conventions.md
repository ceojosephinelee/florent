# conventions.md — Florent 코딩 컨벤션

> AI는 이 문서의 규칙을 모든 코드에 적용한다.
> 규칙에 없는 패턴을 사용해야 할 경우 반드시 먼저 질문한다.

---

## 1. 기본 원칙

- 모든 의존성은 **생성자 주입** (`@RequiredArgsConstructor`) 사용. `@Autowired` 필드 주입 금지.
- 메서드는 하나의 책임만. 20라인 초과 시 분리 고려.
- 매직 넘버/문자열 금지. 상수 또는 enum으로 정의.
- `else` 사용 최소화. Early return 패턴 우선.
- 주석은 "무엇"이 아닌 "왜"를 설명. 코드로 표현 가능한 것은 주석 대신 코드로.

---

## 2. 네이밍 규칙

### 클래스

| 종류 | 규칙 | 예시 |
|---|---|---|
| Domain 클래스 | 명사 | `CurationRequest`, `Proposal` |
| Inbound Port | `~UseCase` | `CreateRequestUseCase` |
| Outbound Port (Repository) | `~Repository` | `CurationRequestRepository` |
| Outbound Port (외부 서비스) | `~Port` | `PaymentPort`, `PushNotificationPort` |
| Application Service | `~Service` | `BuyerRequestService` |
| Inbound Adapter (Controller) | `~Controller` | `BuyerRequestController` |
| Outbound Adapter (JPA) | `~JpaEntity` / `~RepositoryImpl` | `CurationRequestJpaEntity` |
| Outbound Adapter (외부) | `~Adapter` | `MockPaymentAdapter`, `FcmPushAdapter` |
| Command (UseCase 입력) | `~Command` | `CreateRequestCommand` |
| Result (UseCase 출력) | `~Result` | `CreateRequestResult` |
| Request DTO | `~Request` | `CreateRequestRequest` |
| Response DTO | `~Response` | `RequestDetailResponse` |
| 테스트 Fake | `Fake~` | `FakePaymentPort` |

### 메서드

| 종류 | 규칙 | 예시 |
|---|---|---|
| 상태 전이 | 동사 | `confirm()`, `expire()`, `select()` |
| 팩토리 | `create()` / `of()` | `CurationRequest.create()` |
| 재구성 (DB → Domain) | `reconstitute()` | `CurationRequest.reconstitute()` |
| 변환 (JpaEntity → Domain) | `toDomain()` | `entity.toDomain()` |
| 변환 (Domain → JpaEntity) | `from()` | `CurationRequestJpaEntity.from(domain)` |
| 조회 (단건) | `findBy~` / `get~` | `findById()`, `getByKakaoId()` |
| 조회 (다건) | `findAll~` / `find~By~` | `findAllByBuyerId()` |
| 검증 | `is~` / `has~` / `can~` | `isExpired()`, `canSubmit()` |
| 저장 | `save()` | `requestRepository.save()` |

### 변수 / 필드

- 컬렉션: 복수형 (`shops`, `proposals`)
- Boolean: `is~` / `has~` (`isRead`, `isActive`)
- 약어 금지 (`req` → `request`, `res` → `response`, `cnt` → `count`)

### 패키지

- 전부 소문자, 단수형 (`request` not `requests`)

---

## 3. Domain 레이어 규칙

```java
// ✅ 올바른 Domain 클래스
public class CurationRequest {

    // 필드 — Lombok @Getter 사용, Setter 금지
    @Getter private Long id;
    @Getter private RequestStatus status;
    @Getter private LocalDateTime expiresAt;

    // 생성자 — private. 정적 팩토리 메서드로만 생성.
    private CurationRequest() {}

    // 정적 팩토리 — 신규 생성
    public static CurationRequest create(Long buyerId, BudgetTier budgetTier, ...) {
        CurationRequest request = new CurationRequest();
        request.status = RequestStatus.OPEN;
        request.expiresAt = LocalDateTime.now().plusHours(48);
        return request;
    }

    // 정적 팩토리 — DB 재구성 (JpaEntity → Domain)
    public static CurationRequest reconstitute(Long id, RequestStatus status, ...) {
        CurationRequest request = new CurationRequest();
        request.id = id;
        request.status = status;
        return request;
    }

    // 상태 전이 — 비즈니스 규칙 포함
    public void confirm() {
        if (this.status != RequestStatus.OPEN) {
            throw new BusinessException(ErrorCode.REQUEST_NOT_OPEN);
        }
        this.status = RequestStatus.CONFIRMED;
    }

    // 검증 메서드
    public boolean isExpired() {
        return this.status == RequestStatus.EXPIRED
            || LocalDateTime.now().isAfter(this.expiresAt);
    }
}
```

**금지 목록**
```java
// ❌ JPA 어노테이션 금지
@Entity
public class CurationRequest { ... }

// ❌ Setter 금지 (상태 전이는 메서드로)
public void setStatus(RequestStatus status) { ... }

// ❌ @Transactional 금지
@Transactional
public void confirm() { ... }

// ❌ Spring / 외부 기술 import 금지
import org.springframework.stereotype.Component;
import javax.persistence.Entity;
```

---

## 4. Application 레이어 규칙

```java
// ✅ 올바른 Service
@Service
@RequiredArgsConstructor
@Transactional                              // 클래스 레벨: 기본 쓰기 트랜잭션
public class BuyerRequestService implements CreateRequestUseCase {

    private final CurationRequestRepository requestRepository;  // Outbound Port
    private final FlowerShopRepository shopRepository;          // Outbound Port
    private final SaveNotificationUseCase notificationUseCase;  // Inbound Port (내부 호출용)

    @Override
    public CreateRequestResult create(CreateRequestCommand command) {
        CurationRequest request = CurationRequest.create(...);
        requestRepository.save(request);

        findNearbyShops(command.placeLat(), command.placeLng())
            .forEach(shop ->
                notificationUseCase.saveRequestArrived(shop.getSellerId(), request.getId()));

        return CreateRequestResult.from(request);
    }

    @Transactional(readOnly = true)         // 조회는 readOnly
    public RequestDetailResult findById(Long requestId, Long buyerId) {
        CurationRequest request = requestRepository.findById(requestId)
            .orElseThrow(() -> new BusinessException(ErrorCode.REQUEST_NOT_FOUND));
        return RequestDetailResult.from(request);
    }

    // private 헬퍼 메서드로 분리
    private List findNearbyShops(BigDecimal lat, BigDecimal lng) {
        return shopRepository.findAll().stream()
            .filter(shop -> HaversineUtil.isWithin2km(lat, lng, shop.getLat(), shop.getLng()))
            .toList();
    }
}
```

**금지 목록**
```java
// ❌ Adapter 구현체 직접 주입 금지
private final MockPaymentAdapter paymentAdapter;  // PaymentPort로

// ❌ adapter 패키지 import 금지
import com.florent.adapter.out.payment.MockPaymentAdapter;

// ❌ Controller에서 받은 HttpServletRequest 등 웹 객체 금지
public void create(HttpServletRequest httpRequest) { ... }
```

---

## 5. Adapter/in (Controller) 레이어 규칙

```java
// ✅ 올바른 Controller
@RestController
@RequestMapping("/api/v1/buyer/requests")
@RequiredArgsConstructor
public class BuyerRequestController {

    private final CreateRequestUseCase createRequestUseCase;    // 인터페이스만 주입

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

    @GetMapping("/{requestId}")
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse> getDetail(
        @PathVariable Long requestId,
        @AuthenticationPrincipal UserPrincipal principal
    ) {
        RequestDetailResult result = createRequestUseCase.findById(requestId, principal.getUserId());
        return ResponseEntity.ok(ApiResponse.success(RequestDetailResponse.from(result)));
    }
}
```

**금지 목록**
```java
// ❌ Service 구현체 직접 주입 금지
private final BuyerRequestService buyerRequestService;  // CreateRequestUseCase로

// ❌ 비즈니스 로직 금지
if (request.getBudgetTier() == BudgetTier.TIER1) { ... }

// ❌ domain 직접 import 금지 (Command/Result 제외)
import com.florent.domain.request.CurationRequest;
```

---

## 6. Adapter/out (JPA Entity) 규칙

```java
// ✅ 올바른 JpaEntity
@Entity
@Table(name = "curation_request")
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // JPA 기본 생성자
@Getter
public class CurationRequestJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    @Column(nullable = false)
    private Long buyerId;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    private void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    private void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Domain → JpaEntity
    public static CurationRequestJpaEntity from(CurationRequest domain) {
        CurationRequestJpaEntity entity = new CurationRequestJpaEntity();
        entity.id = domain.getId();
        entity.status = domain.getStatus();
        entity.buyerId = domain.getBuyerId();
        entity.expiresAt = domain.getExpiresAt();
        return entity;
    }

    // JpaEntity → Domain
    public CurationRequest toDomain() {
        return CurationRequest.reconstitute(
            this.id, this.status, this.buyerId, this.expiresAt, ...);
    }
}

// ✅ RepositoryImpl
@Repository
@RequiredArgsConstructor
public class CurationRequestRepositoryImpl implements CurationRequestRepository {

    private final CurationRequestJpaRepository jpaRepository;

    @Override
    public CurationRequest save(CurationRequest domain) {
        return jpaRepository.save(CurationRequestJpaEntity.from(domain)).toDomain();
    }

    @Override
    public Optional findById(Long id) {
        return jpaRepository.findById(id).map(CurationRequestJpaEntity::toDomain);
    }
}
```

**JPA 관련 규칙**
- `@Entity`는 `adapter/out/persistence` 패키지에만
- `FetchType.LAZY` 기본 사용. `EAGER` 금지.
- N+1 방지: 연관 조회 필요 시 `@Query` + `JOIN FETCH` 또는 `@EntityGraph`
- JSON 컬럼 (`tags_json` 등): `@Convert` + `AttributeConverter` 또는 String 직렬화/역직렬화
- `@Setter` JpaEntity에도 금지. `from()` / `toDomain()` 으로만 변환.

---

## 7. DTO 규칙

```java
// ✅ Request DTO — Java record 사용
public record CreateRequestRequest(
    @NotEmpty List purposeTags,
    @NotEmpty List relationTags,
    @NotEmpty List moodTags,
    @NotNull BudgetTier budgetTier,
    @NotNull FulfillmentType fulfillmentType,
    @NotNull LocalDate fulfillmentDate,
    @NotEmpty List requestedTimeSlots,
    @NotBlank String placeAddressText,
    @NotNull BigDecimal placeLat,
    @NotNull BigDecimal placeLng
) {
    // DTO → Command 변환 메서드
    public CreateRequestCommand toCommand(Long buyerId) {
        return new CreateRequestCommand(buyerId, purposeTags, ...);
    }
}

// ✅ Response DTO — Java record 사용
public record RequestSummaryResponse(
    Long requestId,
    String status,
    String budgetTier,
    String fulfillmentType,
    LocalDate fulfillmentDate,
    LocalDateTime expiresAt,
    int draftProposalCount,
    int submittedProposalCount
) {
    // Result → Response 변환 정적 팩토리
    public static RequestSummaryResponse from(CreateRequestResult result) {
        return new RequestSummaryResponse(
            result.requestId(), result.status().name(), ...);
    }
}
```

**DTO 규칙**
- Request/Response DTO는 `Java record` 사용
- Command/Result는 `Java record` 사용 (domain 패키지에 위치)
- DTO에 비즈니스 로직 금지. 변환 메서드(`toCommand()`, `from()`)만 허용.
- `@Valid` 검증 어노테이션은 Request DTO 필드에만
- Entity를 Controller에서 직접 반환 금지. 반드시 DTO로 변환.

---

## 8. 예외 처리 규칙

```java
// ✅ ErrorCode enum — 모든 에러 코드 중앙 관리
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 공통
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "요청 값이 올바르지 않습니다."),

    // Request
    REQUEST_NOT_FOUND(HttpStatus.NOT_FOUND, "요청을 찾을 수 없습니다."),
    REQUEST_NOT_OPEN(HttpStatus.UNPROCESSABLE_ENTITY, "진행 중인 요청이 아닙니다."),
    REQUEST_ALREADY_CONFIRMED(HttpStatus.UNPROCESSABLE_ENTITY, "이미 확정된 요청입니다."),

    // Proposal
    PROPOSAL_NOT_FOUND(HttpStatus.NOT_FOUND, "제안을 찾을 수 없습니다."),
    PROPOSAL_ALREADY_EXISTS(HttpStatus.UNPROCESSABLE_ENTITY, "이미 제안서를 작성했습니다."),
    PROPOSAL_NOT_SUBMITTABLE(HttpStatus.UNPROCESSABLE_ENTITY, "제출할 수 없는 상태입니다."),
    PROPOSAL_EXPIRED(HttpStatus.UNPROCESSABLE_ENTITY, "만료된 제안입니다."),

    // Reservation
    DUPLICATE_PAYMENT(HttpStatus.UNPROCESSABLE_ENTITY, "중복 결제 요청입니다."),

    // Shop
    SHOP_NOT_FOUND(HttpStatus.NOT_FOUND, "꽃집을 찾을 수 없습니다."),
    SHOP_ALREADY_EXISTS(HttpStatus.UNPROCESSABLE_ENTITY, "이미 꽃집이 등록되어 있습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}

// ✅ BusinessException
@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}

// ✅ GlobalExceptionHandler
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse> handleBusinessException(BusinessException e) {
        return ResponseEntity.status(e.getErrorCode().getHttpStatus())
            .body(ApiResponse.error(e.getErrorCode().name(), e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidationException(
            MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.joining(", "));
        return ResponseEntity.badRequest()
            .body(ApiResponse.error("VALIDATION_ERROR", message));
    }
}
```

**예외 규칙**
- `throw new RuntimeException()` / `throw new Exception()` 금지. 반드시 `BusinessException` 사용.
- 예외 메시지에 민감 정보 (토큰, 비밀번호 등) 포함 금지.
- 존재하지 않는 리소스: `ErrorCode.~_NOT_FOUND` (404)
- 비즈니스 규칙 위반: `ErrorCode.~` (422)
- 예외는 발생 지점에서 즉시 throw. 잡아서 로그만 찍고 넘기는 패턴 금지.

---

## 9. 공통 응답 래퍼

```java
// ✅ ApiResponse
@Getter
@RequiredArgsConstructor
public class ApiResponse {

    private final boolean success;
    private final T data;
    private final ErrorResponse error;

    public static  ApiResponse success(T data) {
        return new ApiResponse<>(true, data, null);
    }

    public static ApiResponse error(String code, String message) {
        return new ApiResponse<>(false, null, new ErrorResponse(code, message));
    }

    public record ErrorResponse(String code, String message) {}
}
```

---

## 10. 테스트 규칙

```java
// ✅ 테스트 네이밍 — 한국어, 동작 기술
@Test
void OPEN_상태_요청을_confirm하면_CONFIRMED_상태가_된다() { ... }

@Test
void OPEN이_아닌_요청을_confirm하면_BusinessException이_발생한다() { ... }

// ✅ Given-When-Then 주석 필수
@Test
void 제안_선택_시_나머지_제안이_NOT_SELECTED_된다() {
    // given
    CurationRequest request = CurationRequest.create(...);
    Proposal selectedProposal = Proposal.create(request.getId(), shopId1);
    Proposal otherProposal = Proposal.create(request.getId(), shopId2);

    // when
    reservationService.confirmReservation(selectedProposal.getId(), idempotencyKey, buyerId);

    // then
    assertThat(otherProposal.getStatus()).isEqualTo(ProposalStatus.NOT_SELECTED);
}
```

**테스트 규칙**
- 테스트 메서드명: 한국어로 동작 기술 (`~하면_~된다`, `~시_예외가_발생한다`)
- `// given / // when / // then` 주석 필수
- 성공 케이스 + Unhappy Path 최소 1개 이상 필수
- `fake/` 패키지 Fake 구현체 우선 사용. Mockito는 외부 라이브러리 테스트에만.
- `@SpringBootTest` 최소화. 단위 테스트 우선.
- 테스트 간 상태 공유 금지. `@BeforeEach`로 초기화.

---

## 11. AI가 자주 저지르는 실수 — 금지 패턴

```java
// ❌ 1. @Autowired 필드 주입 금지
@Autowired
private BuyerRequestService buyerRequestService;

// ✅ 대신
@RequiredArgsConstructor
public class BuyerRequestController {
    private final CreateRequestUseCase createRequestUseCase;
}

// ❌ 2. Entity를 Controller에서 직접 반환 금지
@GetMapping("/{id}")
public CurationRequest getRequest(@PathVariable Long id) { ... }

// ✅ 대신
public ResponseEntity<ApiResponse> getRequest(...) { ... }

// ❌ 3. RuntimeException 직접 사용 금지
throw new RuntimeException("요청을 찾을 수 없습니다.");

// ✅ 대신
throw new BusinessException(ErrorCode.REQUEST_NOT_FOUND);

// ❌ 4. Service에서 Adapter 직접 참조 금지
private final MockPaymentAdapter paymentAdapter;

// ✅ 대신
private final PaymentPort paymentPort;

// ❌ 5. Domain에 JPA 어노테이션 금지
@Entity
public class CurationRequest { ... }

// ✅ 대신 adapter/out/persistence/request/CurationRequestJpaEntity.java 에 @Entity

// ❌ 6. Controller에서 UseCase 구현체 직접 주입 금지
private final BuyerRequestService buyerRequestService;

// ✅ 대신
private final CreateRequestUseCase createRequestUseCase;

// ❌ 7. N+1 유발하는 즉시 로딩 금지
@ManyToOne(fetch = FetchType.EAGER)
private SellerJpaEntity seller;

// ✅ 대신
@ManyToOne(fetch = FetchType.LAZY)
private SellerJpaEntity seller;

// ❌ 8. Optional을 get()으로 바로 꺼내기 금지
repository.findById(id).get();

// ✅ 대신
repository.findById(id)
    .orElseThrow(() -> new BusinessException(ErrorCode.REQUEST_NOT_FOUND));

// ❌ 9. 트랜잭션 없는 쓰기 작업 금지
public void save(CurationRequest request) {  // @Transactional 없음
    requestRepository.save(request);
}

// ❌ 10. 테스트에서 @SpringBootTest 남용 금지 (단위 테스트에서)
@SpringBootTest
class CurationRequestTest { ... }  // 도메인 단위 테스트에 Spring 컨텍스트 불필요
```

---

## 12. Flyway 마이그레이션 규칙

```sql
-- 파일명: V{순번}__{snake_case_설명}.sql
-- 예: V1__init_schema.sql, V2__add_proposal_image_urls.sql

-- 규칙
-- 1. 기존 마이그레이션 파일 절대 수정 금지 (새 파일로 추가)
-- 2. 컬럼명: snake_case
-- 3. 모든 NOT NULL 컬럼에 DEFAULT 또는 초기값 명시
-- 4. 인덱스는 별도 파일로 분리 가능
```

---

## 13. Git 컨벤션

### 13.1 커밋 메시지 규칙 (Conventional Commits)

```text
<type>: <summary>
```

**허용 type**
- `feat`: 사용자 기능 추가/변경
- `fix`: 버그 수정
- `refactor`: 동작 변화 없는 구조 개선
- `test`: 테스트 추가/수정
- `docs`: 문서 변경
- `chore`: 빌드/설정/의존성/기타 유지보수

**규칙**
- summary는 50자 내외로 간결하게 작성
- 한국어로 작성
- "무엇을 왜 바꿨는지"가 드러나게 작성
- 한 커밋에는 한 가지 목적만 담는다
- 호환성 깨짐이 있으면 본문에 `BREAKING CHANGE:` 명시

**예시**
```text
feat: 구매자 홈 조회 API 추가
fix: 제안 만료 스케줄러의 상태 필터 조건 수정
refactor: ProposalRepositoryImpl 조회 로직 분리
test: ReservationService 실패 케이스 단위 테스트 추가
docs: 요청 타임슬롯 규칙 문서화
chore: 로컬 프로파일 환경 변수 키 정리
```

### 13.2 브랜치 네이밍 규칙

```text
<type>/<short-description>
```

**규칙**
- type은 `feat|fix|refactor|test|docs|chore` 중 하나
- short-description은 소문자 kebab-case 사용
- `main`에 직접 커밋 금지
- 기능/수정은 반드시 작업 브랜치에서 진행

**예시**
```text
feat/buyer-home-api
fix/proposal-expire-scheduler
docs/update-git-conventions
```

### 13.3 PR 규칙

**PR 제목**
- `<type>: <summary>` 형식 사용

**PR 본문 필수 항목**
- 배경/목적
- 주요 변경 사항
- 테스트 결과
- 영향 범위 (`backend`, `frontend`, `api`, `db`, `docs`)
- 후속 작업 (있다면)

**리뷰/머지 규칙**
- 기능 변경 + 리팩터링 + 포맷 변경을 한 PR에 섞지 않는다
- CI/테스트 통과 후 머지한다
- 설명 없는 PR 머지 금지

### 13.4 금지 패턴

- `WIP`, `update`, `fix bug` 같은 의미 없는 커밋 메시지
- 하나의 커밋에 서로 다른 성격 변경(기능 + 리팩터링 + 포맷)을 혼합
- 충돌 해결 후 테스트 생략
- 변경 이유 없이 대규모 PR 생성

### 13.5 Git 체크리스트

- [ ] 커밋 type이 규칙에 맞는가?
- [ ] 커밋 메시지에 변경 이유가 드러나는가?
- [ ] 브랜치명이 규칙에 맞는가?
- [ ] PR 본문 필수 항목을 채웠는가?
- [ ] 테스트 결과를 확인했는가?

