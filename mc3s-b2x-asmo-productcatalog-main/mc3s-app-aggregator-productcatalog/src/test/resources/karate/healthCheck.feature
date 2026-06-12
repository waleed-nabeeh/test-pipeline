Feature: HEALTHCHECK

  Background:
    * url baseUrl


  @test
  Scenario: Get health check status
    Given path "/actuator/health"
    Then method GET
    And status 200
    And match response.status == 'UP'