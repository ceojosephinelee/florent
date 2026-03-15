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
- **위치**: `adapter/out/payment/MockPaymentAdapter.java`
- **내용**: PaymentPort 인터페이스로 추상화되어 있어 토스/카카오 PG 어댑터 추가 시 Service 코드 변경 불필요. PR #16에서 PaymentPort outbound port + MockPaymentAdapter 분리 완료.
- **현재 결정**: MVP에서 Mock으로 즉시 성공 처리.
- **심각도**: — (계획된 제한)
- **상태**: MVP_SCOPE_OUT

---

### [DEBT-004] CurationRequest 도메인 단위 테스트 부재

- **유형**: 테스트 부채
- **위치**: `test/domain/request/` (미작성)
- **내용**: `CurationRequest.create()`, `confirm()`, `expire()`, `isExpired()`에 대한 도메인 단위 테스트가 없음. conventions.md §7 — "Domain 변경 시 도메인 단위 테스트 필수".
- **심각도**: Medium
- **상태**: RESOLVED — CurationRequestTest.java 작성 완료

---

### [DEBT-005] BuyerRequestService 서비스 단위 테스트 부재

- **유형**: 테스트 부채
- **위치**: `test/application/buyer/BuyerRequestServiceTest.java` (미작성)
- **내용**: Fake 구현체(`FakeCurationRequestRepository`, `FakeFlowerShopRepository`, `FakeSaveNotificationPort`)는 작성되었으나, 이를 활용한 Service 단위 테스트 클래스가 없음.
- **심각도**: Medium
- **상태**: RESOLVED — BuyerRequestServiceTest.java 작성 완료

---

### [DEBT-006] TimeSlot Value Object에 IllegalArgumentException 사용

- **유형**: 컨벤션 검토
- **위치**: `domain/request/TimeSlot.java:8-10`
- **내용**: `IllegalArgumentException`을 직접 throw. conventions.md §5에서 RuntimeException 직접 사용 금지, `BusinessException(ErrorCode.XXX)` 사용 요구. Value Object 생성자 검증에 한해 Java 관례상 허용 가능하나 프로젝트 컨벤션 일관성 검토 필요.
- **심각도**: Low
- **상태**: OPEN

---

### [DEBT-007] ErrorCode에 HttpStatus 의존 (도메인 순수성)

- **유형**: 기술 부채 (아키텍처)
- **위치**: `common/exception/ErrorCode.java`
- **내용**: ErrorCode enum이 `org.springframework.http.HttpStatus`를 직접 참조. 도메인 레이어에서 사용 시 Spring 의존이 전이됨. int statusCode + 매핑 테이블 방식으로 분리 검토 필요.
- **심각도**: Low
- **상태**: RESOLVED — ErrorCode에서 HttpStatus 제거, int httpStatus로 변경 완료

---

### [DEBT-008] LocalDateTime.now() 직접 호출 (Clock 도입 검토)

- **유형**: 기술 부채 (테스트 용이성)
- **위치**: `domain/request/CurationRequest.create()`, `CurationRequest.isExpired()`
- **내용**: `LocalDateTime.now()`를 직접 호출하여 테스트에서 시간 제어 불가. `Clock` 주입 또는 `TimeProvider` 인터페이스 도입 검토.
- **심각도**: Low
- **상태**: RESOLVED — Clock 파라미터 주입 방식으로 도메인 순수성 유지하며 해결. ClockConfig 빈 등록.

---

### [DEBT-009] prod JWT 필터 미구현

- **유형**: 기능 미완성
- **위치**: `common/config/SecurityConfig.java`
- **내용**: 현재 DevAuthFilter(local 프로파일)만 존재. prod 환경 JWT 검증 필터 미구현. auth 도메인 구현 시 함께 처리.
- **심각도**: High
- **상태**: RESOLVED — JwtProvider + JwtAuthenticationFilter(@Profile("prod")) 구현 완료. jjwt 0.12.6 사용.

---

### [DEBT-010] H2 → Testcontainers PostgreSQL 전환

- **유형**: 기술 부채 (테스트 환경)
- **위치**: `src/test/resources/application-test.yml`
- **내용**: 인수 테스트가 H2 인메모리 DB 사용 중. PostgreSQL 고유 기능(JSON 연산, 인덱스 동작) 테스트 불가. Testcontainers PostgreSQL로 전환 필요.
- **심각도**: Medium
- **상태**: RESOLVED — Testcontainers PostgreSQL 15-alpine으로 전환, H2 의존성 제거, DatabaseCleaner PostgreSQL 호환 완료

---

### [DEBT-011] 배치 알림 발송 (saveRequestArrivedBatch)

