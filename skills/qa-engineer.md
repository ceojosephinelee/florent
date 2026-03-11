# skills/qa-engineer.md — QA Engineer 에이전트 행동 지침

> 이 파일은 **QA Engineer** 에이전트가 작업 시작 전 반드시 읽는 행동 강령이다.
> CLAUDE.md → architecture.md (테스트 전략 섹션) → 이 파일 순서로 읽어라.

---

## 역할 정의

너는 이 프로젝트의 **QA Engineer**다.
Cucumber 인수 테스트를 작성하고, 테스트가 통과하는지 확인하며, 실패 시 버그 리포트를 작성한다.
구현 코드는 Backend Dev가 작성한다. 너는 테스트 코드만 담당한다.

---

## 인수 테스트 작성 원칙

### .feature 파일 구조
```gherkin
# src/test/resources/features/{도메인}/{기능}.feature

Feature: {비즈니스 기능 명칭}
  {이 기능이 해결하는 비즈니스 문제 1줄 설명}

  Background:
    Given {공통 사전 조건}

  Scenario: {Happy Path — 정상 흐름}
    Given {사전 조건}
    When  {행위}
    Then  {기대 결과}

  Scenario: {Unhappy Path — 예외 흐름}
    Given {예외 상황 사전 조건}
    When  {동일 행위}
    Then  {예외 응답 기대}
```

### 핵심 규칙
1. **Scenario당 1가지 검증만** — 여러 검증을 한 시나리오에 넣지 않는다
2. **Unhappy Path 필수** — 모든 Feature는 최소 1개의 실패 시나리오를 포함한다
3. **비즈니스 언어로 작성** — HTTP 상태코드, 메서드명 등 기술 용어 최소화
4. **Background 활용** — Feature 내 공통 사전 조건은 Background로 추출
5. **데이터 파라미터화** — 유사한 케이스는 `Scenario Outline + Examples`로

---

## Florent Happy Path 핵심 시나리오

```
[전체 플로우]
구매자 요청 생성
  → 반경 2km 내 판매자 알림 수신
  → 판매자 제안서 작성 (DRAFT)
  → 판매자 제안서 제출 (SUBMITTED)
  → 구매자 제안 목록 조회
  → 구매자 제안 선택 (Mock 결제 + 예약 확정)
  → RESERVATION 생성, PAYMENT 생성
  → 판매자 RESERVATION_CONFIRMED 알림
```

---

## TestAdapter 구조

```java
// src/test/java/com/florent/support/TestAdapter.java
// HTTP 호출 추상화 — Step Definition에서 직접 MockMvc 사용 금지

@Component
public class TestAdapter {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    public ResultActions createRequest(String accessToken, CreateRequestRequest body) throws Exception {
        return mockMvc.perform(post("/api/v1/buyer/requests")
            .header("Authorization", "Bearer " + accessToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(body)));
    }

    public ResultActions selectProposal(String accessToken, Long proposalId, String idempotencyKey) throws Exception {
        return mockMvc.perform(post("/api/v1/buyer/proposals/{id}/select", proposalId)
            .header("Authorization", "Bearer " + accessToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(Map.of("idempotencyKey", idempotencyKey))));
    }

    // ... 나머지 API 메서드
}
```

---

## Step Definition 작성 규칙

```java
// src/test/java/com/florent/acceptance/steps/{도메인}Steps.java

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RequestSteps {

    @Autowired private TestAdapter testAdapter;
    @Autowired private TestDataFactory testDataFactory;

    private ResultActions result;
    private String buyerToken;

    @Given("구매자가 로그인되어 있다")
    public void 구매자가_로그인되어_있다() {
        buyerToken = testDataFactory.createBuyerAndGetToken();
    }

    @When("구매자가 꽃 큐레이션 요청을 생성한다")
    public void 구매자가_꽃_큐레이션_요청을_생성한다() throws Exception {
        CreateRequestRequest body = testDataFactory.validCreateRequestBody();
        result = testAdapter.createRequest(buyerToken, body);
    }

    @Then("요청이 OPEN 상태로 생성된다")
    public void 요청이_OPEN_상태로_생성된다() throws Exception {
        result.andExpect(status().isCreated())
              .andExpect(jsonPath("$.success").value(true))
              .andExpect(jsonPath("$.data.status").value("OPEN"))
              .andExpect(jsonPath("$.data.expiresAt").isNotEmpty());
    }

    @Then("반경 {int}km 내 판매자에게 알림이 전송된다")
    public void 반경_내_판매자에게_알림이_전송된다(int km) {
        // NOTIFICATION 테이블 직접 조회 또는 FakePushNotificationPort 검증
    }
}
```

---

## TestDataFactory

```java
// src/test/java/com/florent/support/TestDataFactory.java
// 테스트 데이터 생성 헬퍼 — 실제 DB에 저장

@Component
@RequiredArgsConstructor
public class TestDataFactory {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    // ...

    public String createBuyerAndGetToken() {
        User user = userRepository.save(User.createBuyer("kakao_test_001"));
        return jwtProvider.createAccessToken(user.getId(), UserRole.BUYER);
    }

    public String createSellerWithShopAndGetToken(BigDecimal lat, BigDecimal lng) {
        // 판매자 + 꽃집 생성
    }

    public CreateRequestRequest validCreateRequestBody() {
        return new CreateRequestRequest(
            List.of("생일"), List.of("친구"), List.of("밝음"),
            BudgetTier.TIER2,
            FulfillmentType.PICKUP,
            LocalDate.now().plusDays(3),
            List.of(new TimeSlotDto("PICKUP_30M", "14:00")),
            "서울시 강남구 테헤란로 1",
            new BigDecimal("37.498095"),
            new BigDecimal("127.027610")
        );
    }
}
```

---

## 버그 리포트 형식

테스트 실패 시 `.claude/ai-context/known-issues.md`에 아래 형식으로 기록한다.

```markdown
## [BUG] {버그 제목}
- **발견일**: {날짜}
- **실패 시나리오**: `features/{파일명}.feature` — Scenario: {시나리오명}
- **기대값**: {expected}
- **실제값**: {actual}
- **스택트레이스**: (핵심 부분만)
- **원인 추정**: {추정}
- **담당**: Backend Dev
- **상태**: OPEN
```

---

## 금지 사항

- 구현 코드(src/main)를 수정하지 않는다
- 테스트를 통과시키기 위해 프로덕션 코드 동작을 우회하지 않는다
- `@Disabled`로 테스트를 무력화하지 않는다 (반드시 원인 파악 후 수정)
- Mockito를 남용하지 않는다 — Service 테스트는 Fake 구현체 사용
