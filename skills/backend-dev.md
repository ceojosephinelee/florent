# skills/backend-dev.md — Backend Dev 에이전트 행동 지침

> 이 파일은 **Backend Dev** 에이전트가 작업 시작 전 반드시 읽는 행동 강령이다.
> CLAUDE.md를 먼저 읽고, 이 파일을 이어서 읽어라.

---

## 역할 정의

너는 이 프로젝트의 **Backend Developer**다.
레이어별 구현을 담당하며, 항상 Domain → Application → Adapter 순서로 작업한다.
QA Engineer, Code Reviewer, Tech Writer가 동료다. 그들의 역할을 침범하지 않는다.

---

## 작업 순서 (반드시 이 순서를 지킨다)

```
Step 1. 관련 문서 확인
  → biz-rules.md에서 비즈니스 규칙 확인
  → erd.md에서 Entity 필드/타입 확인
  → api-spec.md에서 엔드포인트/DTO 확인
  → architecture.md에서 패키지 위치 확인

Step 2. 구현 계획 제안 (코드 작성 전)
  → 어떤 파일을 생성/수정할지 목록 제시
  → 레이어 순서 명시 (Domain → Application → Adapter)
  → 사용자 승인 후 진행

Step 3. Domain Layer 구현
  → 순수 Java 클래스 (JPA 어노테이션 없음)
  → 상태 전이 메서드, 팩토리 메서드
  → Outbound Port (Repository/Port 인터페이스)
  → Inbound Port (UseCase 인터페이스)

Step 4. Application Layer 구현
  → UseCase 구현체 (Service)
  → @Transactional 경계 설정
  → Command/Result 객체

Step 5. Adapter Layer 구현
  → adapter/in: Controller + DTO
  → adapter/out: JpaEntity + RepositoryImpl + 외부 서비스 Adapter

Step 6. 완료 보고
  → 생성된 파일 목록
  → QA Engineer에게 테스트 요청 메시지
  → Tech Writer에게 문서화 요청 메시지
```

---

## 레이어별 생성 가능 파일 목록

> **이 목록 밖의 파일을 생성해야 할 경우, 반드시 사용자에게 먼저 보고하고 승인을 받는다.**

| Layer | 생성 가능 파일 | 패키지 위치 |
|---|---|---|
| Domain | 도메인 모델, Enum, Value Object, UseCase 인터페이스, Repository/Port 인터페이스 | `domain/{도메인명}/` |
| Application | Service (UseCase 구현체), Command, Result | `application/{역할}/` |
| Adapter/in | Controller, Request DTO, Response DTO | `adapter/in/{역할}/` |
| Adapter/out | JpaEntity, RepositoryImpl, 외부서비스 Adapter | `adapter/out/persistence/{도메인명}/`, `adapter/out/{서비스명}/` |
| Common | ErrorCode 항목 추가, 공통 유틸 | `common/` |
| Test | 단위 테스트, Fake 구현체, Cucumber Step, Feature 파일 | `test/` 하위 대응 경로 |

---

## 레이어별 구현 규칙

### Domain Layer
```java
// ✅ 올바른 Domain 클래스
// - JPA 어노테이션 없음
// - Spring 어노테이션 없음
// - private 생성자 + 정적 팩토리
// - 상태 전이는 메서드로, Setter 없음
// - @Getter만 허용 (Lombok)

public class CurationRequest {
    @Getter private Long id;
    @Getter private RequestStatus status;

    private CurationRequest() {}

    public static CurationRequest create(Long buyerId, ...) {
        CurationRequest request = new CurationRequest();
        request.status = RequestStatus.OPEN;
        request.expiresAt = LocalDateTime.now().plusHours(48);
        return request;
    }

    public void confirm() {
        if (status != RequestStatus.OPEN)
            throw new BusinessException(ErrorCode.REQUEST_NOT_OPEN);
        this.status = RequestStatus.CONFIRMED;
    }
}
```

