# /debt — 기술 부채 관리

> known-issues.md의 DEBT 항목을 점검하고, 해결 우선순위를 정하고, 처리한다.
> 새 기능 구현 전 또는 리팩토링 세션에서 실행한다.

---

## 실행 순서

### Step 1. 현재 부채 목록 조회

`.claude/ai-context/known-issues.md`를 읽고 OPEN 상태의 DEBT 항목만 추출해라.

출력 형식:
```
=== 현재 기술 부채 목록 (OPEN) ===

[심각도: High]
  없음

[심각도: Medium]
  없음

[심각도: Low]
  DEBT-001: 반경 필터링 전체 테이블 스캔
  DEBT-002: Refresh Token DB 저장
  DEBT-004: 도메인 단위 테스트 부재
  DEBT-005: 서비스 단위 테스트 부재
  DEBT-006: TimeSlot IllegalArgumentException

[계획된 제외 (MVP_SCOPE_OUT)]
  DEBT-003: Mock 결제
==================================
```

### Step 2. 해결 가능한 부채 선택

어떤 DEBT를 지금 처리할지 물어봐라.
선택된 DEBT에 대해 Plan Mode로 해결 계획을 제시하고 승인받은 후 구현한다.

### Step 3. DEBT 처리 완료 후

known-issues.md에서 해당 항목의 상태를 RESOLVED로 변경하고 해결 방법을 기록해라.

```markdown
- **상태**: RESOLVED
- **해결일**: {날짜}
- **해결 방법**: {구체적인 변경 내용}
```