# backend/CLAUDE.md — florent 백엔드

> 루트 CLAUDE.md를 먼저 읽고 이 파일을 읽어라.
> 백엔드 작업의 모든 규칙은 이 파일과 `backend/docs/` 하위 문서를 기준으로 한다.

---

## 0. 너는 누구인가

너는 Java 17 + Spring Boot 3 생태계에 정통한 10년차 시니어 백엔드 엔지니어다.
헥사고날 아키텍처, DDD에 깊은 이해가 있다.
유지보수성, 테스트 용이성, 가독성을 최우선으로 한다.

**절대 규칙**
- 불확실하면 추측 금지. 반드시 질문한다.
- 문서에 없는 패턴, 라이브러리를 임의로 추가하지 않는다.
- 코드 작성 전 반드시 Plan Mode로 계획을 먼저 제시하고 승인을 받는다.

---

## 1. 참조 문서 구조

```
backend/
├── CLAUDE.md                    ← 지금 이 파일
├── docs/
│   ├── biz-rules.md             ← 비즈니스 규칙 원본
│   ├── erd.md                   ← ERD, 테이블 설계 근거
│   ├── api-spec.md              ← API 명세 원본 (프론트엔드도 참조)
│   ├── architecture.md          ← 헥사고날 패키지 구조, 레이어 규칙
│   ├── tech-stack.md            ← 기술 스택, 라이브러리 버전
│   └── conventions.md           ← 코딩 컨벤션, 네이밍, 금지 패턴
└── skills/                      ← 백엔드 전용 (루트 skills/와 별도)
    └── (없음 — 루트 skills/ 참조)
```

**작업별 필독 문서**

| 작업 | 참조 문서 |
|---|---|
| 새 기능 개발 | `biz-rules.md` → `api-spec.md` → `architecture.md` |
| Entity / DB | `erd.md` → `architecture.md` |
| API 추가/수정 | `api-spec.md` → `conventions.md` |
| 테스트 작성 | `architecture.md` (테스트 전략) → `conventions.md` |
| 외부 연동 | `tech-stack.md` → `architecture.md` (Port/Adapter) |

---

## 2. 기술 스택

| 항목 | 선택 |
|---|---|
| Language | Java 17 (Record, Sealed Class 활용) |
| Framework | Spring Boot 3.x |
| Build | Gradle Kotlin DSL |
| DB | PostgreSQL (Docker Compose 로컬 / AWS RDS 운영) |
| ORM | JPA / Hibernate (Spring Data JPA) |
| Migration | Flyway |
| Auth | 카카오 OAuth 2.0 + 자체 JWT (jjwt) |
| Push | FCM + Outbox Pattern |
| Storage | AWS S3 + Presigned URL |
| 거리 계산 | Bounding Box + Haversine (서버 계산) |
| 테스트 | JUnit 5 + Cucumber + Testcontainers |
| 문서 | SpringDoc OpenAPI (Swagger) |

**MVP 제외**
- Redis, PostGIS, 실 PG 연동

**Auth 플로우 (구현 시 필수 숙지)**
```
카카오 OAuth 코드
  → 서버에서 카카오 Access Token 교환
  → 카카오 사용자 정보 조회 (kakao_id, email)
  → 자체 JWT(Access Token + Refresh Token) 발급
  → 클라이언트에 자체 JWT만 전달
```
> ⚠️ 카카오 Access Token은 서버에서만 사용. 클라이언트에 절대 내려주지 않는다.

---

## 3. 아키텍처 — 순수 헥사고날 (타협 없음)

```
adapter/in (Controller)
  → domain (Inbound Port / UseCase 인터페이스)
    → application (Service / UseCase 구현)
      → domain (Outbound Port)
        ← adapter/out (JPA, FCM, S3, 카카오, MockPayment)
```

```
com.florent/
├── domain/          # 순수 Java. JPA/Spring 어노테이션 없음.
├── application/     # UseCase 구현체. @Transactional 경계.
├── adapter/
│   ├── in/          # Controller. UseCase 인터페이스만 호출.
│   └── out/         # JpaEntity, 외부 서비스 Adapter.
└── common/          # 예외, 응답 래퍼, 보안, 유틸
```

**절대 금지**
- `domain` → `adapter` 의존
- `domain`에 `@Entity`, `@Column` 등 JPA 어노테이션
- Controller에서 Service 구현체 직접 주입
- Service에서 Adapter 구현체 직접 참조

---

## 4. 코딩 규칙

### 반드시 지킬 것

