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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ProposalInquirySteps {

    @Autowired
    private TestAdapter testAdapter;

    @Autowired
    private TestDataFactory testDataFactory;

    @Autowired
    private ScenarioContext scenarioContext;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private Long requestId;
    private Long submittedProposalId;

    // ─── Background ───

    @Given("구매자의 OPEN 요청이 존재한다")
    public void 구매자의_OPEN_요청이_존재한다() {
        // given
        Long buyerId = getBuyerIdFromCurrentToken();
        requestId = testDataFactory.createOpenRequest(buyerId);
    }

    @Given("해당 요청에 SUBMITTED 제안 1건과 EXPIRED 제안 1건이 존재한다")
    public void 해당_요청에_SUBMITTED_제안_1건과_EXPIRED_제안_1건이_존재한다() {
        // given
        Long submittedShopId = createShopForProposal("submitted_shop");
        submittedProposalId = testDataFactory.createProposalAndGetId(
                requestId, submittedShopId, "SUBMITTED");

        Long expiredShopId = createShopForProposal("expired_shop");
        testDataFactory.createProposalAndGetId(requestId, expiredShopId, "EXPIRED");
    }

    @Given("해당 요청에 DRAFT 제안 1건이 추가로 존재한다")
    public void 해당_요청에_DRAFT_제안_1건이_추가로_존재한다() {
        // given
        Long draftShopId = createShopForProposal("draft_shop");
        testDataFactory.createProposalAndGetId(requestId, draftShopId, "DRAFT");
    }

    // ─── When ───

    @When("구매자가 요청의 제안 목록을 조회한다")
    public void 구매자가_요청의_제안_목록을_조회한다() {
        // when
        scenarioContext.setResponse(
                testAdapter.getProposalList(getCurrentBuyerToken(), requestId));
    }

    @When("구매자가 SUBMITTED 제안의 상세를 조회한다")
    public void 구매자가_SUBMITTED_제안의_상세를_조회한다() {
        // when
        scenarioContext.setResponse(
                testAdapter.getProposalDetail(getCurrentBuyerToken(), submittedProposalId));
    }

    @When("구매자 {string} 이 {string} 요청의 제안 목록을 조회한다")
    public void 구매자가_다른_구매자_요청의_제안_목록을_조회한다(String viewer, String owner) {
        // when
        Long otherRequestId = scenarioContext.getOtherBuyerRequestId(owner);
        scenarioContext.setResponse(
                testAdapter.getProposalList(getCurrentBuyerToken(), otherRequestId));
    }

    @When("구매자가 존재하지 않는 proposalId {long} 로 상세를 조회한다")
    public void 구매자가_존재하지_않는_proposalId로_상세를_조회한다(long nonExistentId) {
        // when
        scenarioContext.setResponse(
                testAdapter.getProposalDetail(getCurrentBuyerToken(), nonExistentId));
    }

    // ─── Then ───

    @And("SUBMITTED 제안이 목록에 포함된다")
    public void SUBMITTED_제안이_목록에_포함된다() throws Exception {
        // then
        JsonNode root = MAPPER.readTree(scenarioContext.getResponse().getBody());
        JsonNode data = root.path("data");
        assertThat(data.isArray()).isTrue();

        boolean hasSubmitted = false;
        for (JsonNode item : data) {
            if ("SUBMITTED".equals(item.path("status").asText())) {
                hasSubmitted = true;
                break;
            }
        }
        assertThat(hasSubmitted).isTrue();
    }

    @And("제안 목록 응답에 price 필드가 없다")
    public void 제안_목록_응답에_price_필드가_없다() throws Exception {
        // then
        JsonNode root = MAPPER.readTree(scenarioContext.getResponse().getBody());
        JsonNode data = root.path("data");

        for (JsonNode item : data) {
            assertThat(item.has("price")).isFalse();
        }
    }

    @And("EXPIRED 제안이 status {string} 로 목록에 포함된다")
    public void EXPIRED_제안이_status로_목록에_포함된다(String expectedStatus) throws Exception {
        // then
        JsonNode root = MAPPER.readTree(scenarioContext.getResponse().getBody());
        JsonNode data = root.path("data");

        boolean hasExpired = false;
        for (JsonNode item : data) {
            if (expectedStatus.equals(item.path("status").asText())) {
                hasExpired = true;
                break;
            }
        }
        assertThat(hasExpired).isTrue();
    }

    @And("응답에 conceptTitle, description, availableSlot, price 필드가 모두 포함된다")
    public void 응답에_핵심_필드가_모두_포함된다() throws Exception {
        // then
        JsonNode root = MAPPER.readTree(scenarioContext.getResponse().getBody());
        JsonNode data = root.path("data");

        assertThat(data.has("conceptTitle")).isTrue();
        assertThat(data.has("description")).isTrue();
        assertThat(data.has("availableSlot")).isTrue();
        assertThat(data.has("price")).isTrue();
    }

    @And("목록에 DRAFT 상태 제안이 없다")
    public void 목록에_DRAFT_상태_제안이_없다() throws Exception {
        // then
        JsonNode root = MAPPER.readTree(scenarioContext.getResponse().getBody());
        JsonNode data = root.path("data");

        for (JsonNode item : data) {
            assertThat(item.path("status").asText()).isNotEqualTo("DRAFT");
        }
    }

    @And("응답에 shop 정보 \\(name, phone, addressText) 가 포함된다")
    public void 응답에_shop_정보가_포함된다() throws Exception {
        // then
        JsonNode root = MAPPER.readTree(scenarioContext.getResponse().getBody());
        JsonNode shop = root.path("data").path("shop");

        assertThat(shop.has("name")).isTrue();
        assertThat(shop.has("phone")).isTrue();
        assertThat(shop.has("addressText")).isTrue();
    }

    // ─── Helpers ───

    private Long getBuyerIdFromCurrentToken() {
        return testDataFactory.getBuyerIdFromToken(getCurrentBuyerToken());
    }

    private String getCurrentBuyerToken() {
        return scenarioContext.getBuyerToken();
    }

    private Long createShopForProposal(String shopName) {
        Long sellerId = testDataFactory.createSellerWithShop(
                shopName, new BigDecimal("37.498095"), new BigDecimal("127.027610"));
        return jdbcTemplate.queryForObject(
                "SELECT id FROM flower_shop WHERE seller_id = ?", Long.class, sellerId);
    }
}