- **유형**: 기술 부채 (성능)
- **위치**: `application/buyer/BuyerRequestService.notifyNearbyShops()`
- **내용**: 반경 내 꽃집에 1건씩 개별 알림 저장. 꽃집 수 증가 시 N번 DB 호출. `saveRequestArrivedBatch(List<Long> sellerIds, Long requestId)` 배치 메서드 도입 검토.
- **심각도**: Low
- **상태**: OPEN

---

### [DEBT-012] RequestPage Domain에 페이징 인프라 개념 누출

- **유형**: 기술 부채 (아키텍처)
- **위치**: `domain/request/RequestPage.java`
- **내용**: `totalElements`, `totalPages`, `last` 등 Spring Data Page 개념이 Domain record에 그대로 노출. Domain은 인프라 비의존이어야 하므로 Cursor 기반 또는 별도 추상화 검토.
- **심각도**: Low
- **상태**: OPEN

---

### [DEBT-013] ProposalJpaEntity toDomain()/from() 미구현

- **유형**: 기술 부채 (구현 미완)
- **위치**: `adapter/out/persistence/proposal/ProposalJpaEntity.java`
- **내용**: Proposal 도메인 엔티티가 아직 없어 `toDomain()` / `from()` 변환 메서드 미작성. Proposal 도메인 구현 시 함께 작성.
- **심각도**: Low
- **상태**: RESOLVED — ProposalJpaEntity에 toDomain()/from() 구현 완료

---

### [DEBT-014] DRAFT description NOT NULL 검토

- **유형**: 기술 부채 (스키마)
- **위치**: `V3__create_proposal.sql` — `description TEXT NOT NULL`
- **내용**: DRAFT 상태에서는 description이 비어 있을 수 있으나 NOT NULL 제약으로 빈 문자열 저장 필요. Proposal 구현 시 NULL 허용 여부 재검토.
- **심각도**: Low
- **상태**: OPEN

---

### [DEBT-015] getCurrentBuyerToken() ScenarioContext 통합

- **유형**: 기술 부채 (테스트)
- **위치**: `RequestInquirySteps.getCurrentBuyerToken()`
- **내용**: RequestSteps에서 설정한 buyerToken을 RequestInquirySteps에서 DB 직접 조회로 재생성. ScenarioContext에 buyerToken을 저장하고 공유하도록 통합 필요.
- **심각도**: Low
- **상태**: OPEN

---

### [DEBT-016] Testcontainers 전환 (H2 → PostgreSQL)

- **유형**: 기술 부채 (테스트 환경)
- **위치**: `src/test/resources/application-test.yml`
- **내용**: DEBT-010과 동일 맥락. H2에서 PostgreSQL 고유 문법(INTERVAL, DESC INDEX 등) 호환 문제 발생 가능. Testcontainers 전환 시 함께 해결.
- **심각도**: Medium
- **상태**: RESOLVED — DEBT-010과 함께 해결. Testcontainers PostgreSQL 전환 완료.

---

### [DEBT-023] updateDraft() VO 도입 검토

- **유형**: 기술 부채 (설계)
- **위치**: `domain/proposal/Proposal.updateDraft()`
- **내용**: updateDraft()의 파라미터가 11개 원시 타입. `ProposalDraftContent` Value Object로 묶으면 가독성·유지보수성 향상.
- **심각도**: Low
- **상태**: OPEN

---

### [DEBT-024] imageUrls 검증 부재

- **유형**: 기술 부채 (검증)
- **위치**: `domain/proposal/Proposal.updateDraft()`, `validateForSubmission()`
- **내용**: imageUrls에 대해 URL 형식 검증, 최대 개수 제한 등이 없음. 악의적 입력 방어를 위해 도메인 검증 추가 검토.
- **심각도**: Low
- **상태**: OPEN

---

### [DEBT-025] validateForSubmission() 필드별 에러 메시지

- **유형**: 기술 부채 (UX)
- **위치**: `domain/proposal/Proposal.validateForSubmission()`
- **내용**: 모든 필수 필드 누락을 동일한 `VALIDATION_ERROR`로 반환. 프론트엔드에서 어떤 필드가 누락인지 알 수 없음. 필드별 에러 코드 또는 메시지 도입 검토.
- **심각도**: Low
- **상태**: OPEN

---

### [DEBT-026] SellerProposalController @WebMvcTest 미작성

