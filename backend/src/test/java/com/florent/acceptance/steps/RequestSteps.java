package com.florent.acceptance.steps;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.florent.fake.FakeSaveNotificationUseCase;
import com.florent.support.ScenarioContext;
import com.florent.support.TestAdapter;
import com.florent.support.TestDataFactory;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class RequestSteps {

    @Autowired
    private TestAdapter testAdapter;

    @Autowired
    private TestDataFactory testDataFactory;

    @Autowired
    private FakeSaveNotificationUseCase fakeNotification;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ScenarioContext scenarioContext;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private String buyerToken;
    private String sellerToken;
    private final Map<String, Long> shopSellerIds = new HashMap<>();

    // ─── Background ───

    @Given("구매자 {string} 이 로그인되어 있다")
    public void 구매자가_로그인되어_있다(String name) {
        // E2E 시나리오에서 이미 생성된 구매자 토큰이 있으면 재사용
        String existingToken = scenarioContext.getBuyerToken(name);
        if (existingToken != null) {
            buyerToken = existingToken;
        } else {
            buyerToken = testDataFactory.createBuyerAndGetToken(name);
        }
        scenarioContext.setBuyerToken(buyerToken);
        scenarioContext.putBuyerToken(name, buyerToken);
    }

    @Given("반경 2km 이내에 꽃집 {string} 이 위치해 있다 \\(위도 {double}, 경도 {double})")
    public void 반경_이내에_꽃집이_위치해_있다(String shopName, double lat, double lng) {
        Long sellerId = testDataFactory.createSellerWithShop(
                shopName, new java.math.BigDecimal(String.valueOf(lat)),
                new java.math.BigDecimal(String.valueOf(lng)));
        shopSellerIds.put(shopName, sellerId);
    }

    @Given("반경 2km 밖에 꽃집 {string} 이 위치해 있다 \\(위도 {double}, 경도 {double})")
    public void 반경_밖에_꽃집이_위치해_있다(String shopName, double lat, double lng) {
        Long sellerId = testDataFactory.createSellerWithShop(
                shopName, new java.math.BigDecimal(String.valueOf(lat)),
                new java.math.BigDecimal(String.valueOf(lng)));
        shopSellerIds.put(shopName, sellerId);
    }

    // ─── When ───

    @When("구매자가 아래 내용으로 꽃 요청을 생성한다")
    public void 구매자가_아래_내용으로_꽃_요청을_생성한다(Map<String, String> data) {
        String body = testDataFactory.requestBodyFromDataTable(data);
        ResponseEntity<String> response = testAdapter.createRequest(buyerToken, body);
        scenarioContext.setResponse(response);
    }

    @When("구매자가 픽업 방식 요청을 생성한다 \\(기준 좌표 {double}, {double})")
    public void 구매자가_픽업_방식_요청을_생성한다(double lat, double lng) {
        String body = testDataFactory.requestBodyForPickup(lat, lng);
        scenarioContext.setResponse(testAdapter.createRequest(buyerToken, body));
    }

    @When("구매자가 타임슬롯 [{word}:{word}, {word}:{word}] 으로 요청을 생성한다")
    public void 구매자가_타임슬롯으로_요청을_생성한다(String kind1, String val1, String kind2, String val2) {
        String slotsStr = kind1 + ":" + val1 + ", " + kind2 + ":" + val2;
        String body = testDataFactory.requestBodyWithTimeSlots(slotsStr);
        scenarioContext.setResponse(testAdapter.createRequest(buyerToken, body));
    }

    @When("인증 헤더 없이 요청 생성 API를 호출한다")
    public void 인증_헤더_없이_요청_생성_API를_호출한다() {
        String body = testDataFactory.validRequestBody();
        scenarioContext.setResponse(testAdapter.createRequestWithoutAuth(body));
    }

    @When("구매자가 budgetTier 없이 요청 생성을 시도한다")
    public void 구매자가_budgetTier_없이_요청_생성을_시도한다() {
        String body = testDataFactory.requestBodyWithoutBudgetTier();
        scenarioContext.setResponse(testAdapter.createRequest(buyerToken, body));
    }

    @Given("판매자 {string} 이 로그인되어 있다")
    public void 판매자가_로그인되어_있다(String name) {
        // E2E 시나리오에서 이미 생성된 판매자 토큰이 있으면 재사용
        String existingToken = scenarioContext.getSellerToken(name);
        if (existingToken != null) {
            sellerToken = existingToken;
        } else {
            sellerToken = testDataFactory.createSellerAndGetToken(name);
        }
        scenarioContext.setSellerToken(sellerToken);
        scenarioContext.putSellerToken(name, sellerToken);
    }

    @When("판매자가 요청 생성 API를 호출한다")
    public void 판매자가_요청_생성_API를_호출한다() {
        String body = testDataFactory.validRequestBody();
        scenarioContext.setResponse(testAdapter.createRequest(sellerToken, body));
    }

    // ─── Then ───

    @Then("응답 상태 코드는 {int}이다")
    public void 응답_상태_코드는(int statusCode) {
        assertThat(scenarioContext.getResponse().getStatusCode().value()).isEqualTo(statusCode);
    }

    @And("요청 상태는 {string} 이다")
    public void 요청_상태는(String expectedStatus) throws Exception {
        JsonNode root = MAPPER.readTree(scenarioContext.getResponse().getBody());
        String status = root.path("data").path("status").asText();
        assertThat(status).isEqualTo(expectedStatus);
    }

    @And("만료 시각은 생성 시각으로부터 48시간 후다")
    public void 만료_시각은_48시간_후다() throws Exception {
        JsonNode root = MAPPER.readTree(scenarioContext.getResponse().getBody());
        String expiresAtStr = root.path("data").path("expiresAt").asText();
        LocalDateTime expiresAt = LocalDateTime.parse(expiresAtStr);
        LocalDateTime now = LocalDateTime.now();
        long hours = ChronoUnit.HOURS.between(now, expiresAt);
        assertThat(hours).isBetween(47L, 48L);
    }

    @Then("{string} 판매자에게 REQUEST_ARRIVED 알림이 {int}건 생성된다")
    public void 판매자에게_알림이_생성된다(String shopName, int count) {
        Long sellerId = shopSellerIds.get(shopName);
        long actual = fakeNotification.getRecords().stream()
                .filter(r -> r.sellerId().equals(sellerId))
                .count();
        assertThat(actual).isEqualTo(count);
    }

    @And("{string} 판매자에게는 알림이 생성되지 않는다")
    public void 판매자에게는_알림이_생성되지_않는다(String shopName) {
        Long sellerId = shopSellerIds.get(shopName);
        long actual = fakeNotification.getRecords().stream()
                .filter(r -> r.sellerId().equals(sellerId))
                .count();
        assertThat(actual).isZero();
    }

    @And("저장된 요청의 타임슬롯 수는 {int}개다")
    public void 저장된_요청의_타임슬롯_수는(int expectedCount) throws Exception {
        JsonNode root = MAPPER.readTree(scenarioContext.getResponse().getBody());
        Long requestId = root.path("data").path("requestId").asLong();

        String slotsJson = jdbcTemplate.queryForObject(
                "SELECT requested_time_slots_json FROM curation_request WHERE id = ?",
                String.class, requestId);
        JsonNode slotsArray = MAPPER.readTree(slotsJson);
        assertThat(slotsArray.size()).isEqualTo(expectedCount);
    }

    @And("에러 코드는 {string} 이다")
    public void 에러_코드는(String expectedErrorCode) throws Exception {
        JsonNode root = MAPPER.readTree(scenarioContext.getResponse().getBody());
        String errorCode = root.path("error").path("code").asText();
        assertThat(errorCode).isEqualTo(expectedErrorCode);
    }
}
