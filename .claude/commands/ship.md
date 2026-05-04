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

- `.claude/ai-context/known-issues.md` — 새 DEBT/BUG 추가, 해결된 것 RESOLVED 처리
- `.claude/ai-context/api-decisions.md` — 이번 구현 중 설계 결정 추가

---

### Step 4. 테스트

Docker 상태 확인:

```bash
docker info > /dev/null 2>&1 && echo "DOCKER_RUNNING" || echo "DOCKER_NOT_RUNNING"
```

Docker 실행 중:
```bash
cd backend && ./gradlew test
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
머지 후 다음 작업 시작 전 반드시:

```bash
git checkout develop
git pull origin develop
```