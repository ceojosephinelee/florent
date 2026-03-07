package com.florent.acceptance.health;

import com.florent.acceptance.testadapter.HealthCheckTestAdapter;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.assertj.core.api.Assertions;
public class HealthCheckStepDefinitions {

    private final HealthCheckTestAdapter healthCheckTestAdapter;
    private HealthCheckTestAdapter.HealthCheckResult result;

    public HealthCheckStepDefinitions(HealthCheckTestAdapter healthCheckTestAdapter) {
        this.healthCheckTestAdapter = healthCheckTestAdapter;
    }

    @Given("the backend service is running")
    public void theBackendServiceIsRunning() {
        // Spring Boot random port is started by CucumberSpringConfiguration.
    }

    @When("I request the health check endpoint")
    public void iRequestTheHealthCheckEndpoint() {
        result = healthCheckTestAdapter.checkHealth();
    }

    @Then("the response status code is {int}")
    public void theResponseStatusCodeIs(int statusCode) {
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.statusCode()).isEqualTo(statusCode);
    }

    @And("the health status is {string}")
    public void theHealthStatusIs(String status) {
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.status()).isEqualTo(status);
    }
}
