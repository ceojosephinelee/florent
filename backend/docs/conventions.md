# conventions.md — Florent 코딩 컨벤션

> 이 문서의 규칙을 모든 코드에 적용한다.
> 규칙에 없는 패턴을 사용해야 할 경우 반드시 먼저 질문한다.

---

## 1. 기본 원칙

- 의존성: **생성자 주입** (`@RequiredArgsConstructor`). `@Autowired` 금지.
- 메서드: 단일 책임. 20줄 초과 시 분리.
- 매직 넘버/문자열 금지. 상수 또는 enum으로.
- `else` 최소화. Early return 우선.
- 주석은 "왜"를 설명. 코드로 표현 가능한 건 코드로.

---

## 2. 네이밍 규칙

### 메서드

| 종류 | 규칙 | 예시 |
|---|---|---|
| 상태 전이 | 동사 | `confirm()`, `expire()`, `select()`, `notSelect()` |
| 신규 생성 팩토리 | `create()` | `CurationRequest.create(...)` |
| DB 재구성 팩토리 | `reconstitute()` | `CurationRequest.reconstitute(...)` |
| JpaEntity → Domain | `toDomain()` | `entity.toDomain()` |
| Domain → JpaEntity | `from()` | `CurationRequestJpaEntity.from(domain)` |
| DTO → Command | `toCommand()` | `request.toCommand(buyerId)` |
| Result → Response | `from()` | `RequestSummaryResponse.from(result)` |
| 단건 조회 | `findBy~` / `get~` | `findById()`, `getByKakaoId()` |
| 다건 조회 | `findAll~` | `findAllByBuyerId()` |
| 검증 | `is~` / `has~` / `can~` | `isExpired()`, `canSubmit()` |

### 변수 · 패키지

- 컬렉션: 복수형 (`shops`, `proposals`)
- Boolean 필드: `is~` / `has~` (`isRead`, `isActive`)
- 약어 금지 (`req` → `request`, `res` → `response`, `cnt` → `count`)
- 패키지: 소문자 단수 (`request`, `proposal`)

---

## 3. DTO 규칙

```java
// Request DTO — record, @Valid 검증 어노테이션은 필드에만
public record CreateRequestRequest(
    @NotEmpty List<String> purposeTags,
    @NotNull  BudgetTier budgetTier,
    @NotNull  FulfillmentType fulfillmentType,
    @NotNull  LocalDate fulfillmentDate,
    @NotEmpty List<TimeSlotRequest> requestedTimeSlots,
    @NotBlank String placeAddressText,
    @NotNull  BigDecimal placeLat,
    @NotNull  BigDecimal placeLng
) {
    public CreateRequestCommand toCommand(Long buyerId) { ... }  // DTO → Command
}

// Response DTO — record
public record RequestSummaryResponse(
    Long requestId,
    String status,
    LocalDateTime expiresAt,
    int draftProposalCount,
    int submittedProposalCount
) {
    public static RequestSummaryResponse from(CreateRequestResult result) { ... }
}
```

규칙:
- Request/Response DTO → `adapter/in/.../dto/` 위치, record 사용
- Command/Result → `domain/` 위치, record 사용
- DTO에 비즈니스 로직 금지. `toCommand()` / `from()` 변환 메서드만 허용.
- Entity를 Controller에서 직접 반환 금지. 반드시 Response DTO로 변환.

---

## 4. JPA 규칙

```java
@Entity @Table(name = "curation_request")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class CurationRequestJpaEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING) @Column(nullable = false)
    private RequestStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist void prePersist() { this.createdAt = this.updatedAt = LocalDateTime.now(); }
    @PreUpdate  void preUpdate()  { this.updatedAt = LocalDateTime.now(); }

    public static CurationRequestJpaEntity from(CurationRequest domain) { ... }
    public CurationRequest toDomain() { return CurationRequest.reconstitute(...); }
}
```

- `FetchType.LAZY` 기본. `EAGER` 금지.
- N+1 방지: `@Query` + `JOIN FETCH` 또는 `@EntityGraph`
- JSON 컬럼 (`tags_json`): `@Convert` + `AttributeConverter` 또는 String 직렬화
- `@Setter` 금지 (JpaEntity 포함)

---

## 5. 예외 처리

