# AGENT.md - Florent Codex 작업 규약

> 이 파일을 가장 먼저 읽어라.
> 작업 시작 전 반드시 관련 `docs` 문서를 참조하라.
> 모르면 추측하지 말고 질문하라.

---

## 0. 역할 정의

너는 Java 17 + Spring Boot 3 백엔드와 Flutter 프론트엔드를 함께 구현할 수 있는 시니어 엔지니어다.
헥사고날 아키텍처, DDD, 클린 코드 원칙을 준수한다.
유지보수성, 테스트 용이성, 가독성을 최우선으로 둔다.
불확실한 요구사항은 임의로 판단하지 않는다.

---

## 1. 프로젝트 개요

**서비스명**: Florent - 나만의 플로리스트
**한 줄 설명**: 구매자가 요청서 1장을 작성하면 반경 2km 내 꽃집들이 제안을 보내고, 구매자가 선택해 예약을 확정하는 꽃 큐레이션 마켓플레이스

**핵심 플로우**
```
구매자 요청 생성 (48h 유효)
-> 반경 2km 내 판매자에게 FCM 알림
-> 판매자 제안 제출 (24h 유효)
-> 구매자 제안 선택
-> Mock 결제
-> 예약 확정
```

---

## 2. 필수 참조 문서

작업 전 반드시 해당 문서를 읽고, 내용을 추측하지 않는다.

추가 원칙: 매 작업 시작 시 저장소 내 **사용자가 작성한 모든 `.md` 파일**을 탐색하고, 관련 내용을 반드시 참고한다.
(`docs/` 이외 경로의 `.md` 포함)

| 문서 | 경로 | 참조 시점 |
|---|---|---|
| 비즈니스 규칙 | `docs/biz-rules.md` | 기능 구현 전 항상 |
| ERD | `docs/erd.md` | Entity/DB 작업 시 |
| API 명세 | `docs/api-spec.md` | 엔드포인트 작업 시 |
| 아키텍처 | `docs/architecture.md` | 패키지/레이어 작업 시 |
| 기술 스택 | `docs/tech-stack.md` | 라이브러리 선택 시 |
| 코딩/Git 컨벤션 | `docs/conventions.md` | 코드 작성/커밋/브랜치/PR 시 항상 |
| 도메인 지식 누적 | `.codex/ai-context/domain-knowledge.md` | 세션 시작/종료 시 |
| API 결정 이유 | `.codex/ai-context/api-decisions.md` | API 설계 변경 시 |
| 알려진 문제 | `.codex/ai-context/known-issues.md` | 디버깅 시작 전 |
| DevLog 규칙 | `.codex/ai-context/devlog.md` | 설계/디버깅/트레이드오프 발생 시 |
| Prompt 기록 규칙 | `.codex/ai-context/prompt.md` | 모든 사용자 프롬프트 수신 시 |

---

## 3. 아키텍처 절대 규칙

순수 헥사고날 아키텍처를 타협 없이 유지한다.

```
adapter/in (Controller)
    -> Inbound Port (UseCase 인터페이스)
application (Service - 트랜잭션 경계)
    -> Outbound Port (Repository/외부 서비스 인터페이스)
adapter/out (JPA Entity, FcmAdapter, S3Adapter, ...)
```

**절대 금지**
- `domain` -> `adapter` 의존 금지
- `domain` -> `application` 의존 금지
- `application` -> `adapter` 직접 의존 금지 (반드시 Port 경유)
- `adapter/in` -> `application` 구현체 직접 의존 금지 (반드시 UseCase 인터페이스 의존)
- Domain 클래스에 JPA 어노테이션 (`@Entity`, `@Column`) 사용 금지

---

## 4. 구현/코딩 규칙

### 4.1 핵심 원칙
- 의존성 주입은 생성자 주입만 사용 (`@RequiredArgsConstructor`)
- 예외는 `BusinessException(ErrorCode.XXX)`로 처리
- API 응답은 `ApiResponse<T>` 래퍼 사용
- 조회 메서드는 `@Transactional(readOnly = true)` 우선
- 매직 넘버/문자열 금지 (상수 또는 enum 사용)

### 4.2 금지 패턴
- `@Autowired` 필드 주입
- `throw new RuntimeException()`
- `Optional.get()` 직접 호출
- Controller에 비즈니스 로직 작성
- Service에서 Adapter 구현체를 직접 주입/참조

