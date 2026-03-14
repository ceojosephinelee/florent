# /sync-docs — 문서 동기화 및 살아있는 문서 유지

> 기능 구현 완료 후 또는 주기적으로 실행한다.
> 실제 구현과 문서의 괴리를 감지하고, ai-context 파일들을 최신 상태로 유지한다.

---

## 실행 순서

직접 bash와 파일 읽기를 병행하여 실행한다.

### Step 1. api-spec.md ↔ 실제 구현 비교

아래 파일들을 읽어라:
- `backend/docs/api-spec.md`
- `backend/src/main/java/com/florent/adapter/in/` 하위 전체 Controller 파일

그 다음 bash로 실제 엔드포인트를 추출해라:
```bash
grep -r "@GetMapping\|@PostMapping\|@PutMapping\|@PatchMapping\|@DeleteMapping\|@RequestMapping" \
  backend/src/main/java/com/florent/adapter/in/ \
  --include="*.java" -n
```

비교 결과를 아래 표 형식으로 출력해라:

```
=== api-spec.md ↔ 실제 구현 비교 ===

✅ 일치 (구현 완료):
  - POST /api/v1/buyer/requests

⚠️  명세에 있으나 미구현:
  - POST /api/v1/auth/kakao
  - ...

❌ 구현됐으나 명세에 없음 (무단 추가 의심):
  - (없으면 없음으로 표기)
```

### Step 2. @Ignore feature 진행 상황 추적

```bash
# @Ignore 붙은 feature 목록
grep -r "@Ignore" backend/src/test/resources/features/ --include="*.feature" -l

# 전체 feature 목록
find backend/src/test/resources/features/ -name "*.feature" | sort
```

출력 형식:
```
=== Feature 구현 진행 상황 ===

✅ 구현 완료 (Ignore 없음):
  - health-check.feature
  - request_creation.feature

🔒 @Ignore (미구현 — 해당 도메인 API 완료 후 태그 제거):
  - request_inquiry.feature      → buyer/request 조회 API 완료 시
  - proposal_submission.feature  → proposal API 완료 시
  - proposal_inquiry.feature     → proposal API 완료 시
  - reservation_confirmation.feature → reservation API 완료 시
  - full_happy_path.feature      → 전체 도메인 완료 시
```

### Step 3. known-issues.md 자동 업데이트

`.claude/ai-context/known-issues.md`를 읽어라.

아래 항목을 점검하고 업데이트해라:
1. RESOLVED 처리할 수 있는 항목이 있는가? (실제 코드를 확인해서 해결됐으면 RESOLVED로 변경)
2. 새로 발견된 이슈가 있으면 추가 (구현 중 발견한 기술 부채 포함)
3. 심각도가 잘못 분류된 항목이 있으면 수정

### Step 4. api-decisions.md 자동 기록

`.claude/ai-context/api-decisions.md`를 읽어라.

이번 구현에서 발생한 설계 결정이 기록됐는지 확인해라.
누락된 결정이 있으면 `[AD-{N}]` 형식으로 추가해라.

기록 대상 예시:
- ErrorCode 체계 변경 결정
- 응답 구조 변경 (ApiResponse 중첩 객체 방식)
- SecurityConfig Profile 분리 결정
- @Ignore 처리 방식

### Step 5. domain-knowledge.md 업데이트

`.claude/ai-context/domain-knowledge.md`를 읽어라.

이번 구현에서 발견한 도메인 엣지케이스, 비즈니스 규칙 보완 사항이 있으면 추가해라.

### Step 6. 동기화 완료 보고

```
=== /sync-docs 완료 보고 ===

api-spec.md 비교:
  일치: {N}개
  미구현: {N}개
  무단 추가: {N}개

Feature 진행:
  완료: {N}개
  대기(@Ignore): {N}개

known-issues.md:
  RESOLVED 처리: {N}건
  신규 추가: {N}건

api-decisions.md:
  신규 기록: {N}건

domain-knowledge.md:
  업데이트: {있음/없음}
=============================
```