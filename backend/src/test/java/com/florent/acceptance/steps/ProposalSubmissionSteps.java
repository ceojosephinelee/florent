package com.florent.acceptance.steps;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.florent.fake.FakeSaveNotificationPort;
import com.florent.support.ScenarioContext;
import com.florent.support.TestAdapter;
import com.florent.support.TestDataFactory;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

public class ProposalSubmissionSteps {

    @Autowired
    private TestAdapter testAdapter;

    @Autowired
    private TestDataFactory testDataFactory;

    @Autowired
    private ScenarioContext scenarioContext;

    @Autowired
    private FakeSaveNotificationPort fakeNotification;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private Long expiredRequestId;

    // ─── Given ───

    @Given("판매자 {string} 가 꽃집과 함께 등록되어 있다")
    public void 판매자가_꽃집과_함께_등록되어_있다(String shopName) {
        String sellerToken = testDataFactory.createSellerWithShopAndGetToken(
                shopName, new BigDecimal("37.498095"), new BigDecimal("127.027610"));
        scenarioContext.setSellerToken(sellerToken);
        scenarioContext.putSellerToken(shopName, sellerToken);
    }

    @Given("판매자가 해당 요청에 제안 초안을 생성했다")
    public void 판매자가_해당_요청에_제안_초안을_생성했다() throws Exception {
        // when
        ResponseEntity<String> response = testAdapter.startProposal(
                scenarioContext.getSellerToken(), scenarioContext.getRequestId());

        // then — extract proposalId
        JsonNode root = MAPPER.readTree(response.getBody());
        Long proposalId = root.path("data").path("proposalId").asLong();
        scenarioContext.setProposalId(proposalId);
    }

    @Given("판매자가 제안을 임시저장했다")
    public void 판매자가_제안을_임시저장했다() {
        String body = testDataFactory.validSaveProposalBody();
        testAdapter.saveProposal(
                scenarioContext.getSellerToken(),
                scenarioContext.getProposalId(), body);
    }

    @Given("EXPIRED 상태의 요청이 존재한다")
    public void EXPIRED_상태의_요청이_존재한다() {
        Long buyerId = testDataFactory.getBuyerIdFromToken(scenarioContext.getBuyerToken());
        expiredRequestId = testDataFactory.createExpiredRequest(buyerId);
    }

    @Given("판매자가 제안을 제출했다")
    public void 판매자가_제안을_제출했다() {
        testAdapter.submitProposal(
                scenarioContext.getSellerToken(), scenarioContext.getProposalId());
    }

    @Given("다른 판매자 {string} 가 꽃집과 함께 등록되어 있다")
    public void 다른_판매자가_꽃집과_함께_등록되어_있다(String shopName) {
        String otherSellerToken = testDataFactory.createSellerWithShopAndGetToken(
                shopName, new BigDecimal("37.499000"), new BigDecimal("127.028000"));
        scenarioContext.putSellerToken(shopName, otherSellerToken);
    }

    // ─── When ───

    @When("판매자가 해당 요청에 제안 초안을 생성한다")
    public void 판매자가_해당_요청에_제안_초안을_생성한다() {
        ResponseEntity<String> response = testAdapter.startProposal(
                scenarioContext.getSellerToken(), scenarioContext.getRequestId());
        scenarioContext.setResponse(response);
    }

    @When("판매자가 제안을 임시저장한다")
    public void 판매자가_제안을_임시저장한다() {
        String body = testDataFactory.validSaveProposalBody();
        ResponseEntity<String> response = testAdapter.saveProposal(
                scenarioContext.getSellerToken(),
                scenarioContext.getProposalId(), body);
        scenarioContext.setResponse(response);
    }

    @When("판매자가 제안을 제출한다")
    public void 판매자가_제안을_제출한다() {
        ResponseEntity<String> response = testAdapter.submitProposal(
                scenarioContext.getSellerToken(), scenarioContext.getProposalId());
        scenarioContext.setResponse(response);
    }

    @When("판매자가 만료된 요청에 제안 초안을 생성한다")
    public void 판매자가_만료된_요청에_제안_초안을_생성한다() {
        ResponseEntity<String> response = testAdapter.startProposal(
                scenarioContext.getSellerToken(), expiredRequestId);
        scenarioContext.setResponse(response);
    }

