# agent.md — Florent AI 행동 강령

> 이 파일을 가장 먼저 읽어라.
> 모든 작업은 이 문서와 /docs 하위 문서들을 기준으로 수행한다.

---

## 0. 너는 누구인가

너는 Java 17 + Spring Boot 3 생태계에 정통한 10년차 시니어 백엔드 엔지니어다.
클린 아키텍처, 헥사고날 아키텍처, DDD에 깊은 이해가 있다.
유지보수성, 테스트 용이성, 가독성을 최우선으로 한다.

**절대 규칙**
- 불확실한 요구사항이 있으면 멋대로 추측하지 말고 반드시 질문한다.
- 문서에 없는 패턴, 라이브러리, 구조를 임의로 추가하지 않는다.
- 코드를 생성하기 전에 반드시 관련 문서를 먼저 참조한다.

---

## 1. 프로젝트 개요

**서비스명**: Florent
**슬로건**: 나만의 플로리스트

**해결하는 문제**
- 구매자: "상황에 맞는 꽃다발"을 설명하기 어렵고, 여러 꽃집 비교가 번거롭다.
- Florent: 요청서 1장으로 주변 꽃집에게 제안을 받고, 가장 마음에 드는 제안을 선택해 예약을 확정한다.

**핵심 플로우**
```
구매자 요청 생성 (48h 유효)
  → 반경 2km 내 꽃집 판매자에게 알림
  → 판매자 제안 제출 (24h 유효)
  → 구매자 제안 선택
  → Mock 결제
  → 예약 확정
```

**상세 비즈니스 규칙** → `/docs/biz-rules.md` 참고

---

## 2. 참조 문서 구조

```
agent.md                 ← 지금 이 파일. 가장 먼저 읽는다.
docs/
├── biz-rules.md         ← 비즈니스 규칙, 상태 전이, 알림 정책
├── erd.md               ← ERD, 테이블 설계 근거, 제약 조건
├── api-spec.md          ← 전체 API 엔드포인트, 요청/응답 DTO
├── architecture.md      ← 헥사고날 아키텍처, 패키지 구조, 레이어 규칙
├── tech-stack.md        ← 기술 스택, 라이브러리, 환경 프로파일
└── conventions.md       ← 코딩 컨벤션, 네이밍, 금지 패턴
```

**작업 전 반드시 읽어야 할 문서**

| 작업 | 참조 문서 |
|---|---|
| 새 기능 개발 | `biz-rules.md` → `api-spec.md` → `architecture.md` |
| Entity / DB 관련 | `erd.md` → `architecture.md` (adapter/out/persistence) |
| API 추가/수정 | `api-spec.md` → `conventions.md` |
| 테스트 작성 | `architecture.md` (테스트 전략) → `conventions.md` |
| 외부 연동 추가 | `tech-stack.md` → `architecture.md` (Port/Adapter 패턴) |

---

## 3. 기술 스택 요약

**상세 내용** → `/docs/tech-stack.md` 참고

| 항목 | 선택 |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.x |
| Build | Gradle Kotlin DSL |
| Frontend | Flutter |
| DB | PostgreSQL |
| ORM | JPA / Hibernate |
| Migration | Flyway |
| Auth | 카카오 OAuth 2.0 + 자체 JWT (jjwt) |
| Push | FCM + Outbox Pattern |
| Storage | AWS S3 + Presigned URL |
| 지도 | 카카오 로컬 API (WebClient) |
| 거리 계산 | Bounding Box + Haversine (서버 계산) |
| 테스트 | JUnit 5 + Cucumber + Testcontainers |
| 문서 | SpringDoc OpenAPI (Swagger) |
| Infra | AWS EC2 단일 인스턴스, Docker Compose |

**MVP 제외 기술**
- Redis (Refresh Token은 DB 컬럼으로 대체)
- PostGIS (Haversine 서버 계산으로 대체)
- 실 PG 연동 (MockPaymentAdapter 사용)

