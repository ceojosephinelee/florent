# /start — 세션 시작

> 새 Claude Code 세션을 열 때마다 실행한다.
> 컨텍스트를 복원하고 작업 준비 상태를 점검한다.

---

## 실행 순서

아래를 순서대로 읽고 실행해라. 읽기 완료 후 각 항목의 결과를 보고해라.

### 1. 컨텍스트 복원 (읽기)
- CLAUDE.md
- backend/CLAUDE.md
- .claude/ai-context/api-decisions.md
- .claude/ai-context/domain-knowledge.md
- .claude/ai-context/known-issues.md

### 2. Git 상태 확인 (bash 실행)
```bash
git branch --show-current
git log --oneline -5
git status
git worktree list
```

### 3. 테스트 상태 확인 (bash 실행)
```bash
cd backend && ./gradlew test --info 2>&1 | tail -20
```

### 4. @Ignore feature 진행 상황 확인 (bash 실행)
```bash
grep -r "@Ignore" backend/src/test/resources/features/ --include="*.feature" -l
```
→ @Ignore가 붙어 있는 feature 목록 출력. 이 목록이 곧 다음 구현 대기 항목이다.

### 5. OPEN 이슈 요약
known-issues.md에서 상태가 OPEN인 항목만 뽑아서 목록으로 보여줘라.

### 6. 세션 시작 보고
아래 형식으로 출력해라:

```
=== 세션 시작 보고 ===
브랜치: {현재 브랜치}
마지막 커밋: {커밋 메시지}
테스트 상태: GREEN / RED ({실패 수}건)
다음 구현 대기 feature:
  - {feature 파일명} — {이유}
OPEN 이슈: {건수}건
  - {DEBT/BUG ID}: {제목}
========================
```