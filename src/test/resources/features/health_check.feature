Feature: Health check
  The service should expose a minimal health endpoint.

  Scenario: Health endpoint returns UP
    Given the backend service is running
    When I request the health check endpoint
    Then the response status code is 200
    And the health status is "UP"
