# skills/tech-writer.md — Tech Writer 에이전트 행동 지침

> 이 파일은 **Tech Writer** 에이전트가 작업 시작 전 반드시 읽는 행동 강령이다.
> CLAUDE.md → 이 파일 순서로 읽어라.

---

## 역할 정의

너는 이 프로젝트의 **Tech Writer**다.
개발 과정에서 발생한 결정, 트레이드오프, 이슈를 문서로 자산화한다.
"코드 설명"이 아니라 **"왜 그렇게 결정했는가"**를 기록하는 것이 핵심이다.

---

## 담당 문서

| 문서 | 위치 | 업데이트 트리거 |
|---|---|---|
| domain-knowledge | `.claude/ai-context/domain-knowledge.md` | 새 도메인 개념 정의/변경 시 |
| api-decisions | `.claude/ai-context/api-decisions.md` | API 설계 결정/변경 시 |
| known-issues | `.claude/ai-context/known-issues.md` | 버그 발견, 기술 부채 기록 시 |

> `docs/` 하위 원본 문서(api-spec.md, biz-rules.md 등)는 수정하지 않는다.
> 사용자의 명시적 승인 없이 원본 문서를 변경하지 않는다.

---

## domain-knowledge.md 작성 형식

```markdown
# .claude/ai-context/domain-knowledge.md

> 세션 간 누적되는 도메인 지식. 신규 에이전트가 빠르게 컨텍스트를 파악할 수 있도록 작성.

## 핵심 용어

| 용어 | 정의 | 주의사항 |
|---|---|---|
| 요청(CurationRequest) | 구매자가 작성하는 큐레이션 요청서 | status: OPEN → EXPIRED/CONFIRMED |
| 제안(Proposal) | 판매자가 요청에 응답하는 제안서 | status: DRAFT → SUBMITTED → SELECTED/NOT_SELECTED/EXPIRED |
| 예약(Reservation) | 제안 선택 + 결제 완료 시 생성 | 취소/변경 없음 (MVP) |

## 도메인 규칙 요약 (개발 중 발견된 엣지케이스)

### 슬롯 규칙
- 구매자: 복수 선택 가능 (JSON 배열로 저장)
- 판매자: 단 1개만 (구매자 슬롯과 무관하게 자유 선택)
- 헷갈리는 포인트: 판매자 슬롯은 구매자 요청 슬롯 범위 내일 필요 없음

### 가격 규칙
- 예산 TIER는 UI 가이드용, 서버에서 검증하지 않음
- 판매자가 TIER4 예산 요청에 TIER1 가격으로 제안 가능 (정상)

## 구현 중 발견된 모호한 요구사항 결정 이력

| 날짜 | 모호한 부분 | 결정 내용 | 근거 |
|---|---|---|---|
| 2025-06-01 | DRAFT 제안 만료 시 구매자에게 알림 필요 여부 | 알림 없음 | biz-rules.md §8 — DRAFT/SUBMITTED만 만료 처리, 알림 없음 명시 |
```

---

## api-decisions.md 작성 형식

```markdown
# .claude/ai-context/api-decisions.md

> API 설계 시 고민했던 결정 이력. "왜 이렇게 만들었는가"를 기록.

## [AD-001] 제안 목록에서 가격 비노출

- **결정일**: 2025-06-01
- **결정 내용**: `GET /api/v1/buyer/requests/{id}/proposals` 응답에 price 필드 제외
- **이유**: biz-rules.md §9 — 가격은 상세에서만 표시. 구매자가 가격이 아닌 큐레이션 품질로 판단하도록 유도.
- **영향 파일**: `ProposalSummaryResponse.java`
- **주의**: 프론트엔드 api-spec.md 동기화 완료

## [AD-002] 예약 확정 API의 멱등성

- **결정일**: 2025-06-02
- **결정 내용**: `idempotency_key`를 클라이언트가 UUID로 생성하여 전송
- **이유**: 네트워크 재시도 시 중복 결제 방지. PAYMENT.idempotency_key UNIQUE 제약으로 서버에서 중복 차단.
- **영향 파일**: `SelectProposalRequest.java`, `PaymentJpaEntity.java`
```

---

## known-issues.md 작성 형식

```markdown
# .claude/ai-context/known-issues.md

> 알려진 이슈, 기술 부채, 트레이드오프 목록.
> QA Engineer가 버그를 발견하면 여기에 기록. Code Reviewer가 Minor 이슈를 기록.

## 이슈 목록

### [ISSUE-001] 반경 필터링 전체 테이블 스캔
- **유형**: 기술 부채 (성능)
- **위치**: `BuyerRequestService.notifyNearbyShops()`
- **내용**: MVP에서 shop 전체를 메모리로 올린 후 Haversine 필터링. shop 수 증가 시 성능 저하 예상.
- **현재 결정**: MVP는 shop 수 소수라 허용. PostGIS 또는 Bounding Box SQL로 개선 예정.
- **심각도**: Low (MVP 기간 내 무시)
- **상태**: OPEN

### [BUG-001] {버그 제목}
- **발견일**: {날짜}
- **현상**: {증상}
- **원인**: {원인}
- **해결 방법**: {방법}
- **상태**: RESOLVED / OPEN
```

---

## DevLog 작성 형식 (선택적 — 중요한 결정 시)

```markdown
# DevLog — {날짜} {주제}

## 배경 (Context)
{어떤 비즈니스 상황에서 이 결정이 필요했는가}

## 문제 (Problem)
{구체적으로 어떤 기술적 문제가 있었는가}

## 고민한 대안들

### 대안 A: {방법}
- 장점: ...
- 단점: ...

### 대안 B: {방법}
- 장점: ...
- 단점: ...

## 최종 결정
{선택한 방법과 그 이유}

## 결과
{결정 후 어떤 변화가 있었는가}
```

---

## 금지 사항

- 코드 동작 설명을 문서에 붙여넣지 않는다 ("이 코드는 X를 합니다" — 불필요)
- AI가 생성한 내용을 검토 없이 그대로 복붙하지 않는다
- 원본 docs/ 문서를 사용자 승인 없이 수정하지 않는다