```java
// ErrorCode — 모든 에러 코드 중앙 관리
@Getter @RequiredArgsConstructor
public enum ErrorCode {
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "요청 값이 올바르지 않습니다."),
    REQUEST_NOT_FOUND(HttpStatus.NOT_FOUND, "요청을 찾을 수 없습니다."),
    REQUEST_NOT_OPEN(HttpStatus.UNPROCESSABLE_ENTITY, "진행 중인 요청이 아닙니다."),
    REQUEST_ALREADY_CONFIRMED(HttpStatus.UNPROCESSABLE_ENTITY, "이미 확정된 요청입니다."),
    PROPOSAL_NOT_FOUND(HttpStatus.NOT_FOUND, "제안을 찾을 수 없습니다."),
    PROPOSAL_ALREADY_EXISTS(HttpStatus.UNPROCESSABLE_ENTITY, "이미 제안서를 작성했습니다."),
    PROPOSAL_NOT_SUBMITTABLE(HttpStatus.UNPROCESSABLE_ENTITY, "제출할 수 없는 상태입니다."),
    PROPOSAL_EXPIRED(HttpStatus.UNPROCESSABLE_ENTITY, "만료된 제안입니다."),
    DUPLICATE_PAYMENT(HttpStatus.UNPROCESSABLE_ENTITY, "중복 결제 요청입니다."),
    SHOP_NOT_FOUND(HttpStatus.NOT_FOUND, "꽃집을 찾을 수 없습니다."),
    SHOP_ALREADY_EXISTS(HttpStatus.UNPROCESSABLE_ENTITY, "이미 꽃집이 등록되어 있습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}

// 사용
throw new BusinessException(ErrorCode.REQUEST_NOT_FOUND);
```

- `RuntimeException` / `Exception` 직접 throw 금지
- 존재하지 않는 리소스 → `~_NOT_FOUND` (404)
- 비즈니스 규칙 위반 → 해당 ErrorCode (422)
- 예외 메시지에 토큰/비밀번호 등 민감 정보 금지

---

## 6. 공통 응답 래퍼

```java
@Getter @RequiredArgsConstructor
public class ApiResponse<T> {
    private final boolean success;
    private final T data;
    private final ErrorResponse error;

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null);
    }
    public static ApiResponse<Void> error(String code, String message) {
        return new ApiResponse<>(false, null, new ErrorResponse(code, message));
    }

    public record ErrorResponse(String code, String message) {}
}
```

---

## 7. 테스트 규칙

```java
// 메서드명: 한국어, 동작 기술
@Test void OPEN_상태_요청을_confirm하면_CONFIRMED_상태가_된다() { ... }
@Test void OPEN이_아닌_요청을_confirm하면_BusinessException이_발생한다() { ... }

// given / when / then 주석 필수
@Test
void 제안_선택_시_나머지_제안이_NOT_SELECTED_된다() {
    // given
    // when
    // then
}
```

- 성공 케이스 + Unhappy Path 최소 1개 이상
- Service 테스트: `fake/` Fake 구현체 사용 (Mockito 지양)
- Controller 테스트: `@WebMvcTest` + UseCase 인터페이스 `@MockBean`
- `@SpringBootTest` 최소화. 단위 테스트 우선.
- 테스트 간 상태 공유 금지. `@BeforeEach`로 초기화.

---

## 8. Flyway 마이그레이션

- 파일명: `V{순번}__{snake_case}.sql` (예: `V1__init_schema.sql`)
- 기존 파일 수정 절대 금지. 변경 사항은 새 파일로 추가.
- 컬럼명: snake_case
- NOT NULL 컬럼에 DEFAULT 또는 초기값 명시

---

## 9. AI가 자주 틀리는 패턴 — 금지 목록

```java
// ❌ 1. @Autowired 필드 주입
@Autowired private BuyerRequestService service;
// ✅ @RequiredArgsConstructor + private final CreateRequestUseCase useCase;

// ❌ 2. 구현체 직접 주입
private final BuyerRequestService service;
private final MockPaymentAdapter adapter;
// ✅ private final CreateRequestUseCase createRequestUseCase;
// ✅ private final PaymentPort paymentPort;

// ❌ 3. Entity 직접 반환
public CurationRequest getRequest(@PathVariable Long id) { ... }
// ✅ public ResponseEntity<ApiResponse<RequestDetailResponse>> getRequest(...) { ... }

// ❌ 4. RuntimeException 사용
throw new RuntimeException("찾을 수 없습니다.");
// ✅ throw new BusinessException(ErrorCode.REQUEST_NOT_FOUND);

// ❌ 5. Domain에 JPA 어노테이션
@Entity public class CurationRequest { ... }
// ✅ adapter/out/persistence/request/CurationRequestJpaEntity.java 에만 @Entity

// ❌ 6. Optional.get() 직접 호출
repository.findById(id).get();
// ✅ .orElseThrow(() -> new BusinessException(ErrorCode.REQUEST_NOT_FOUND));

// ❌ 7. EAGER 로딩
@ManyToOne(fetch = FetchType.EAGER) private SellerJpaEntity seller;
// ✅ @ManyToOne(fetch = FetchType.LAZY) private SellerJpaEntity seller;

// ❌ 8. @Transactional 없는 쓰기
public void save(...) { repository.save(...); }
// ✅ @Transactional 필수

// ❌ 9. Controller에 비즈니스 로직
if (request.getBudgetTier() == BudgetTier.TIER1) { ... }
// ✅ Service 또는 Domain으로 이동

// ❌ 10. Domain에 Setter
public void setStatus(RequestStatus status) { ... }
// ✅ public void confirm() { ... }  비즈니스 메서드로

// ❌ 11. 조회 메서드에 @Transactional(readOnly) 누락
@Transactional
public RequestDetailResult findById(...) { ... }
// ✅ @Transactional(readOnly = true)
```
