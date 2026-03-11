package com.florent.acceptance.steps;

import com.florent.support.TestAdapter;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class HealthCheckSteps {

    @Autowired
    private TestAdapter testAdapter;

    private ResponseEntity<String> response;

    @When("the client requests the health endpoint")
    public void the_client_requests_the_health_endpoint() {
        response = testAdapter.getHealth();
    }

    @Then("the response status code is {int}")
    public void the_response_status_code_is(int statusCode) {
        assertThat(response.getStatusCode().value()).isEqualTo(statusCode);
    }

    @And("the response body contains {string}")
    public void the_response_body_contains(String expectedContent) {
        assertThat(response.getBody()).contains(expectedContent);
    }
}