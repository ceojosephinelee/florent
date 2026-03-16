# /fix-process — 개발 프로세스 문제 자동 개선

> 프로세스 문제가 발견될 때마다 실행한다.
> 문제를 문서에 반영하여 재발을 방지한다.

## 실행 순서

CLAUDE.md, backend/CLAUDE.md, .claude/commands/ 아래 모든 커맨드 파일,
skills/backend-dev.md를 읽어라.

읽은 후 아래 항목들이 각 문서에 반영되어 있는지 확인하고,
누락된 항목만 추가해라. 이미 있으면 건너뛴다.

### 확인 항목

1. skills/backend-dev.md — Layer 경계
    - 각 Layer에서 생성 가능한 파일 목록 명시
    - Layer 경계 외 파일 생성 시 사람에게 먼저 보고

2. backend/CLAUDE.md 또는 CLAUDE.md — 테스트 GREEN 기준
    - Docker 실행 중: ./gradlew test 전체 GREEN 필수
    - Docker 미실행: 단위 + 슬라이스 테스트 GREEN으로 /ship 허용

3. .claude/commands/ship.md — 테스트 기준 반영
    - Docker 상태에 따른 GREEN 기준 명시

4. .claude/commands/next.md — Layer 자동 진행
    - Layer 완료 후 다음 Layer 진행 여부 자동 제안

5. .claude/commands/ship.md — DEBT 경고
    - Medium 이상 DEBT 있으면 경고 + 확인 요청

6. CLAUDE.md — 세션 시작 체크리스트
    - git pull, unstaged 파일 확인, Docker 상태 확인 포함

7. .claude/commands/ship.md — 컨텍스트 저장 순서
    - known-issues.md, domain-knowledge.md 업데이트를 Step 1로

완료 후 변경된 파일 목록과 변경 섹션 요약을 보여줘.
생성 완료 후 git add + commit + push까지 해줘.


