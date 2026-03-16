package com.florent.acceptance.steps;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.florent.fake.FakeSaveNotificationUseCase;
import com.florent.support.ScenarioContext;
import com.florent.support.TestAdapter;
import com.florent.support.TestDataFactory;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class ReservationConfirmationSteps {

    @Autowired
    private TestAdapter testAdapter;

    @Autowired
    private TestDataFactory testDataFactory;

    @Autowired
    private ScenarioContext scenarioContext;

    @Autowired
    private FakeSaveNotificationUseCase fakeNotification;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private String lastIdempotencyKey;

    // ─── Given ───

    @Given("구매자가 해당 제안을 선택했다")
    public void 구매자가_해당_제안을_선택했다() throws Exception {
        lastIdempotencyKey = UUID.randomUUID().toString();
        String body = "{\"idempotencyKey\":\"" + lastIdempotencyKey + "\"}";
        ResponseEntity<String> response = testAdapter.selectProposal(
                scenarioContext.getBuyerToken(), scenarioContext.getProposalId(), body);

        JsonNode root = MAPPER.readTree(response.getBody());
        Long reservationId = root.path("data").path("reservationId").asLong();
        scenarioContext.setReservationId(reservationId);
    }

    @Given("다른 판매자가 해당 요청에 제안을 제출했다")
    public void 다른_판매자가_해당_요청에_제안을_제출했다() throws Exception {
        String otherSellerToken = scenarioContext.getSellerToken("꽃집S");

        // 다른 판매자의 제안 초안 생성
        ResponseEntity<String> startResponse = testAdapter.startProposal(
                otherSellerToken, scenarioContext.getRequestId());
        JsonNode root = MAPPER.readTree(startResponse.getBody());
        Long otherProposalId = root.path("data").path("proposalId").asLong();
        scenarioContext.setOtherProposalId(otherProposalId);

        // 임시저장
        String saveBody = testDataFactory.validSaveProposalBody();
        testAdapter.saveProposal(otherSellerToken, otherProposalId, saveBody);

        // 제출
        testAdapter.submitProposal(otherSellerToken, otherProposalId);
    }

    // ─── When ───

    @When("구매자가 해당 제안을 선택한다")
    public void 구매자가_해당_제안을_선택한다() {
        lastIdempotencyKey = UUID.randomUUID().toString();
        String body = "{\"idempotencyKey\":\"" + lastIdempotencyKey + "\"}";
        ResponseEntity<String> response = testAdapter.selectProposal(
                scenarioContext.getBuyerToken(), scenarioContext.getProposalId(), body);
        scenarioContext.setResponse(response);
    }

    @When("구매자가 같은_idempotencyKey로_다시_선택한다")
    public void 구매자가_같은_idempotencyKey로_다시_선택한다() {
        // 새 요청/제안이 필요하지만, 같은 idempotencyKey 사용
        String body = "{\"idempotencyKey\":\"" + lastIdempotencyKey + "\"}";
        ResponseEntity<String> response = testAdapter.selectProposal(
                scenarioContext.getBuyerToken(), scenarioContext.getProposalId(), body);
        scenarioContext.setResponse(response);
    }

    @When("구매자가 다른_판매자의_제안을_선택한다")
    public void 구매자가_다른_판매자의_제안을_선택한다() {
        String body = "{\"idempotencyKey\":\"" + UUID.randomUUID() + "\"}";
        ResponseEntity<String> response = testAdapter.selectProposal(
                scenarioContext.getBuyerToken(), scenarioContext.getOtherProposalId(), body);
        scenarioContext.setResponse(response);
    }

    @When("구매자가 예약 목록을 조회한다")
    public void 구매자가_예약_목록을_조회한다() {
        ResponseEntity<String> response = testAdapter.getBuyerReservations(
                scenarioContext.getBuyerToken());
        scenarioContext.setResponse(response);
    }

    @When("구매자가 예약 상세를 조회한다")
    public void 구매자가_예약_상세를_조회한다() {
        ResponseEntity<String> response = testAdapter.getBuyerReservationDetail(
                scenarioContext.getBuyerToken(), scenarioContext.getReservationId());
        scenarioContext.setResponse(response);
    }

    // ─── Then ───

    @And("예약 상태는 {string} 이다")
    public void 예약_상태는(String expectedStatus) throws Exception {
        JsonNode root = MAPPER.readTree(scenarioContext.getResponse().getBody());
        String status = root.path("data").path("status").asText();
        assertThat(status).isEqualTo(expectedStatus);
    }

    @And("결제 상태는 {string} 이다")
    public void 결제_상태는(String expectedStatus) throws Exception {
        JsonNode root = MAPPER.readTree(scenarioContext.getResponse().getBody());
        String paymentStatus = root.path("data").path("paymentStatus").asText();
        assertThat(paymentStatus).isEqualTo(expectedStatus);
    }

    @And("판매자에게 RESERVATION_CONFIRMED 알림이 생성된다")
    public void 판매자에게_RESERVATION_CONFIRMED_알림이_생성된다() {
        assertThat(fakeNotification.getReservationRecords()).hasSize(1);
        assertThat(fakeNotification.getReservationRecords().get(0).sellerId()).isNotNull();
    }

    @And("예약 목록에 {int}건이 포함된다")
    public void 예약_목록에_N건이_포함된다(int expectedCount) throws Exception {
        JsonNode root = MAPPER.readTree(scenarioContext.getResponse().getBody());
        JsonNode data = root.path("data");
        assertThat(data.isArray()).isTrue();
        assertThat(data.size()).isEqualTo(expectedCount);
    }

    @And("예약 상세의 상태는 {string} 이다")
    public void 예약_상세의_상태는(String expectedStatus) throws Exception {
        JsonNode root = MAPPER.readTree(scenarioContext.getResponse().getBody());
        String status = root.path("data").path("status").asText();
        assertThat(status).isEqualTo(expectedStatus);
    }
}