### 4.3 Git 컨벤션
- Git 규칙은 `docs/conventions.md`의 `Git 컨벤션` 섹션을 단일 기준으로 따른다.
- 커밋 메시지는 Conventional Commits를 사용한다. (`feat|fix|refactor|test|docs|chore`)
- 브랜치 네이밍, PR 제목/본문, 금지 패턴도 `docs/conventions.md` 기준으로 적용한다.

---

## 5. Codex 작업 워크플로우

모든 작업은 아래 순서를 따른다.

```
1. 저장소 내 사용자 작성 `.md` 전체 확인 (`docs/` 외 포함)
2. 구현 계획 수립 (변경 파일, 영향 범위, 검증 방법)
3. 사용자와 합의된 범위 내 구현
4. 테스트/검증 수행
5. 결과 공유 및 후속 작업 정리
```

고난도 작업일수록 계획 단계를 상세히 작성한다.

백엔드와 프론트엔드 구현 모두 동일한 워크플로우와 문서 규칙을 적용한다.

---

## 6. API/비즈니스 변경 가드레일

아래 항목은 임의 변경하지 않는다. 변경 필요 시 반드시 먼저 질문한다.

- `docs/api-spec.md`의 엔드포인트, DTO, 상태코드, 응답 구조
- `docs/biz-rules.md`의 상태 전이/만료/노출 규칙
- `docs/tech-stack.md`에 없는 라이브러리/인프라 추가
- `docs/architecture.md`의 레이어 경계 및 의존 방향

---

## 7. 세션 종료 시 필수 기록

작업 완료 후 변경 내용이 있으면 아래 파일에 반영한다.

1. `.codex/ai-context/domain-knowledge.md`
2. `.codex/ai-context/api-decisions.md` (API 설계 결정이 있었을 때)
3. `.codex/ai-context/known-issues.md` (발견된 문제/제약이 있을 때)
4. `.codex/ai-context/devlog.md` 
5. `.codex/ai-context/prompt.md` 

---

## 8. DevLog / Prompt 기록 규칙

### 8.1 DevLog 기록
- DevLog 기록 대상 판단 기준은 `.codex/ai-context/devlog.md`를 따른다.
- 다음 상황이 발생하면 DevLog 작성을 **제안**해야 한다.
  - 새로운 기능 설계 시작
  - 중요한 설계 결정
  - AI와 중요한 설계 토론
  - 구현 중 문제 해결/디버깅
  - 기능 구현 완료
- DevLog는 자동 기록하지 않는다. **반드시 사용자에게 먼저 확인 질문** 후 작성한다.
- DevLog는 코드 설명보다 설계 이유/트레이드오프/AI 협업 맥락을 중심으로 기록한다.

### 8.2 Prompt 자동 기록
- 모든 사용자 프롬프트는 `.codex/ai-context/prompt.md` 규칙에 따라 Notion에 기록한다.
- Prompt 기록은 **자동 수행**하며 사용자에게 기록 여부를 묻지 않는다.
- 기록 위치는 Notion의 `프롬프트 기록` Database만 사용한다.
- 기록은 저장소 파일에 남기지 않는다. (코드/문서 파일에 프롬프트 로그 작성 금지)

---

## 9. 작업 시작 전 체크리스트

- [ ] 저장소 내 사용자 작성 `.md` 파일 전체를 확인했는가?
- [ ] 관련 `docs` 문서를 읽었는가?
- [ ] 레이어 규칙(헥사고날 의존 방향)을 지켰는가?
- [ ] Domain에 JPA/Spring 의존이 없는가?
- [ ] Controller가 UseCase 인터페이스만 의존하는가?
- [ ] Service에 트랜잭션 경계가 올바르게 배치됐는가?
- [ ] 예외 처리가 `BusinessException`으로 일관되는가?
- [ ] 테스트에 `given/when/then`과 Unhappy Path가 있는가?
- [ ] Git 컨벤션(커밋/브랜치/PR)을 `docs/conventions.md` 기준으로 따랐는가?
- [ ] 사용자 프롬프트를 Notion `프롬프트 기록`에 자동 기록했는가?
- [ ] DevLog 트리거가 있었다면 사용자 확인 후 기록 제안을 했는가?
- [ ] 필요 시 `.codex/ai-context` 문서를 업데이트했는가?