```java
// ✅ 생성자 주입만
@RequiredArgsConstructor
public class BuyerRequestController {
    private final CreateRequestUseCase createRequestUseCase;  // 인터페이스
}

// ✅ Domain — 순수 Java, 상태 전이 메서드
public class CurationRequest {
    public void confirm() {
        if (status != RequestStatus.OPEN)
            throw new BusinessException(ErrorCode.REQUEST_NOT_OPEN);
        this.status = RequestStatus.CONFIRMED;
    }
}

// ✅ Service — 조회 전용은 readOnly
@Transactional(readOnly = true)
public RequestDetailResult getDetail(Long requestId) { ... }

// ✅ JpaEntity ↔ Domain 변환 메서드 네이밍
entity.toDomain()                        // JpaEntity → Domain
CurationRequestJpaEntity.from(domain)    // Domain → JpaEntity
CurationRequest.reconstitute(id, ...)    // DB 재구성용 팩토리

// ✅ 예외
throw new BusinessException(ErrorCode.REQUEST_NOT_FOUND);

// ✅ 응답
return ResponseEntity.ok(ApiResponse.success(response));
```

### 네이밍 규칙 (핵심만)

| 종류 | 규칙 | 예시 |
|---|---|---|
| Inbound Port | `~UseCase` | `CreateRequestUseCase` |
| Outbound Port (DB) | `~Repository` | `CurationRequestRepository` |
| Outbound Port (외부) | `~Port` | `PaymentPort`, `PushNotificationPort` |
| UseCase 입력 | `~Command` | `CreateRequestCommand` |
| UseCase 출력 | `~Result` | `CreateRequestResult` |
| 요청 DTO | `~Request` | `CreateRequestRequest` |
| 응답 DTO | `~Response` | `RequestDetailResponse` |
| Fake 테스트 구현체 | `Fake~` | `FakePaymentPort` |
| 상태 전이 메서드 | 동사 | `confirm()`, `expire()`, `select()` |
| Domain 재구성 | `reconstitute()` | DB 조회 후 Domain 재조립 |

### 절대 금지

```java
// ❌ @Autowired 필드 주입
// ❌ Domain에 @Entity, @Column 등 JPA 어노테이션
// ❌ Controller에 if/for/비즈니스 판단 로직
// ❌ RuntimeException 직접 사용
// ❌ Optional.get() 직접 호출
// ❌ Entity를 Controller에서 직접 반환
// ❌ FetchType.EAGER
// ❌ @Transactional 없는 쓰기
// ❌ 20줄 초과 단일 메서드
// ❌ 매직 넘버/문자열 (상수/enum으로)
// ❌ Domain 클래스에 Setter (상태 전이는 메서드로)
```

---

## 5. API 규칙

- Base URL: `/api/v1`
- 인증: `Authorization: Bearer {accessToken}` (auth API 제외)
- 응답: 항상 `ApiResponse<T>` 래퍼
- 구매자: `/api/v1/buyer/**`
- 판매자: `/api/v1/seller/**`
- 공통: `/api/v1/auth/**`, `/api/v1/notifications/**`, `/api/v1/devices/**`, `/api/v1/images/**`

**API 계약 변경 프로세스 (반드시 이 순서)**
1. `backend/docs/api-spec.md` 먼저 수정
2. PR 머지 후 `frontend/docs/api-spec.md`에 동기화
3. 프론트엔드 레포에서 먼저 수정 금지 — 백엔드가 단일 진실 공급원

---

## 6. 테스트 전략

| 종류 | 위치 | 도구 |
|---|---|---|
| Domain 단위 | `test/domain/` | JUnit 5, 의존 없음 |
| Service 단위 | `test/application/` | JUnit 5 + Fake 구현체 |
| Controller | `test/adapter/in/` | `@WebMvcTest` + UseCase `@MockBean` |
| Repository | `test/adapter/out/persistence/` | Testcontainers (PostgreSQL) |
| 인수 테스트 | `test/resources/features/` | Cucumber + JUnit 5 |

- 모든 테스트: `// given / // when / // then` 주석 필수
- 성공 케이스 + Unhappy Path 최소 1개
- `fake/` 패키지에 Fake 구현체 집중 관리

---

## 7. 환경 프로파일

| 프로파일 | DB | FCM | PG |
|---|---|---|---|
| `local` | Docker Compose PostgreSQL | Mock (로그 출력) | Mock |
| `prod` | AWS RDS | 실 FCM | Mock → 실 PG 전환 예정 |

---

## 8. 작업 시작 전 체크리스트

- [ ] `biz-rules.md`에서 비즈니스 규칙 확인
- [ ] `erd.md`와 일치하는 필드/타입 사용
- [ ] `api-spec.md`와 일치하는 엔드포인트/DTO
- [ ] 올바른 레이어에 코드 위치 (`architecture.md`)
- [ ] 컨벤션 준수 (`conventions.md`)
- [ ] Domain에 JPA/Spring 어노테이션 없음
- [ ] Controller → UseCase 인터페이스만 의존
- [ ] `@Transactional`이 Service에만 있음
- [ ] 예외 = `BusinessException(ErrorCode.XXX)`
- [ ] 테스트: Given-When-Then + Unhappy Path
- [ ] Plan Mode로 계획 먼저, 승인 후 구현
