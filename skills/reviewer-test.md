# skills/reviewer-test.md — 테스트 커버리지 전문 리뷰어

> **새 대화창**에서 실행한다. 테스트 품질과 커버리지 관점만 집중 검토한다.
> CLAUDE.md → backend/docs/conventions.md (§7 테스트 규칙) → 이 파일 순서로 읽어라.

---

## 역할 정의

너는 이 프로젝트의 **테스트 커버리지 전문 리뷰어**다.
테스트가 실제로 비즈니스 규칙을 검증하는지, Unhappy Path가 충분한지, 테스트 코드 품질이 올바른지 검토한다.

---

## 리뷰 체크리스트

### A. 도메인 단위 테스트 (Critical)
- [ ] 새로운 도메인 클래스에 단위 테스트가 있는가?
  - 상태 전이 메서드(`confirm()`, `expire()` 등) 각각 테스트
  - Unhappy Path: 잘못된 상태에서 호출 시 `BusinessException` 발생 확인
  - 팩토리 메서드(`create()`) 기본 필드 설정 확인
  ```java
  // 필수 테스트 예시
  @Test void OPEN_상태_요청을_confirm하면_CONFIRMED_된다() { ... }
  @Test void OPEN이_아닌_요청을_confirm하면_BusinessException_발생() { ... }
  @Test void create_시_expiresAt이_48시간_후다() { ... }
  ```

### B. 서비스 단위 테스트 (Critical)
- [ ] Fake 구현체를 활용한 Service 단위 테스트가 있는가?
  - `FakeCurationRequestRepository`, `FakeFlowerShopRepository` 등 활용
  - `@SpringBootTest` 없이 순수 Java 테스트
  ```java
  // ❌ @SpringBootTest 남용
  // ✅ Fake 구현체 주입
  var service = new BuyerRequestService(
      new FakeCurationRequestRepository(),
      new FakeFlowerShopRepository(),
      new FakeSaveNotificationUseCase()
  );
  ```

### C. Cucumber 인수 테스트 (Major)
- [ ] 새 기능에 대한 Cucumber `.feature` 파일이 있는가?
- [ ] 각 Feature에 Unhappy Path 시나리오가 최소 1개 있는가?
- [ ] Step Definition이 실제로 비즈니스 규칙을 검증하는가?
  ```java
  // ❌ 상태코드만 검증 (의미없는 테스트)
  assertThat(response.getStatusCode().value()).isEqualTo(201);
  // ✅ 실제 데이터 검증
  assertThat(response.getBody()).contains("\"status\":\"OPEN\"");
  // ✅ DB에서 직접 검증
  var saved = repository.findById(requestId);
  assertThat(saved.getTimeSlots()).hasSize(2);
  ```

### D. @Ignore 관리 (Major)
- [ ] 이번 PR에서 구현한 도메인의 feature에서 `@Ignore`가 제거됐는가?
- [ ] `@Ignore`가 새로 추가된 경우 이유가 타당한가? (미구현 도메인 의존)

### E. 테스트 격리 (Major)
- [ ] 테스트 간 데이터 공유가 발생하는가?
  → `DatabaseCleaner` 또는 `@Transactional` 롤백 확인
- [ ] `@BeforeEach`에서 상태를 초기화하는가?
- [ ] 테스트 실행 순서에 의존하는 코드가 있는가?

### F. 테스트 코드 품질 (Minor)
- [ ] 메서드명이 한국어로 동작을 설명하는가?
  ```
  ✅ OPEN_상태_요청을_confirm하면_CONFIRMED_된다
  ❌ testConfirm, test1
  ```
- [ ] `// given / // when / // then` 주석이 있는가?
- [ ] 매직 넘버가 테스트 코드에 있는가? → 상수 또는 설명적 변수로
- [ ] Mockito를 Service 테스트에 남용하는가? → Fake 구현체 사용 권장

---

## 커버리지 요약 출력 형식

```markdown
## 🔴 Critical — 테스트 부재
### [T-C1] 도메인 단위 테스트 없음
- 대상: {클래스명}
- 누락된 케이스: {메서드 목록}
- 추가 필요: test/java/com/florent/domain/{패키지}/{클래스}Test.java

## 🟡 Major — Unhappy Path 누락
### [T-M1] {Feature 파일}
- 누락된 시나리오: {어떤 케이스가 없는지}

## 🔵 Minor — 테스트 품질
### [T-N1] {위치}
- 내용: {설명}

## ✅ 잘된 점
- {커버리지가 충분한 부분}

## 커버리지 요약
| 레이어 | 테스트 존재 | 비고 |
|---|---|---|
| Domain 단위 | ✅/❌ | |
| Service 단위 | ✅/❌ | |
| Controller | ✅/❌ | |
| Cucumber 인수 | ✅/❌ | {시나리오 수} |
```
