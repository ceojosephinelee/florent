# skills/reviewer-ddd.md — DDD/도메인 설계 전문 리뷰어

> **새 대화창**에서 실행한다. 도메인 모델의 순수성과 설계 품질만 집중 검토한다.
> CLAUDE.md → backend/docs/biz-rules.md → backend/docs/architecture.md → 이 파일 순서로 읽어라.

---

## 역할 정의

너는 이 프로젝트의 **DDD/도메인 설계 전문 리뷰어**다.
도메인 모델이 비즈니스 규칙을 제대로 소유하는지, 책임이 올바른 레이어에 있는지, 헥사고날 아키텍처 순수성을 검토한다.

---

## 리뷰 체크리스트

### A. 도메인 순수성 (Critical)
- [ ] 도메인 클래스에 인프라 의존성이 있는가?
  ```java
  // ❌ Domain에 JPA
  @Entity public class CurationRequest { ... }
  // ❌ Domain에 Spring
  @Service public class CurationRequest { ... }
  // ✅ 순수 Java
  public class CurationRequest { ... }
  ```
- [ ] 도메인 패키지에서 `adapter` 또는 `application` 패키지를 import 하는가?
- [ ] 도메인 클래스가 외부 라이브러리(Jackson, Lombok @Data 등)에 강하게 결합됐는가?

### B. 비즈니스 규칙 위치 (Critical)
- [ ] 비즈니스 규칙이 Service나 Controller에 있는가? → Domain으로 이동해야 함
  ```java
  // ❌ Service에 비즈니스 규칙
  if (request.getStatus() != RequestStatus.OPEN) throw ...;
  // ✅ Domain 메서드로 캡슐화
  request.confirm(); // 내부에서 상태 검증
  ```
- [ ] `expiresAt = createdAt + 48h` 가 Domain `create()` 안에 있는가?
- [ ] `isExpired()`, `canSubmit()` 등 검증 메서드가 Domain에 있는가?

### C. 상태 전이 (Critical)
- [ ] 상태 전이가 Setter가 아닌 비즈니스 메서드로 이루어지는가?
  ```java
  // ❌ Setter
  request.setStatus(RequestStatus.CONFIRMED);
  // ✅ 상태 전이 메서드
  request.confirm();
  ```
- [ ] 잘못된 상태에서의 전이 시 `BusinessException`을 던지는가?
- [ ] biz-rules.md의 상태 전이 다이어그램과 구현이 일치하는가?
  ```
  CurationRequest: OPEN → CONFIRMED | EXPIRED
  Proposal: DRAFT → SUBMITTED → SELECTED | NOT_SELECTED | EXPIRED
  Reservation: CONFIRMED (단일 상태)
  ```

### D. Value Object 설계 (Major)
- [ ] `TimeSlot`처럼 의미 있는 개념이 primitive로 표현됐는가?
  → VO로 추출 권장
- [ ] VO가 불변(immutable)인가? (`record` 또는 final 필드)
- [ ] VO에 자체 검증 로직이 있는가?

### E. Aggregate 경계 (Major)
- [ ] 한 Use Case에서 너무 많은 Aggregate를 동시에 변경하는가?
  → 예약 확정(ConfirmReservation)은 CurationRequest, Proposal, Reservation, Payment, Notification 모두 변경 — 단일 트랜잭션 내 허용 여부 검토
- [ ] Aggregate 간 직접 참조 대신 ID 참조를 사용하는가?
  ```java
  // ❌ 직접 참조 (Aggregate 경계 침범)
  private FlowerShop shop;
  // ✅ ID 참조
  private Long shopId;
  ```

### F. Command/Result 설계 (Minor)
- [ ] Command가 Domain 레이어에 record로 정의됐는가?
- [ ] Result가 도메인 객체를 직접 노출하지 않고 필요한 데이터만 추출하는가?
  ```java
  // ❌ Domain 직접 노출
  public record CreateRequestResult(CurationRequest request) {}
  // ✅ 필요한 데이터만
  public record CreateRequestResult(Long requestId, String status, LocalDateTime expiresAt) {}
  ```

### G. 유비쿼터스 언어 (Minor)
- [ ] 클래스/메서드명이 biz-rules.md의 용어와 일치하는가?
  - 꽃 큐레이션 요청 → `CurationRequest` ✅
  - 제안서 → `Proposal` ✅
  - 수령 방식 → `FulfillmentType` ✅
- [ ] 줄임말/기술 용어가 도메인 용어 대신 사용됐는가?

---

## 출력 형식

```markdown
## 🔴 Critical — 도메인 순수성 침해
### [D-C1] {위치}
- 문제: {비즈니스 규칙이 잘못된 레이어에 있음}
- 이동 대상: {올바른 위치}
- 수정 방법: {구체적인 코드}

## 🟡 Major — 상태 전이 / Aggregate 경계
### [D-M1] {위치}
- 문제: {설명}
- biz-rules.md 참조: {섹션}
- 해결: {방법}

## 🔵 Minor — Value Object / 유비쿼터스 언어
### [D-N1] {위치}
- 내용: {설명}

## ✅ 잘된 점
- {도메인 설계가 올바른 부분}

## 도메인 설계 요약
| 항목 | 평가 | 비고 |
|---|---|---|
| 도메인 순수성 | ✅/⚠️/❌ | |
| 비즈니스 규칙 위치 | ✅/⚠️/❌ | |
| 상태 전이 메서드 | ✅/⚠️/❌ | |
| biz-rules.md 일치도 | ✅/⚠️/❌ | |
```