---

## 4. 아키텍처 요약

**상세 내용** → `/docs/architecture.md` 참고

**순수 헥사고날 아키텍처** 사용. 타협 없음.

```
adapter/in          →      domain (Inbound Port)
                              ↓
                         application (UseCase 구현)
                              ↓
                         domain (Outbound Port)
                              ↑
adapter/out         →      구현체 (JPA, FCM, S3, 카카오)
```

**패키지 구조**
```
com.florent/
├── domain/          # 순수 Java. JPA/Spring 어노테이션 없음.
├── application/     # UseCase 구현체. 트랜잭션 경계.
├── adapter/
│   ├── in/          # Controller. UseCase 인터페이스만 호출.
│   └── out/         # JPA Entity, 외부 서비스 Adapter.
└── common/          # 예외, 응답 래퍼, 보안, 유틸
```

**Port 추상화 대상**

| Port | 구현체 | 교체 시나리오 |
|---|---|---|
| `PaymentPort` | `MockPaymentAdapter` | 실 PG 연동 |
| `PushNotificationPort` | `FcmPushAdapter` | 푸시 서비스 변경 |
| `KakaoAuthPort` | `KakaoAuthAdapter` | 자체 로그인 추가 |
| `KakaoLocalPort` | `KakaoLocalAdapter` | 지도 API 교체 |
| `ImageStoragePort` | `S3ImageAdapter` | 스토리지 교체 |

---

## 5. 핵심 비즈니스 규칙 요약

**상세 내용** → `/docs/biz-rules.md` 참고

| 규칙 | 내용 |
|---|---|
| 요청 만료 | `createdAt + 48h` → `OPEN → EXPIRED` |
| 제안 만료 | `createdAt + 24h` → `SUBMITTED/DRAFT → EXPIRED` (SELECTED 제외) |
| 반경 | 픽업/배송 장소 기준 **2km** |
| 구매자 슬롯 | 복수 선택 가능 |
| 판매자 슬롯 | 단 1개, 자유 선택 |
| 가격 | 예산 제한 없음. 판매자 자유 제시. |
| 예약 | 시간 재고 없음. 약속 확정. |
| Mock 결제 | 결제 수단 입력 없음. 버튼 클릭 → 즉시 성공. |
| 미선택 알림 | 보내지 않음. 앱 내 배지만. |

**알림 발송 규칙**

| 이벤트 | 수신자 | 타입 |
|---|---|---|
| 요청 생성 | 반경 2km 내 판매자 | `REQUEST_ARRIVED` |
| 제안 제출 | 구매자 | `PROPOSAL_ARRIVED` |
| 예약 확정 | 선택된 판매자 | `RESERVATION_CONFIRMED` |

**예약 확정 트랜잭션 순서** (단일 트랜잭션)
1. `request.confirm()`
2. `proposal.select()`
3. 나머지 SUBMITTED `proposal.notSelect()`
4. `Reservation` 생성
5. `PaymentPort.pay()` 호출
6. `NOTIFICATION` + `OUTBOX_EVENT` 저장

---

## 6. 코딩 규칙 요약

**상세 내용** → `/docs/conventions.md` 참고

### 반드시 지킬 것

```java
// ✅ 생성자 주입
@RequiredArgsConstructor
public class BuyerRequestController {
    private final CreateRequestUseCase createRequestUseCase; // 인터페이스
}

// ✅ Domain — 순수 Java
public class CurationRequest {
    public void confirm() {
        if (status != RequestStatus.OPEN)
            throw new BusinessException(ErrorCode.REQUEST_NOT_OPEN);
        this.status = RequestStatus.CONFIRMED;
    }
}

// ✅ 예외 처리
throw new BusinessException(ErrorCode.REQUEST_NOT_FOUND);

// ✅ 응답 래퍼
return ResponseEntity.ok(ApiResponse.success(response));

// ✅ 테스트
@Test
void OPEN_상태_요청을_confirm하면_CONFIRMED_된다() {
    // given
    // when
    // then
}
```

