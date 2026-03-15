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
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class NotificationSteps {

    @Autowired
    private TestAdapter testAdapter;

    @Autowired
    private TestDataFactory testDataFactory;

    @Autowired
    private ScenarioContext scenarioContext;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final Map<String, String> userTokens = new HashMap<>();

    // ─── Given ───

    @Given("구매자 {string}가 가입되어 있다")
    public void 구매자가_가입되어_있다(String name) {
        String token = testDataFactory.createBuyerAndGetToken(name);
        if (scenarioContext.getBuyerToken() == null) {
            scenarioContext.setBuyerToken(token);
        }
        userTokens.put(name, token);
    }

    @Given("구매자에게 알림 {int}건이 저장되어 있다")
    public void 구매자에게_알림_N건이_저장되어_있다(int count) {
        String token = scenarioContext.getBuyerToken();
        Long userId = getUserIdFromToken(token);

        for (int i = 0; i < count; i++) {
            jdbcTemplate.update(
                    "INSERT INTO notification "
                            + "(user_id, type, reference_type, reference_id, title, body, is_read, created_at, updated_at) "
                            + "VALUES (?, 'REQUEST_ARRIVED', 'REQUEST', ?, '새 요청 도착', '새로운 꽃 요청이 도착했습니다.', false, "
                            + "now() - INTERVAL '" + (count - i) + "' HOUR, now())",
                    userId, (long) (i + 1));
        }

        Long firstNotificationId = jdbcTemplate.queryForObject(
                "SELECT id FROM notification WHERE user_id = ? ORDER BY created_at ASC LIMIT 1",
                Long.class, userId);
        scenarioContext.setNotificationId(firstNotificationId);
    }

    // ─── When ───

    @When("구매자가 알림 목록을 조회한다 page={int} size={int}")
    public void 구매자가_알림_목록을_조회한다(int page, int size) {
        ResponseEntity<String> response = testAdapter.getNotifications(
                scenarioContext.getBuyerToken(), page, size);
        scenarioContext.setResponse(response);
    }

    @When("구매자가 첫 번째 알림을 읽음 처리한다")
    public void 구매자가_첫_번째_알림을_읽음_처리한다() {
        ResponseEntity<String> response = testAdapter.markNotificationAsRead(
                scenarioContext.getBuyerToken(), scenarioContext.getNotificationId());
        scenarioContext.setResponse(response);
    }

    @When("{string}가 첫 번째 알림을 읽음 처리한다")
    public void 다른_유저가_첫_번째_알림을_읽음_처리한다(String name) {
        String token = userTokens.get(name);
        ResponseEntity<String> response = testAdapter.markNotificationAsRead(
                token, scenarioContext.getNotificationId());
        scenarioContext.setResponse(response);
    }

    @When("구매자가 존재하지 않는 알림을 읽음 처리한다")
    public void 구매자가_존재하지_않는_알림을_읽음_처리한다() {
        ResponseEntity<String> response = testAdapter.markNotificationAsRead(
                scenarioContext.getBuyerToken(), 999999L);
        scenarioContext.setResponse(response);
    }

    // ─── Then ───

    @And("알림 목록에 {int}건이 포함된다")
    public void 알림_목록에_N건이_포함된다(int expectedCount) throws Exception {
        JsonNode root = MAPPER.readTree(scenarioContext.getResponse().getBody());
        JsonNode content = root.path("data").path("content");
        assertThat(content.size()).isEqualTo(expectedCount);
    }

    @And("전체 알림 수는 {int}건이다")
    public void 전체_알림_수는_N건이다(int expectedTotal) throws Exception {
        JsonNode root = MAPPER.readTree(scenarioContext.getResponse().getBody());
        int totalElements = root.path("data").path("totalElements").asInt();
        assertThat(totalElements).isEqualTo(expectedTotal);
    }

    @And("읽음 상태는 true 이다")
    public void 읽음_상태는_true_이다() throws Exception {
        JsonNode root = MAPPER.readTree(scenarioContext.getResponse().getBody());
        boolean isRead = root.path("data").path("isRead").asBoolean();
        assertThat(isRead).isTrue();
    }

    private Long getUserIdFromToken(String token) {
        try {
            String json = new String(java.util.Base64.getDecoder().decode(token));
            return MAPPER.readTree(json).get("userId").asLong();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
