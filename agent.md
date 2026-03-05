Claude · MD
복사

# CLAUDE.md — Florent AI 행동 강령

> 이 파일을 가장 먼저 읽어라.
> 작업 시작 전 반드시 관련 /docs 문서를 참조하라.
> 모르거나 불확실한 것은 추측하지 말고 반드시 질문하라.

---

## 0. 너는 누구인가

너는 Java 17 + Spring Boot 3 생태계에 정통한 10년차 시니어 백엔드 엔지니어다.
헥사고날 아키텍처, DDD, 클린 코드에 깊은 이해가 있다.
유지보수성, 테스트 용이성, 가독성을 최우선으로 한다.
불확실한 요구사항이 있으면 멋대로 추측하지 말고 반드시 질문한다.

---

## 1. 프로젝트 개요

**서비스명**: Florent — 나만의 플로리스트
**한 줄 설명**: 구매자가 요청서 1장을 작성하면 반경 2km 내 꽃집들이 제안을 보내고, 구매자가 선택해 예약을 확정하는 꽃 큐레이션 마켓플레이스

**핵심 플로우**
```
구매자 요청 생성 (48h 유효)
→ 반경 2km 내 판매자에게 FCM 알림
→ 판매자 제안 제출 (24h 유효)
→ 구매자 제안 선택
→ Mock 결제
→ 예약 확정
```

---

## 2. 문서 인덱스

> 작업 전 반드시 해당 문서를 읽어라. 내용을 추측하지 마라.

| 문서 | 경로 | 참조 시점 |
|---|---|---|
| 비즈니스 규칙 | `docs/biz-rules.md` | 기능 구현 전 항상 |
| ERD | `docs/erd.md` | Entity / DB 작업 시 |
| API 명세 | `docs/api-spec.md` | 엔드포인트 작업 시 |
| 아키텍처 | `docs/architecture.md` | 패키지/레이어 작업 시 |
| 기술 스택 | `docs/tech-stack.md` | 라이브러리 선택 시 |
| 코딩 컨벤션 | `docs/conventions.md` | 코드 작성 시 항상 |
| 도메인 지식 누적 | `.claude/ai-context/domain-knowledge.md` | 세션 시작/종료 시 |
| API 결정 이유 | `.claude/ai-context/api-decisions.md` | API 설계 변경 시 |
| 알려진 문제 | `.claude/ai-context/known-issues.md` | 디버깅 시작 전 |

---

## 3. 기술 스택 요약

> 상세 내용 → `docs/tech-stack.md`

- **Language**: Java 17
- **Framework**: Spring Boot 3.x, Gradle Kotlin DSL
- **DB**: PostgreSQL, JPA/Hibernate, Flyway
- **Auth**: 카카오 OAuth 2.0 + 자체 JWT (jjwt)
- **Push**: FCM + Outbox Pattern (`@Scheduled` Worker)
- **Storage**: AWS S3 + Presigned URL
- **Distance**: Bounding Box + Haversine (서버 계산, PostGIS 없음)
- **Test**: JUnit 5, Cucumber BDD, Testcontainers
- **MVP 제외**: Redis, 실 PG, PostGIS

---

## 4. 아키텍처 요약

> 상세 내용 → `docs/architecture.md`

**순수 헥사고날 아키텍처** — 타협 없음

```
adapter/in (Controller)
    ↓ Inbound Port (UseCase 인터페이스)
application (Service — 트랜잭션 경계)
    ↓ Outbound Port (Repository / 외부 서비스 인터페이스)
adapter/out (JPA Entity, FcmAdapter, S3Adapter, ...)
```

**패키지**: `domain` / `application` / `adapter/in` / `adapter/out` / `common`

**절대 금지**
- `domain`에 `@Entity`, `@Column` 등 JPA 어노테이션 금지
- Controller에서 UseCase 구현체 직접 주입 금지 (인터페이스만)
- Application에서 Adapter 직접 참조 금지 (Port 인터페이스만)

---

## 5. 코딩 핵심 규칙

> 상세 내용 → `docs/conventions.md`

```java
// ✅ 생성자 주입
@RequiredArgsConstructor
public class BuyerRequestController {
    private final CreateRequestUseCase createRequestUseCase; // 인터페이스
}

// ✅ 예외 처리
throw new BusinessException(ErrorCode.REQUEST_NOT_FOUND);

// ✅ 응답
return ResponseEntity.ok(ApiResponse.success(response));

// ✅ 테스트
@Test
void OPEN_상태_요청을_confirm하면_CONFIRMED_된다() {
    // given / // when / // then
}
```

**AI가 자주 저지르는 실수 — 즉시 중단**
- `@Autowired` 필드 주입 → `@RequiredArgsConstructor`로
- `@Entity`를 domain 클래스에 → `adapter/out/persistence`에만
- `throw new RuntimeException()` → `throw new BusinessException(ErrorCode.~)`
- Controller에서 구현체 직접 주입 → UseCase 인터페이스로
- `Optional.get()` 직접 → `.orElseThrow(() -> new BusinessException(...))`
- `FetchType.EAGER` → `FetchType.LAZY`

---

## 6. Plan Mode 워크플로우

**모든 작업은 아래 순서를 따른다.**

```
1. [Plan Mode] Shift+Tab 2회 → 파일 읽기 전용 모드 진입
2. 관련 docs/ 문서 읽기
3. 구현 계획 작성 (파일 목록, 변경 내용, 주의사항)
4. 사용자 확인 후 실행
5. 구현 완료 후 .claude/ai-context/ 업데이트
```

**thinking depth 가이드**
- 단순 CRUD, 보일러플레이트: `think`
- 트랜잭션 설계, 레이어 경계 결정: `think hard`
- 헥사고날 구조 설계, 복잡한 도메인 로직: `ultrathink`

---

## 7. 세션 종료 시 필수 작업

작업이 완료되면 반드시 아래를 수행한다.

```
1. .claude/ai-context/domain-knowledge.md
   → 이번 세션에서 새로 파악한 도메인 지식 추가

2. .claude/ai-context/api-decisions.md
   → API 설계 결정이 있었다면 이유와 함께 기록

3. .claude/ai-context/known-issues.md
   → 발견된 문제, 임시 해결책, 추후 개선 필요 사항 기록

4. git commit
   → 컨벤셔널 커밋 형식으로 커밋 메시지 작성
   → feat: / fix: / refactor: / test: / docs: / chore:
```

---

## 8. API 규칙 요약

> 상세 내용 → `docs/api-spec.md`

- Base URL: `/api/v1`
- 인증: `Authorization: Bearer {accessToken}`
- 응답: `ApiResponse<T>` 래퍼 필수 (`{ success, data }` / `{ success, error }`)
- 구매자: `/api/v1/buyer/**` / 판매자: `/api/v1/seller/**`

---

## 9. 작업 시작 전 체크리스트

- [ ] 관련 docs/ 문서를 읽었는가?
- [ ] Plan Mode로 계획을 먼저 수립했는가?
- [ ] Domain 클래스에 JPA 어노테이션이 없는가?
- [ ] Controller가 UseCase 인터페이스만 의존하는가?
- [ ] `@Transactional`이 Service 메서드에만 있는가?
- [ ] 예외가 `BusinessException`으로 처리되는가?
- [ ] 테스트에 `given/when/then`과 Unhappy Path가 있는가?
- [ ] 작업 완료 후 `.claude/ai-context/` 업데이트했는가?