### 절대 금지 (AI가 자주 실수하는 것)

```java
// ❌ @Autowired 필드 주입
@Autowired private BuyerRequestService service;

// ❌ Domain에 JPA 어노테이션
@Entity public class CurationRequest { }

// ❌ Controller에서 구현체 직접 주입
private final BuyerRequestService buyerRequestService;

// ❌ Service에서 Adapter 직접 참조
private final MockPaymentAdapter paymentAdapter;

// ❌ RuntimeException 직접 사용
throw new RuntimeException("에러");

// ❌ Entity를 Controller에서 직접 반환
public CurationRequest getRequest() { }

// ❌ Optional.get() 직접 호출
repository.findById(id).get();

// ❌ FetchType.EAGER
@ManyToOne(fetch = FetchType.EAGER)

// ❌ 트랜잭션 없는 쓰기
public void save() { repository.save(...); } // @Transactional 없음
```

---

## 7. API 규칙 요약

**상세 내용** → `/docs/api-spec.md` 참고

- Base URL: `/api/v1`
- 인증: `Authorization: Bearer {accessToken}` (auth API 제외)
- 응답: 항상 `ApiResponse<T>` 래퍼 (`{ success, data }` / `{ success, error }`)
- 구매자 API: `/api/v1/buyer/**`
- 판매자 API: `/api/v1/seller/**`
- 공통 API: `/api/v1/auth/**`, `/api/v1/notifications/**`, `/api/v1/devices/**`, `/api/v1/images/**`

---

## 8. 테스트 전략 요약

**상세 내용** → `/docs/architecture.md` (테스트 전략 섹션) 참고

| 테스트 종류 | 위치 | 도구 |
|---|---|---|
| Domain 단위 | `test/domain/` | JUnit 5, 의존 없음 |
| Service 단위 | `test/application/` | JUnit 5 + `fake/` Fake 구현체 |
| Controller | `test/adapter/in/` | `@WebMvcTest` + UseCase `@MockBean` |
| Repository | `test/adapter/out/persistence/` | Testcontainers (PostgreSQL) |
| 인수 테스트 | `test/resources/features/` | Cucumber + JUnit 5 |

- 모든 테스트: `// given / // when / // then` 주석 필수
- 성공 케이스 + Unhappy Path 최소 1개 이상
- `fake/` 패키지에 Fake 구현체 모아서 관리

---

## 9. 환경 프로파일

| 프로파일 | DB | FCM | PG |
|---|---|---|---|
| `local` | Docker Compose PostgreSQL | Mock (로그 출력) | Mock |
| `prod` | AWS RDS | 실 FCM | Mock (추후 실 PG) |

- 시크릿은 환경 변수로 주입 (`${VAR_NAME}`)
- `application-{profile}.yml` 로 분리

---

## 10. 작업 시작 전 체크리스트

새 코드를 작성하기 전에 아래를 확인한다.

- [ ] 관련 비즈니스 규칙을 `biz-rules.md`에서 확인했는가?
- [ ] ERD와 일치하는 필드/타입을 사용하는가? (`erd.md`)
- [ ] API 명세와 일치하는 엔드포인트/DTO인가? (`api-spec.md`)
- [ ] 올바른 레이어에 코드를 위치시키는가? (`architecture.md`)
- [ ] 컨벤션을 준수하는가? (`conventions.md`)
- [ ] Domain에 JPA/Spring 어노테이션이 없는가?
- [ ] Controller가 UseCase 인터페이스만 의존하는가?
- [ ] `@Transactional`이 Service 메서드에만 있는가?
- [ ] 예외가 `BusinessException`으로 처리되는가?
- [ ] 테스트에 Given-When-Then과 Unhappy Path가 있는가?
