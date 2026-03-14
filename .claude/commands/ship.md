# /ship — 기능 완료 후 커밋 & PR

> Layer 6(Cucumber)까지 구현 완료 후 실행한다.
> 테스트 → worktree → 커밋 → PR → 정리까지 자동 처리한다.

---

## 실행 순서

직접 bash로 실행한다. 명령어만 알려주는 게 아니라 네가 실행한다.

### [주의 — 반드시 지킬 것]
- `cp` 명령어 절대 사용 금지 (파일 복사 하지 않는다)
- worktree 안에 새 폴더를 만들거나 기존 폴더를 중첩하지 않는다
- worktree는 레포 전체 구조가 그대로 들어가므로 추가 복사 없이 `git add`만 한다

### Step 1. 최종 테스트
```bash
cd backend && ./gradlew test
```
→ BUILD SUCCESSFUL 확인. FAILED 있으면 수정 후 재실행. 통과 전까지 다음 단계 금지.

### Step 2. 변경 파일 확인
```bash
git status
git diff --stat HEAD
```

### Step 3. 브랜치 + worktree 생성
```bash
# $BRANCH_NAME 은 아래 규칙으로 결정:
# 백엔드 기능: feature/backend/{기능명}
# 예: feature/backend/proposal-api

git checkout develop
git checkout -b $BRANCH_NAME
git push -u origin $BRANCH_NAME
git worktree add ../$WORKTREE_NAME $BRANCH_NAME
```

### Step 4. worktree 이동 후 구조 확인
```bash
cd ../$WORKTREE_NAME
ls          # 레포 루트 구조가 그대로 있어야 함 (backend/, frontend/ 등)
git status  # 변경 파일 목록 확인
```

### Step 5. 커밋 & 푸시
```bash
git add backend/
git commit -m "{type}({scope}): {기능명}"
git push origin $BRANCH_NAME
```

### Step 6. PR 생성
```bash
gh pr create --base develop \
  --title "{type}({scope}): {기능명}" \
  --body "
## 구현 내용
{구현한 파일/기능 목록}

## 아키텍처 체크
- [ ] Domain에 JPA 어노테이션 없음
- [ ] Service가 Port 인터페이스만 의존
- [ ] Controller가 UseCase 인터페이스만 의존
- [ ] feature 파일 에러코드와 ErrorCode enum 일치

## 테스트
{통과한 feature 목록 및 시나리오 수}
- BUILD SUCCESSFUL
"
```

### Step 7. worktree 정리
```bash
cd ../florent
git worktree remove ../$WORKTREE_NAME
```

### Step 8. ai-context 업데이트
- `.claude/ai-context/api-decisions.md` — 이번 구현 중 발생한 설계 결정 추가
- `.claude/ai-context/known-issues.md` — 새 DEBT/BUG 있으면 추가, 해결된 것은 RESOLVED 처리
- `.claude/ai-context/domain-knowledge.md` — 도메인 관련 발견 사항 추가