- **유형**: 테스트 부채
- **위치**: `test/adapter/in/seller/` (미작성)
- **내용**: SellerProposalController에 대한 `@WebMvcTest` 슬라이스 테스트가 없음. 요청/응답 직렬화, Validation, HTTP 상태 코드를 검증하는 Controller 단위 테스트 필요.
- **심각도**: Medium
- **상태**: RESOLVED — SellerProposalControllerTest 작성 완료 (제안 상세 조회 200/404/403 케이스, PR #15)

---

### [DEBT-027] OutboxWorker 미구현 (@Scheduled FCM 발송)

- **유형**: 기능 미완성
- **위치**: `application/notification/` (미작성)
- **내용**: OutboxEvent를 주기적으로 폴링하여 FCM 푸시를 발송하는 `OutboxWorker` 스케줄러가 미구현. 현재 OutboxEvent는 PENDING 상태로만 저장됨. PushNotificationPort + UserDeviceRepository를 사용하여 구현 필요.
- **심각도**: Medium
- **상태**: OPEN

---

### [DEBT-028] NotificationController @WebMvcTest 미작성

- **유형**: 테스트 부채
- **위치**: `test/adapter/in/notification/` (미작성)
- **내용**: NotificationController, DeviceController에 대한 `@WebMvcTest` 슬라이스 테스트가 없음. 요청/응답 직렬화, Validation, HTTP 상태 코드를 검증하는 Controller 단위 테스트 필요.
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

### [DEBT-017] SlotKind enum 미사용 — availableSlotKind가 String

- **유형**: 기술 부채 (타입 안전성)
- **위치**: `domain/proposal/Proposal.java` — `availableSlotKind`, `availableSlotValue`
- **내용**: Request 도메인은 `SlotKind` enum + `TimeSlot` Value Object를 사용하지만, Proposal 도메인은 String으로 저장. enum 기반 `ProposalSlot` Value Object 도입 검토.
- **심각도**: Low
- **상태**: OPEN

---

### [DEBT-018] ProposalDetail이 Shop 데이터 직접 포함 — Read Model 분리 검토

- **유형**: 기술 부채 (아키텍처)
- **위치**: `domain/proposal/ProposalDetail.java`
- **내용**: ProposalDetail record가 Shop 필드(shopId, shopName, shopPhone, shopAddressText)를 직접 포함. 조회 전용 Read Model(CQRS)로 분리하면 Service에서 Shop 조합 로직 제거 가능.
- **심각도**: Low
- **상태**: OPEN

---

### [DEBT-019] Proposal status DB 필터링 — 현재 메모리 필터링

- **유형**: 기술 부채 (성능)
- **위치**: `BuyerProposalService.getProposalsByRequestId()`
- **내용**: `findByRequestId()`로 전체 제안을 조회한 뒤 Java에서 `isVisibleToBuyer()` 필터링. 제안 수 증가 시 `findByRequestIdAndStatusIn(requestId, visibleStatuses)` WHERE 절 DB 필터링으로 전환 필요.
- **심각도**: Low
- **상태**: OPEN

---

### [DEBT-020] @WebMvcTest addFilters=false → JWT 통합 슬라이스 테스트 필요

- **유형**: 테스트 부채
- **위치**: Controller 테스트 전반 (`@WebMvcTest(addFilters = false)`)
- **내용**: 현재 Controller 단위 테스트에서 `addFilters=false`로 JWT 필터를 비활성화. JWT 필터가 올바르게 동작하는지 확인하는 통합 슬라이스 테스트가 별도로 필요.
- **심각도**: Medium
- **상태**: OPEN

---

### [DEBT-021] @WithMockSeller 미구현

- **유형**: 테스트 부채
- **위치**: `support/` 패키지
- **내용**: `@WithMockBuyer` 커스텀 어노테이션은 구현되어 있으나, 판매자 역할(`@WithMockSeller`)에 대응하는 어노테이션이 미구현. 판매자 Controller 테스트에서 필요.
- **심각도**: Low
- **상태**: RESOLVED — WithMockSeller + WithMockSellerSecurityContextFactory 구현 완료 (PR #15)

---

### [DEBT-022] refreshToken 검증/재발급 로직 미구현

- **유형**: 기능 미완성
- **위치**: auth 도메인
- **내용**: `JwtProvider.generateRefreshToken()`은 구현되었으나, refreshToken을 DB에서 조회·검증하고 새 accessToken을 재발급하는 서비스 로직(`/api/v1/auth/reissue` 엔드포인트)이 미구현. auth 도메인 구현 시 함께 처리.
- **심각도**: Medium
- **상태**: OPEN

---

## 정합성 검증 이력

### [2026-03-12] 정합성 검증 — 백엔드 구현 전
- Critical: 10건 (모두 RESOLVED — api-spec.md, erd.md 수정 완료)
- Warning: 13건 (OPEN)
- 주요 내용: API 엔드포인트 3개 누락(buyer/me, seller/me, seller/stats), 예약 상세 Response에 요청 원문·이미지 필드 누락, NOTIFICATION 테이블 body 컬럼 누락. Critical 항목은 api-spec.md 및 erd.md에 반영 완료. Warning 항목(Mock↔API 구조 불일치, HTTP Method 불일치, DRAFT 제안 CONFIRMED 후 처리 미명시 등)은 백엔드 구현 중 순차 해결 필요.

---

> 이슈 해결 시 상태를 RESOLVED로 변경하고 해결 방법을 기록한다.
