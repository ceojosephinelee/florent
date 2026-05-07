# /ux-fix — UX 수정 작업

> e2e 테스트 중 발견한 화면 수정, UX 개선, 레이아웃 변경 시 사용한다.
> 백엔드 수정이 필요한 경우도 이 커맨드로 처리한다.
> **Step 0 승인 없이 코드 작성 절대 금지.**

---

## Step 0 — 범위 선언 (승인 필요)

구현 전에 아래를 먼저 나에게 보고한다.

```
[수정 목표]
- 무엇이 문제인가?

[수정할 파일 목록]
- frontend/lib/...
- backend/...  (백엔드 수정이 필요한 경우만)

[영향받는 다른 파일]
- 같은 위젯을 쓰는 다른 화면
- 같은 API를 쓰는 다른 곳

[공통 파일 건드리는가?]
- shared/ 위젯: YES / NO
- router/: YES / NO
- backend Port/UseCase 시그니처: YES / NO
```

→ 내가 "진행해" 하면 Step 1 시작.

---

## Step 1 — 구현

### 프론트엔드 수정 시
- `frontend/lib/features/` 하위 자유롭게 수정
- 공통 위젯(`shared/`) 수정 시 → 반드시 다시 물어볼 것
- 라우팅(`router/`) 수정 시 → 반드시 다시 물어볼 것

### 백엔드 수정이 필요한 경우
허용:
- Response DTO 필드 추가
- 새 쿼리 파라미터 추가

금지 (반드시 물어볼 것):
- 기존 API 응답 필드 변경/삭제
- Domain 엔티티 필드 변경
- Port/UseCase 인터페이스 시그니처 변경
- 기존 API URL/Method 변경
- `.claude/ai-context/known-issues.md` 수정 (충돌 원인 — develop에서만 수정)

---

## Step 2 — 완료 보고

```
[수정된 파일 전체 목록]
-

[빌드 확인]
- flutter build: PASS / FAIL
- ./gradlew build: PASS / FAIL  (백엔드 건드린 경우만)

[공통 파일 영향 여부]
- shared/ 건드렸는가? YES → 어떤 파일, 왜
- router/ 건드렸는가? YES → 어떤 파일, 왜
```
