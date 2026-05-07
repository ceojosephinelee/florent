# /feature — 새 기능 추가

> 새 API, 새 화면, 새 도메인 기능을 추가할 때 사용한다.
> 백엔드 Layer 1→6 순서를 반드시 준수한다.
> **Step 0 승인 없이 코드 작성 절대 금지.**

---
## 시작 전 필수 — develop 최신화

```bash
git checkout develop
git pull origin develop
git checkout -b feat/{기능명}
```

→ 이 순서 지키지 않으면 Step 0 시작 금지.

---
## Step 0 — 영향 분석 (승인 필요)

구현 전에 아래를 먼저 나에게 보고한다.

```
[기능 요약]
- 무엇을 만드는가?

[새로 생성할 파일]
- backend/...
- frontend/...

[수정이 불가피한 기존 파일]
- 파일명: 이유

[건드리면 안 되는 파일]
- 파일명: 이유

[기존 API/도메인 영향 여부]
- 기존 Port/UseCase 변경: YES / NO
- 기존 API 응답 구조 변경: YES / NO
- shared/ 위젯 수정: YES / NO
```

→ 내가 "진행해" 하면 Step 1 시작.

---

## Step 1 — 백엔드 구현 (Layer 순서 필수)

각 Layer 완료 후 `./gradlew compileJava` GREEN 확인 후 다음 진행.
GREEN 아니면 다음 Layer 절대 진행 금지.

```
Layer 1. Domain 엔티티 / 값객체
  → domain/ 에 순수 Kotlin/Java 클래스
  → @Entity 금지. JPA 의존성 금지.

Layer 2. Port 인터페이스
  → domain/ 에 Repository/Service 인터페이스 정의

Layer 3. UseCase 인터페이스
  → application/port/in/ 에 Inbound Port 정의

Layer 4. Service 구현체
  → application/service/ 에 UseCase 구현
  → 도메인 로직은 Domain 객체 안에

Layer 5. Adapter 구현
  → adapter/in/  : Controller, Request/Response DTO
  → adapter/out/ : RepositoryImpl, JpaEntity, JpaRepository

Layer 6. 테스트
  → 단위 테스트: Service, Domain
  → ./gradlew test GREEN 확인
```

**절대 금지**
- 기존 Port/UseCase 인터페이스 시그니처 변경
- Domain 레이어에 @Entity, @Service 등 Spring 어노테이션
- 기존 테스트 삭제/수정
- `.claude/ai-context/known-issues.md` 수정 (충돌 원인 — develop에서만 수정)

---

## Step 2 — 프론트엔드 구현

백엔드 완료 후 진행. (백엔드 없이 프론트만 추가하는 경우는 바로 진행)

```
1. backend/docs/api-spec.md 에서 해당 API 확인
2. frontend/lib/features/ 에 화면 구현
3. shared/ 공통 위젯 재사용 우선
   → 새 공통 위젯 추가 시 반드시 나에게 물어볼 것
4. router/ 수정 시 반드시 나에게 물어볼 것
```

---


## Step 3 — 완료 보고

```
[생성된 파일 전체 목록]
-

[수정된 기존 파일 목록]
-

[빌드/테스트 확인]
- ./gradlew test: PASS / FAIL
- flutter build: PASS / FAIL

[DEBT 기록 필요 항목]
- known-issues.md에 추가할 것: (여기에 메모만 남긴다. 파일 수정 절대 금지.)
- 실제 known-issues.md 수정은 PR 머지 후 develop 브랜치에서만 한다.
```
