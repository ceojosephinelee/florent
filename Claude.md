# CLAUDE.md — florent (모노레포 루트)

> 이 파일을 가장 먼저 읽어라.
> 이 레포지토리는 **florent** 서비스의 백엔드와 프론트엔드를 단일 레포에서 관리한다.
> 작업 전 반드시 해당 디렉토리의 하위 CLAUDE.md도 함께 읽어라.

---

## 0. 레포지토리 구조

```
florent/                          ← 이 레포 (모노레포 루트)
├── CLAUDE.md                     ← 지금 이 파일. 가장 먼저 읽는다.
├── backend/                      ← Spring Boot 3 백엔드
│   ├── CLAUDE.md                 ← 백엔드 전용 규칙. 백엔드 작업 시 필독.
│   └── docs/                     ← API 명세, ERD, 아키텍처 등 원본 문서
├── frontend/                     ← Flutter 앱 (구매자앱 + 판매자앱)
│   ├── CLAUDE.md                 ← 프론트엔드 전용 규칙. 프론트 작업 시 필독.
│   └── docs/                     ← 화면 목록, 상태관리 설계 등
├── skills/                       ← 멀티에이전트 역할 지침
│   ├── backend-dev.md
│   ├── frontend-dev.md
│   ├── qa-engineer.md
│   ├── code-reviewer.md
│   └── tech-writer.md
└── .claude/
    └── ai-context/               ← 세션 간 누적 컨텍스트
        ├── domain-knowledge.md
        ├── api-decisions.md
        └── known-issues.md
```

**단일 진실 공급원 (Single Source of Truth)**
- API 명세: `backend/docs/api-spec.md` ← 여기서만 수정
- ERD: `backend/docs/erd.md`
- 비즈니스 규칙: `backend/docs/biz-rules.md`
- 화면 목록: `frontend/docs/screen-list.md`
- Flutter는 `backend/docs/api-spec.md`를 복사해 참조 (직접 수정 금지)

---

## 1. 서비스 개요

**Florent** — 나만의 플로리스트 큐레이션 마켓플레이스

```
구매자 요청 생성 (48h 유효)
  → 반경 2km 내 꽃집들에게 FCM 알림
  → 판매자 제안서 작성 · 제출 (24h 유효)
  → 구매자 제안 선택
  → Mock 결제 → 예약 확정
```

**두 개의 앱**
- 구매자 앱: 요청 생성 → 제안 확인 → 결제 → 예약 관리
- 판매자 앱: 요청 수신 → 제안서 작성 · 제출 → 예약 관리

---

## 2. Claude Code 작업 방식 (필수 준수)

### Plan Mode 필수 사용

복잡한 작업은 반드시 Plan Mode로 시작한다.

```
Shift+Tab 두 번 → Plan Mode 활성화 (⏸ plan mode on)
  → Claude가 파일을 읽고 분석만 함 (수정 없음)
  → 구현 계획 제시
  → 사용자 승인
Shift+Tab 한 번 → Normal Mode 복귀 → 실제 구현
```

**Plan Mode를 쓰는 기준**
- 여러 파일을 수정해야 할 때
- 새 기능 개발 시작할 때
- 아키텍처 결정이 필요할 때
- "왜 이렇게 하는가"가 불분명할 때

**Plan Mode를 건너뛰는 기준**
- 단일 파일 수정 1줄 ~ 5줄
- 변경 내용을 한 문장으로 설명 가능할 때

### Todo 리스트 활용

복잡한 작업은 반드시 Todo 리스트를 먼저 생성한다.
컨텍스트를 초기화(`/clear`)해도 Todo는 유지된다.

```
좋은 예:
"구현 전 수정이 필요한 파일 목록을 Todo로 만들어줘.
 코드 작성은 내가 승인한 후에만 시작해."

나쁜 예:
"요청 생성 기능 구현해줘." (계획 없이 바로 시작)
```

### Think 레벨 활용

| 상황 | 키워드 |
|---|---|
| 일반 구현 | (없음) |
| 아키텍처 설계, 레이어 간 의존성 결정 | `think hard` |
| 복잡한 트랜잭션 설계, 동시성 문제 | `think harder` |
| 전체 시스템 설계, 핵심 도메인 설계 | `ultrathink` |

### 세션 관리

```
세션 시작 시:
  → CLAUDE.md 읽기
  → .claude/ai-context/ 3개 파일 읽기
  → 현재 브랜치 · 마지막 커밋 확인
  → known-issues.md에서 OPEN 이슈 확인

작업 완료 시:
  → git commit (작동하는 단위로만)
  → known-issues.md 업데이트
  → 다음 작업 Todo 리스트 작성
```

---

## 3. 멀티에이전트 협업 구조

| 에이전트 | 역할 | skill 파일 |
|---|---|---|
| Backend Dev | Spring Boot 레이어별 구현 | `skills/backend-dev.md` |
| Frontend Dev | Flutter 화면 구현 | `skills/frontend-dev.md` |
| QA Engineer | Cucumber 인수 테스트 | `skills/qa-engineer.md` |
| Code Reviewer | PR 리뷰 (새 컨텍스트에서) | `skills/code-reviewer.md` |
| Tech Writer | 문서화 · DevLog | `skills/tech-writer.md` |

**협업 원칙**
- 각 에이전트는 자신의 skill 파일을 반드시 먼저 읽는다
- Code Reviewer는 **반드시 새 대화창(새 컨텍스트)**에서 실행한다
- 에이전트 간 역할 월권 없음

---

## 4. 브랜치 전략

```
main           ← 배포 가능 상태만
develop        ← 통합 브랜치
feature/backend/{기능명}   ← 백엔드 기능
feature/frontend/{기능명}  ← 프론트엔드 기능
```

Git Worktree 병렬 작업 (선택):
```bash
git worktree add ../florent-backend  feature/backend/request-api
git worktree add ../florent-frontend feature/frontend/buyer-app
```

---

## 5. 작업 시작 전 체크리스트

- [ ] 루트 CLAUDE.md 읽었는가?
- [ ] 해당 디렉토리 CLAUDE.md 읽었는가? (`backend/` 또는 `frontend/`)
- [ ] `.claude/ai-context/` 3개 파일 읽었는가?
- [ ] Plan Mode로 계획을 먼저 작성했는가?
- [ ] Todo 리스트를 생성했는가?
- [ ] 사용자 승인 후 구현을 시작하는가?
