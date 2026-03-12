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

    @When("클라이언트가 헬스 체크 엔드포인트를 호출한다")
    public void 클라이언트가_헬스_체크_엔드포인트를_호출한다() {
        response = testAdapter.getHealth();
    }

    @Then("헬스 체크 응답 상태 코드는 {int}이다")
    public void 헬스_체크_응답_상태_코드는(int statusCode) {
        assertThat(response.getStatusCode().value()).isEqualTo(statusCode);
    }

    @And("응답 바디에 {string} 이 포함된다")
    public void 응답_바디에_이_포함된다(String expected) {
        assertThat(response.getBody()).contains(expected);
    }
}
