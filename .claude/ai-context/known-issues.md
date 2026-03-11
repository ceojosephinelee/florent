# .claude/ai-context/known-issues.md

> 알려진 이슈, 기술 부채, 트레이드오프 목록.
> QA Engineer 버그 리포트, Code Reviewer Minor 이슈, Backend Dev 발견 부채를 여기에 기록.

---

## 기술 부채 목록

### [DEBT-001] 반경 필터링 전체 테이블 스캔

- **유형**: 기술 부채 (성능)
- **위치**: `BuyerRequestService.notifyNearbyShops()`
- **내용**: MVP에서 FLOWER_SHOP 전체를 메모리로 올린 후 Haversine 필터링. shop 수 증가 시 성능 저하 예상.
- **현재 결정**: shop 수 소수 가정 하에 허용. 실서비스 전 PostGIS 또는 Bounding Box SQL 조건으로 개선 필요.
- **심각도**: Low (MVP 기간 내 무시)
- **상태**: OPEN

---

### [DEBT-002] Refresh Token DB 저장 — Redis 미사용

- **유형**: 기술 부채 (확장성)
- **위치**: `USER.refresh_token` 컬럼
- **내용**: Redis 없이 DB 컬럼으로 Refresh Token 관리. 서버 부하 증가 시 매 요청마다 DB 조회.
- **현재 결정**: MVP에서 Redis 미포함. 사용자 수 증가 시 Redis 도입 예정.
- **심각도**: Low (MVP 기간 내 무시)
- **상태**: OPEN

---

### [DEBT-003] Mock 결제 — 실 PG 미연동

- **유형**: 기능 제한 (MVP 의도적 제외)
- **위치**: `MockPaymentAdapter.java`
- **내용**: PaymentPort 인터페이스로 추상화되어 있어 토스/카카오 PG 어댑터 추가 시 Service 코드 변경 불필요.
- **현재 결정**: MVP에서 Mock으로 즉시 성공 처리.
- **심각도**: — (계획된 제한)
- **상태**: MVP_SCOPE_OUT

---

## 버그 목록

> 버그 발견 시 아래 형식으로 추가

```
### [BUG-{N}] {버그 제목}
- **발견일**: {날짜}
- **발견자**: QA Engineer (Cucumber) / Code Reviewer / Backend Dev
- **실패 시나리오**: `features/{파일}.feature` — Scenario: {시나리오명}
- **기대값**: {expected}
- **실제값**: {actual}
- **원인**: {원인}
- **해결 방법**: {방법}
- **상태**: OPEN / RESOLVED
```

---

> 이슈 해결 시 상태를 RESOLVED로 변경하고 해결 방법을 기록한다.
