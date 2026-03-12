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

### [DEBT-004] CurationRequest 도메인 단위 테스트 부재

- **유형**: 테스트 부채
- **위치**: `test/domain/request/` (미작성)
- **내용**: `CurationRequest.create()`, `confirm()`, `expire()`, `isExpired()`에 대한 도메인 단위 테스트가 없음. conventions.md §7 — "Domain 변경 시 도메인 단위 테스트 필수".
- **심각도**: Medium
- **상태**: OPEN

---

### [DEBT-005] BuyerRequestService 서비스 단위 테스트 부재

- **유형**: 테스트 부채
- **위치**: `test/application/buyer/BuyerRequestServiceTest.java` (미작성)
- **내용**: Fake 구현체(`FakeCurationRequestRepository`, `FakeFlowerShopRepository`, `FakeSaveNotificationUseCase`)는 작성되었으나, 이를 활용한 Service 단위 테스트 클래스가 없음.
- **심각도**: Medium
- **상태**: OPEN

---

### [DEBT-006] TimeSlot Value Object에 IllegalArgumentException 사용

- **유형**: 컨벤션 검토
- **위치**: `domain/request/TimeSlot.java:8-10`
- **내용**: `IllegalArgumentException`을 직접 throw. conventions.md §5에서 RuntimeException 직접 사용 금지, `BusinessException(ErrorCode.XXX)` 사용 요구. Value Object 생성자 검증에 한해 Java 관례상 허용 가능하나 프로젝트 컨벤션 일관성 검토 필요.
- **심각도**: Low
- **상태**: OPEN

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

## 정합성 검증 이력

### [2026-03-12] 정합성 검증 — 백엔드 구현 전
- Critical: 10건 (모두 RESOLVED — api-spec.md, erd.md 수정 완료)
- Warning: 13건 (OPEN)
- 주요 내용: API 엔드포인트 3개 누락(buyer/me, seller/me, seller/stats), 예약 상세 Response에 요청 원문·이미지 필드 누락, NOTIFICATION 테이블 body 컬럼 누락. Critical 항목은 api-spec.md 및 erd.md에 반영 완료. Warning 항목(Mock↔API 구조 불일치, HTTP Method 불일치, DRAFT 제안 CONFIRMED 후 처리 미명시 등)은 백엔드 구현 중 순차 해결 필요.

---

> 이슈 해결 시 상태를 RESOLVED로 변경하고 해결 방법을 기록한다.