    @When("판매자가 본인 제안 목록을 조회한다")
    public void 판매자가_본인_제안_목록을_조회한다() {
        ResponseEntity<String> response = testAdapter.getSellerProposals(
                scenarioContext.getSellerToken(), 0, 20);
        scenarioContext.setResponse(response);
    }

    @When("판매자가 필수_필드_없이_제안을_제출한다")
    public void 판매자가_필수_필드_없이_제안을_제출한다() {
        // save 없이 바로 제출 → 필수 필드(description, price 등) 미설정
        ResponseEntity<String> response = testAdapter.submitProposal(
                scenarioContext.getSellerToken(), scenarioContext.getProposalId());
        scenarioContext.setResponse(response);
    }

    @When("판매자가_SUBMITTED_제안을_임시저장한다")
    public void 판매자가_SUBMITTED_제안을_임시저장한다() {
        // 이미 제출된 제안에 대해 임시저장 시도
        String body = testDataFactory.validSaveProposalBody();
        ResponseEntity<String> response = testAdapter.saveProposal(
                scenarioContext.getSellerToken(),
                scenarioContext.getProposalId(), body);
        scenarioContext.setResponse(response);
    }

    @When("다른 판매자가 해당 제안을 임시저장한다")
    public void 다른_판매자가_해당_제안을_임시저장한다() {
        // 마지막으로 등록된 다른 판매자의 토큰으로 기존 제안에 접근
        String otherToken = scenarioContext.getSellerToken("꽃집D");
        String body = testDataFactory.validSaveProposalBody();
        ResponseEntity<String> response = testAdapter.saveProposal(
                otherToken, scenarioContext.getProposalId(), body);
        scenarioContext.setResponse(response);
    }

    // ─── Then ───

    @And("제안 상태는 {string} 이다")
    public void 제안_상태는(String expectedStatus) throws Exception {
        JsonNode root = MAPPER.readTree(scenarioContext.getResponse().getBody());
        String status = root.path("data").path("status").asText();
        assertThat(status).isEqualTo(expectedStatus);
    }

    @And("제안 만료 시각이 존재한다")
    public void 제안_만료_시각이_존재한다() throws Exception {
        JsonNode root = MAPPER.readTree(scenarioContext.getResponse().getBody());
        String expiresAt = root.path("data").path("expiresAt").asText();
        assertThat(expiresAt).isNotEmpty();
    }

    @And("제안 제출 시각이 존재한다")
    public void 제안_제출_시각이_존재한다() throws Exception {
        JsonNode root = MAPPER.readTree(scenarioContext.getResponse().getBody());
        String submittedAt = root.path("data").path("submittedAt").asText();
        assertThat(submittedAt).isNotEmpty();
    }

    @And("구매자에게 PROPOSAL_ARRIVED 알림이 생성된다")
    public void 구매자에게_PROPOSAL_ARRIVED_알림이_생성된다() {
        assertThat(fakeNotification.getProposalRecords()).hasSize(1);
        assertThat(fakeNotification.getProposalRecords().get(0).buyerId()).isNotNull();
    }

    @And("제안 목록에 {int}건이 포함된다")
    public void 제안_목록에_N건이_포함된다(int expectedCount) throws Exception {
        JsonNode root = MAPPER.readTree(scenarioContext.getResponse().getBody());
        JsonNode content = root.path("data").path("content");
        assertThat(content.isArray()).isTrue();
        assertThat(content.size()).isEqualTo(expectedCount);
    }

    @And("목록 응답에 페이지네이션 필드가 포함된다")
    public void 목록_응답에_페이지네이션_필드가_포함된다() throws Exception {
        JsonNode root = MAPPER.readTree(scenarioContext.getResponse().getBody());
        JsonNode data = root.path("data");
        assertThat(data.has("page")).isTrue();
        assertThat(data.has("size")).isTrue();
        assertThat(data.has("totalElements")).isTrue();
        assertThat(data.has("totalPages")).isTrue();
        assertThat(data.has("last")).isTrue();
    }
}