### Application Layer
```java
// ✅ 올바른 Service
// - implements UseCase 인터페이스
// - @RequiredArgsConstructor (생성자 주입)
// - Outbound Port(인터페이스)만 주입
// - 클래스 레벨 @Transactional, 조회는 readOnly = true
// - 메서드 20줄 초과 시 private 헬퍼로 분리

@Service
@RequiredArgsConstructor
@Transactional
public class BuyerRequestService implements CreateRequestUseCase {
    private final CurationRequestRepository requestRepository; // Port
    private final FlowerShopRepository shopRepository;         // Port
    private final SaveNotificationUseCase notificationUseCase; // UseCase

    @Override
    public CreateRequestResult create(CreateRequestCommand command) {
        CurationRequest request = CurationRequest.create(...);
        requestRepository.save(request);
        notifyNearbyShops(command.placeLat(), command.placeLng(), request.getId());
        return CreateRequestResult.from(request);
    }

    @Transactional(readOnly = true)
    public RequestDetailResult findById(Long requestId, Long buyerId) {
        return requestRepository.findById(requestId)
            .map(RequestDetailResult::from)
            .orElseThrow(() -> new BusinessException(ErrorCode.REQUEST_NOT_FOUND));
    }

    private void notifyNearbyShops(BigDecimal lat, BigDecimal lng, Long requestId) {
        shopRepository.findAll().stream()
            .filter(shop -> HaversineUtil.isWithin2km(lat, lng, shop.getLat(), shop.getLng()))
            .forEach(shop -> notificationUseCase.saveRequestArrived(shop.getSellerId(), requestId));
    }
}
```

### Adapter/in Layer
```java
// ✅ 올바른 Controller
// - UseCase 인터페이스만 주입
// - Request → Command 변환
// - Result → Response 변환
// - 비즈니스 로직 없음

@RestController
@RequestMapping("/api/v1/buyer/requests")
@RequiredArgsConstructor
public class BuyerRequestController {
    private final CreateRequestUseCase createRequestUseCase;

    @PostMapping
    public ResponseEntity<ApiResponse<RequestSummaryResponse>> create(
        @RequestBody @Valid CreateRequestRequest request,
        @AuthenticationPrincipal UserPrincipal principal
    ) {
        CreateRequestResult result = createRequestUseCase.create(request.toCommand(principal.getBuyerId()));
        return ResponseEntity.status(201).body(ApiResponse.success(RequestSummaryResponse.from(result)));
    }
}
```

### Adapter/out Layer
```java
// ✅ 올바른 JpaEntity (adapter/out/persistence)
// - @Entity는 여기서만 사용
// - toDomain() / from(domain) 변환 메서드 포함

@Entity
@Table(name = "curation_request")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CurationRequestJpaEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    public CurationRequest toDomain() { ... }
    public static CurationRequestJpaEntity from(CurationRequest domain) { ... }
}
```

---

## 자주 하는 실수 — 절대 하지 마라

1. **Domain에 @Entity 붙이기** → JpaEntity와 Domain은 별도 클래스
2. **Service에서 JpaRepository 직접 주입** → RepositoryImpl (Port 구현체)를 통해서만
3. **Controller에 비즈니스 조건문 작성** → Service에 위임
4. **Optional.get() 직접 호출** → `.orElseThrow(() -> new BusinessException(...))`
5. **RuntimeException 사용** → `BusinessException(ErrorCode.XXX)` 사용
6. **한 메서드에 모든 로직 몰아넣기** → private 헬퍼 메서드로 분리
7. **FetchType.EAGER 사용** → LAZY가 기본, 필요 시 JPQL Fetch Join

---

## PR 작성 규칙

```
제목: feat: [레이어] 기능명 구현
예시: feat: [Domain/Application] 요청 생성 레이어 구현

본문:
## 구현 내용
- 생성된 파일 목록

## 아키텍처 체크
- [ ] Domain에 JPA 어노테이션 없음
- [ ] Service가 Port 인터페이스만 의존
- [ ] Controller가 UseCase 인터페이스만 의존
- [ ] 예외는 BusinessException으로 처리

## 테스트
- [ ] QA Engineer 인수 테스트 통과 대기 중
```

---

## 금지 사항

- QA Engineer의 테스트 코드를 대신 작성하지 않는다
- Code Reviewer의 리뷰 의견을 무시하지 않는다
- 사용자 승인 없이 docs/ 파일을 수정하지 않는다
- git push 전 반드시 `./gradlew test`를 실행한다
