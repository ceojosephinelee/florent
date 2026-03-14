package com.florent.acceptance.steps;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.florent.support.ScenarioContext;
import com.florent.support.TestAdapter;
import com.florent.support.TestDataFactory;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class RequestInquirySteps {

    @Autowired
    private TestAdapter testAdapter;

    @Autowired
    private TestDataFactory testDataFactory;

    @Autowired
    private ScenarioContext scenarioContext;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private String buyerToken;
    private Long requestId;
    private final Map<String, Long> otherBuyerRequestIds = new HashMap<>();

    // ─── Background ───

    @Given("구매자의 OPEN 요청 1건이 존재한다")
    public void 구매자의_OPEN_요청_1건이_존재한다() {
        // given
        Long buyerId = getBuyerIdFromCurrentToken();
        requestId = testDataFactory.createOpenRequest(buyerId);
    }

    @Given("해당 요청에 DRAFT 제안 {int}건, SUBMITTED 제안 {int}건이 존재한다")
    public void 해당_요청에_제안이_존재한다(int draftCount, int submittedCount) {
        // given
        for (int i = 0; i < draftCount; i++) {
            Long shopId = createShopForProposal("draft_shop_" + i);
            testDataFactory.createProposal(requestId, shopId, "DRAFT");
        }
        for (int i = 0; i < submittedCount; i++) {
            Long shopId = createShopForProposal("submitted_shop_" + i);
            testDataFactory.createProposal(requestId, shopId, "SUBMITTED");
        }
    }

    // ─── When ───

    @When("구매자가 요청 목록을 조회한다")
    public void 구매자가_요청_목록을_조회한다() {
        // when
        scenarioContext.setResponse(testAdapter.getRequestList(getCurrentBuyerToken(), 0, 20));
    }

    @When("구매자가 요청 상세를 조회한다")
    public void 구매자가_요청_상세를_조회한다() {
        // when
        scenarioContext.setResponse(testAdapter.getRequestDetail(getCurrentBuyerToken(), requestId));
    }

    @When("구매자가 존재하지 않는 requestId {long} 로 조회한다")
    public void 구매자가_존재하지_않는_requestId로_조회한다(long nonExistentId) {
        // when
        scenarioContext.setResponse(testAdapter.getRequestDetail(getCurrentBuyerToken(), nonExistentId));
    }

    @Given("구매자 {string} 의 요청이 존재한다")
    public void 다른_구매자의_요청이_존재한다(String name) {
        // given
        String otherToken = testDataFactory.createBuyerAndGetToken(name);
        Long otherBuyerId = testDataFactory.getBuyerIdFromToken(otherToken);
        Long otherRequestId = testDataFactory.createOpenRequest(otherBuyerId);
        otherBuyerRequestIds.put(name, otherRequestId);
        scenarioContext.putOtherBuyerRequestId(name, otherRequestId);
    }

    @When("구매자 {string} 이 {string} 의 요청 상세를 조회한다")
    public void 구매자가_다른_구매자의_요청_상세를_조회한다(String viewer, String owner) {
        // when
        Long otherRequestId = otherBuyerRequestIds.get(owner);
        scenarioContext.setResponse(testAdapter.getRequestDetail(getCurrentBuyerToken(), otherRequestId));
    }

    // ─── Then ───

    @And("첫 번째 요청의 draftProposalCount 는 {int}다")
    public void 첫_번째_요청의_draftProposalCount는(int expected) throws Exception {
        // then
        JsonNode root = MAPPER.readTree(scenarioContext.getResponse().getBody());
        int count = root.path("data").path("content").get(0).path("draftProposalCount").asInt();
        assertThat(count).isEqualTo(expected);
    }

    @And("첫 번째 요청의 submittedProposalCount 는 {int}이다")
    public void 첫_번째_요청의_submittedProposalCount는(int expected) throws Exception {
        // then
        JsonNode root = MAPPER.readTree(scenarioContext.getResponse().getBody());
        int count = root.path("data").path("content").get(0).path("submittedProposalCount").asInt();
        assertThat(count).isEqualTo(expected);
    }

    @And("요청의 만료 시각이 포함된다")
    public void 요청의_만료_시각이_포함된다() throws Exception {
        // then
        JsonNode root = MAPPER.readTree(scenarioContext.getResponse().getBody());
        String expiresAt = root.path("data").path("expiresAt").asText();
        assertThat(expiresAt).isNotEmpty();
    }

    @And("타임슬롯 정보가 포함된다")
    public void 타임슬롯_정보가_포함된다() throws Exception {
        // then
        JsonNode root = MAPPER.readTree(scenarioContext.getResponse().getBody());
        JsonNode timeSlots = root.path("data").path("requestedTimeSlots");
        assertThat(timeSlots.isArray()).isTrue();
        assertThat(timeSlots.size()).isGreaterThan(0);
    }

    // ─── Helpers ───

    private Long getBuyerIdFromCurrentToken() {
        return testDataFactory.getBuyerIdFromToken(getCurrentBuyerToken());
    }

    private String getCurrentBuyerToken() {
        if (buyerToken != null) {
            return buyerToken;
        }
        Long buyerId = jdbcTemplate.queryForObject(
                "SELECT id FROM buyer ORDER BY id ASC LIMIT 1", Long.class);
        Long userId = jdbcTemplate.queryForObject(
                "SELECT user_id FROM buyer WHERE id = ?", Long.class, buyerId);
        buyerToken = com.florent.support.TestTokenProvider.createBuyerToken(userId, buyerId);
        return buyerToken;
    }

    private Long createShopForProposal(String shopName) {
        Long sellerId = testDataFactory.createSellerWithShop(
                shopName, new BigDecimal("37.498095"), new BigDecimal("127.027610"));
        return jdbcTemplate.queryForObject(
                "SELECT id FROM flower_shop WHERE seller_id = ?", Long.class, sellerId);
    }
}
