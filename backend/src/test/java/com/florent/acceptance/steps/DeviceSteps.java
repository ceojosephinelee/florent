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

import static org.assertj.core.api.Assertions.assertThat;

public class DeviceSteps {

    @Autowired
    private TestAdapter testAdapter;

    @Autowired
    private TestDataFactory testDataFactory;

    @Autowired
    private ScenarioContext scenarioContext;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    // ─── Given ───

    @Given("구매자가 FCM 토큰 {string}을 {string}로 등록했다")
    public void 구매자가_FCM_토큰을_등록했다(String fcmToken, String platform) {
        String body = "{\"fcmToken\":\"" + fcmToken + "\",\"platform\":\"" + platform + "\"}";
        testAdapter.registerDevice(scenarioContext.getBuyerToken(), body);
    }

    // ─── When ───

    @When("구매자가 FCM 토큰 {string}을 {string}로 등록한다")
    public void 구매자가_FCM_토큰을_등록한다(String fcmToken, String platform) {
        String body = "{\"fcmToken\":\"" + fcmToken + "\",\"platform\":\"" + platform + "\"}";
        ResponseEntity<String> response = testAdapter.registerDevice(
                scenarioContext.getBuyerToken(), body);
        scenarioContext.setResponse(response);
    }

    // ─── Then ───

    @And("디바이스 ID가 반환된다")
    public void 디바이스_ID가_반환된다() throws Exception {
        JsonNode root = MAPPER.readTree(scenarioContext.getResponse().getBody());
        Long deviceId = root.path("data").path("deviceId").asLong();
        assertThat(deviceId).isPositive();
    }
}
