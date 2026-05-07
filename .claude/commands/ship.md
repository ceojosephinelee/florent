# /ship — 기능 완료 후 커밋 & PR

> 기능 구현 완료 후 실행한다.
> 테스트 → 커밋 → PR 생성까지 처리한다.

---

## 실행 순서

직접 bash로 실행한다. 명령어만 알려주는 게 아니라 네가 실행한다.

---

### Step 1. develop 최신화

```bash
git checkout develop
git pull origin develop
```

---

### Step 2. feat 브랜치 생성

```bash
git checkout -b feat/{기능명}
# 예: git checkout -b feat/proposal-api
```

---

### Step 3. ai-context 업데이트

- `.claude/ai-context/api-decisions.md` — 이번 구현 중 설계 결정 추가
- `.claude/ai-context/known-issues.md` — **수정 금지**
  → known-issues.md는 develop 브랜치에서만 수정한다
  → 이번 브랜치에서 발견한 DEBT/BUG는 메모만 해두고, Step 8 머지 후 develop에서 업데이트

---

### Step 4. 테스트

Docker 상태 확인:

```bash
docker info > /dev/null 2>&1 && echo "DOCKER_RUNNING" || echo "DOCKER_NOT_RUNNING"
```

Docker 실행 중:
```bash
git checkout -b feat/{기능명}
# 예: git checkout -b feat/proposal-api
```

Docker 미실행:
```bash
cd backend && ./gradlew test -x cucumberTest
```

→ FAILED 있으면 수정 후 재실행. 통과 전까지 다음 단계 금지.

---

### Step 5. anti-pattern 검사

```bash
grep -r "@Autowired" backend/src/main --include="*.java"
grep -r "throw new RuntimeException" backend/src/main --include="*.java"
grep -r "import jakarta.persistence" backend/src/main/java/com/florent/*/domain --include="*.java"
```

→ 결과 나오면 수정 후 Step 4부터 재실행.

**known-issues.md 충돌 방지 검사:**

```bash
git diff --cached --name-only | grep "known-issues.md" && echo "BLOCKED: known-issues.md는 feat 브랜치에서 수정 금지. unstage 해라." || echo "PASS"
```

→ BLOCKED 나오면 반드시 되돌린다:
```bash
git restore --staged .claude/ai-context/known-issues.md
git restore .claude/ai-context/known-issues.md
```

**Medium 이상 DEBT 경고:**

```bash
grep -c "심각도.*Medium" .claude/ai-context/known-issues.md | xargs -I{} echo "Medium+ DEBT: {}건"
grep -c "심각도.*High" .claude/ai-context/known-issues.md | xargs -I{} echo "High DEBT: {}건"
```

→ Medium 3건 이상 또는 High 1건 이상이면 사용자에게 경고 후 확인 요청.

---

### Step 6. 커밋 & 푸시

```bash
git add .
git status
git commit -m "{type}({scope}): {기능명}"
git push -u origin feat/{기능명}
```

---

### Step 7. PR 생성

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

## 테스트
- BUILD SUCCESSFUL
{통과한 테스트 목록}
"
```

---

### Step 8. 완료 안내

PR 링크를 지현이에게 보여준다.
머지는 지현이가 직접 GitHub에서 한다.

**머지 방식 주의:**
- feat → develop: **Squash and Merge** 사용
- develop → main: **Create a merge commit** 사용 (Squash 절대 금지 — 무한 충돌 원인)

머지 후 다음 작업 시작 전 반드시:

```bash
git checkout develop
git pull origin develop
```

그 다음 known-issues.md 업데이트:
- 이번 브랜치에서 발견한 DEBT/BUG 추가
- 해결된 것 RESOLVED 처리
- 커밋: `docs: known-issues 업데이트`
