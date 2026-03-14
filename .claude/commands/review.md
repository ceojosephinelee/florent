# /review — 코드 리뷰 요청

> PR 머지 전 반드시 새 대화창에서 실행한다.
> 구현자 컨텍스트와 완전히 분리된 상태에서 리뷰해야 한다.

---

## 실행 전 준비 (리뷰 요청자가 할 것)

아래 명령어 결과를 복사해서 이 파일 아래에 붙여넣어라:

```bash
# 변경 파일 목록
git diff develop...HEAD --stat

# 전체 diff
git diff develop...HEAD
```

---

## 리뷰어 선택

기본 리뷰어 (항상 실행):
- `skills/code-reviewer.md` — 아키텍처 · 컨벤션 종합 리뷰

전문 리뷰어 (필요 시 추가 — 새 대화창에서 각각 실행):
- `skills/reviewer-performance.md` — 성능 · N+1 전문
- `skills/reviewer-security.md` — 보안 · Auth/JWT 전문
- `skills/reviewer-test.md` — 테스트 커버리지 전문
- `skills/reviewer-ddd.md` — DDD · 도메인 설계 전문

---

## 기본 리뷰어 실행 프롬프트

```
=== Code Reviewer 에이전트 시작 ===
(반드시 새 대화창에서 실행)

아래 파일들을 순서대로 읽어라:
1. CLAUDE.md
2. skills/code-reviewer.md
3. backend/docs/architecture.md
4. backend/docs/conventions.md
5. backend/docs/biz-rules.md

읽기 완료 후 아래 PR diff를 리뷰해라.

[PR Diff]
{git diff 결과 붙여넣기}

[리뷰 포인트]
- 아키텍처 위반 (Critical)
- 컨벤션 위반 (Major)
- 비즈니스 규칙 불일치 (Major)
- N+1 가능성 (Minor)
- 테스트 누락 (Minor)
```

---

## 전문 리뷰어 실행 순서 (선택)

기본 리뷰어 완료 후 Critical 이슈 없으면 필요한 전문 리뷰어를 추가 실행한다.
각각 새 대화창에서 실행. `skills/reviewer-{역할}.md`를 먼저 읽힌 후 동일한 diff를 붙여넣는다.

---

## 리뷰 반영 완료 기준

- 🔴 Critical: 전부 수정 (1개라도 남으면 머지 금지)
- 🟡 Major: 이번 PR 또는 known-issues.md에 DEBT로 기록
- 🔵 Minor: known-issues.md에 기록 후 이월 허용