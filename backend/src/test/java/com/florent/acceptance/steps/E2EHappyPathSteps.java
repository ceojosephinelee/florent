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

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class E2EHappyPathSteps {

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

    // sellerId를 이름으로 추적 (알림 검증용)
    private final Map<String, Long> sellerIdsByName = new HashMap<>();

    // ─── Given ───

    @Given("반경 2km 이내에 {string} 판매자 {string} 이 존재한다")
    public void 반경_2km_이내에_판매자가_존재한다(String shopName, String sellerName) {
        // given — 기준 좌표(37.498095, 127.027610)에서 2km 이내
        String sellerToken = testDataFactory.createSellerWithShopAndGetToken(
                shopName, new BigDecimal("37.499000"), new BigDecimal("127.028000"));
        scenarioContext.putSellerToken(sellerName, sellerToken);

        Long sellerId = testDataFactory.getSellerIdFromToken(sellerToken);
        sellerIdsByName.put(sellerName, sellerId);
    }

    // ─── When ───

    @When("구매자가 픽업 방식으로 꽃 큐레이션 요청을 생성한다")
    public void 구매자가_픽업_방식으로_꽃_큐레이션_요청을_생성한다() {
        // when — 기본 좌표로 픽업 요청 생성
        String body = testDataFactory.requestBodyForPickup(37.498095, 127.027610);
        ResponseEntity<String> response = testAdapter.createRequest(
                scenarioContext.getBuyerToken(), body);
        scenarioContext.setResponse(response);
    }

    @When("판매자가 해당 요청에 대해 제안서 작성을 시작한다")
    public void 판매자가_해당_요청에_대해_제안서_작성을_시작한다() throws Exception {
        // when — requestId 추출 후 제안 초안 생성
        ResponseEntity<String> response = testAdapter.startProposal(
                scenarioContext.getSellerToken(), scenarioContext.getRequestId());
        scenarioContext.setResponse(response);

        // then — proposalId 추출
        JsonNode root = MAPPER.readTree(response.getBody());
        Long proposalId = root.path("data").path("proposalId").asLong();
        scenarioContext.setProposalId(proposalId);
    }

    @When("판매자가 아래 내용으로 제안을 임시저장하고 제출한다")
    public void 판매자가_아래_내용으로_제안을_임시저장하고_제출한다(Map<String, String> data) throws Exception {
        // when — 임시저장
        String saveBody = buildSaveBodyFromDataTable(data);
        testAdapter.saveProposal(
                scenarioContext.getSellerToken(),
                scenarioContext.getProposalId(), saveBody);

        // when — 제출
        ResponseEntity<String> submitResponse = testAdapter.submitProposal(
                scenarioContext.getSellerToken(), scenarioContext.getProposalId());
        scenarioContext.setResponse(submitResponse);
    }

    @When("구매자가 해당 제안의 상세를 조회한다")
    public void 구매자가_해당_제안의_상세를_조회한다() {
        // when
        ResponseEntity<String> response = testAdapter.getProposalDetail(
                scenarioContext.getBuyerToken(), scenarioContext.getProposalId());
        scenarioContext.setResponse(response);
    }

    @When("구매자가 해당 제안을 선택한다 \\(idempotencyKey: {string})")
    public void 구매자가_해당_제안을_선택한다_with_key(String idempotencyKey) {
        // when
        String body = "{\"idempotencyKey\":\"" + idempotencyKey + "\"}";
        ResponseEntity<String> response = testAdapter.selectProposal(
                scenarioContext.getBuyerToken(), scenarioContext.getProposalId(), body);
        scenarioContext.setResponse(response);
    }

    // ─── Then ───

    @Then("요청이 OPEN 상태로 생성된다")
    public void 요청이_OPEN_상태로_생성된다() throws Exception {
        // then
        JsonNode root = MAPPER.readTree(scenarioContext.getResponse().getBody());
        String status = root.path("data").path("status").asText();
        assertThat(status).isEqualTo("OPEN");

        // requestId를 scenarioContext에 저장 (이후 단계에서 사용)
        Long requestId = root.path("data").path("requestId").asLong();
        scenarioContext.setRequestId(requestId);
    }

    @And("{string} 에게 REQUEST_ARRIVED 알림이 생성된다")
    public void 에게_REQUEST_ARRIVED_알림이_생성된다(String sellerName) {
        // then
        Long sellerId = sellerIdsByName.get(sellerName);
        long count = fakeNotification.getRecords().stream()
                .filter(r -> r.sellerId().equals(sellerId))
                .count();
        assertThat(count).isGreaterThanOrEqualTo(1);
    }

    @Then("제안이 DRAFT 상태로 생성된다")
    public void 제안이_DRAFT_상태로_생성된다() throws Exception {
        // then
        JsonNode root = MAPPER.readTree(scenarioContext.getResponse().getBody());
        String status = root.path("data").path("status").asText();
        assertThat(status).isEqualTo("DRAFT");
    }

    @And("구매자 {string} 에게 PROPOSAL_ARRIVED 알림이 생성된다")
    public void 구매자에게_PROPOSAL_ARRIVED_알림이_생성된다_named(String buyerName) {
        // then
        assertThat(fakeNotification.getProposalRecords()).isNotEmpty();
    }

    @Then("SUBMITTED 제안 1건이 목록에 포함된다")
    public void SUBMITTED_제안_1건이_목록에_포함된다() throws Exception {
        // then
        JsonNode root = MAPPER.readTree(scenarioContext.getResponse().getBody());
        JsonNode data = root.path("data");
        assertThat(data.isArray()).isTrue();

        long submittedCount = 0;
        for (JsonNode item : data) {
            if ("SUBMITTED".equals(item.path("status").asText())) {
                submittedCount++;
            }
        }
        assertThat(submittedCount).isEqualTo(1);
    }

    @And("목록에 price 필드는 없다")
    public void 목록에_price_필드는_없다() throws Exception {
        // then
        JsonNode root = MAPPER.readTree(scenarioContext.getResponse().getBody());
        JsonNode data = root.path("data");

        for (JsonNode item : data) {
            assertThat(item.has("price")).isFalse();
        }
    }

    @Then("개념 제목, 설명, 가격이 모두 포함된다")
    public void 개념_제목_설명_가격이_모두_포함된다() throws Exception {
        // then
        JsonNode root = MAPPER.readTree(scenarioContext.getResponse().getBody());
        JsonNode data = root.path("data");

        assertThat(data.has("conceptTitle")).isTrue();
        assertThat(data.path("conceptTitle").asText()).isNotEmpty();
        assertThat(data.has("description")).isTrue();
        assertThat(data.path("description").asText()).isNotEmpty();
        assertThat(data.has("price")).isTrue();
        assertThat(data.path("price").asInt()).isGreaterThan(0);
    }

    @And("요청 상태가 {string} 로 변경된다")
    public void 요청_상태가_변경된다(String expectedStatus) {
        // then — DB에서 직접 확인
        String status = jdbcTemplate.queryForObject(
                "SELECT status FROM curation_request WHERE id = ?",
                String.class, scenarioContext.getRequestId());
        assertThat(status).isEqualTo(expectedStatus);
    }

    @And("{string} 에게 RESERVATION_CONFIRMED 알림이 생성된다")
    public void 에게_RESERVATION_CONFIRMED_알림이_생성된다(String sellerName) {
        // then
        Long sellerId = sellerIdsByName.get(sellerName);
        long count = fakeNotification.getReservationRecords().stream()
                .filter(r -> r.sellerId().equals(sellerId))
                .count();
        assertThat(count).isGreaterThanOrEqualTo(1);
    }

    @Then("예약 1건이 조회된다")
    public void 예약_1건이_조회된다() throws Exception {
        // then
        JsonNode root = MAPPER.readTree(scenarioContext.getResponse().getBody());
        JsonNode data = root.path("data");
        assertThat(data.isArray()).isTrue();
        assertThat(data.size()).isEqualTo(1);
    }

    @And("꽃집 이름은 {string} 이다")
    public void 꽃집_이름은(String expectedShopName) throws Exception {
        // then
        JsonNode root = MAPPER.readTree(scenarioContext.getResponse().getBody());
        JsonNode data = root.path("data");
        // 목록 응답의 첫 번째 항목에서 shopName 확인
        JsonNode first = data.isArray() ? data.get(0) : data;
        String shopName = first.path("shopName").asText();
        assertThat(shopName).isEqualTo(expectedShopName);
    }

    @And("수령 방식은 {string} 이다")
    public void 수령_방식은(String expectedType) throws Exception {
        // then
        JsonNode root = MAPPER.readTree(scenarioContext.getResponse().getBody());
        JsonNode data = root.path("data");
        JsonNode first = data.isArray() ? data.get(0) : data;
        String fulfillmentType = first.path("fulfillmentType").asText();
        assertThat(fulfillmentType).isEqualTo(expectedType);
    }

    // ─── Helpers ───

    private String buildSaveBodyFromDataTable(Map<String, String> data) {
        try {
            var node = MAPPER.createObjectNode();
            if (data.containsKey("conceptTitle")) {
                node.put("conceptTitle", data.get("conceptTitle"));
            }
            if (data.containsKey("description")) {
                node.put("description", data.get("description"));
            }
            if (data.containsKey("availableSlotKind") && data.containsKey("availableSlotValue")) {
                var slot = MAPPER.createObjectNode();
                slot.put("kind", data.get("availableSlotKind"));
                slot.put("value", data.get("availableSlotValue"));
                node.set("availableSlot", slot);
            }
            if (data.containsKey("price")) {
                node.put("price", Integer.parseInt(data.get("price")));
            }
            // 필수 필드 기본값 채우기
            if (!node.has("moodColors")) {
                var arr = MAPPER.createArrayNode();
                arr.add("PINK");
                node.set("moodColors", arr);
            }
            if (!node.has("mainFlowers")) {
                var arr = MAPPER.createArrayNode();
                arr.add("장미");
                arr.add("프리지아");
                node.set("mainFlowers", arr);
            }
            if (!node.has("wrappingStyle")) {
                var arr = MAPPER.createArrayNode();
                arr.add("리본");
                node.set("wrappingStyle", arr);
            }
            if (!node.has("imageUrls")) {
                var arr = MAPPER.createArrayNode();
                arr.add("https://example.com/img1.jpg");
                node.set("imageUrls", arr);
            }
            return node.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
