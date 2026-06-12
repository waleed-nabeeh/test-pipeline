Feature: Catalog related API's
  Background:
    * url baseUrl
    * print baseUrl

  @Performance
  Scenario: All catalogs for context
    Given path '/aggregator/catalog'
    And header Authorization = 'Bearer ' + ACCESS_TOKEN_ADMIN
    And header X-Store = storeIdentifier
    And header karate-name = 'Get All catalogs for context'
    When method GET
    Then status 200
    And match response contains {offset: '#number', limit: '#number', total: '#number', totalPages: '#number', count: '#number', results: '#array'}
    And def singleEntry = response.results[0]
    And match singleEntry contains {id: '#string', key: '#string', name: '#string', description: '#string'}

  Scenario: All catalogs for context [Failure - Unauthorized]
    Given path '/aggregator/catalog'
    And header X-Store = storeIdentifier
    And header Authorization = 'Bearer ' + 'invalidAccessToken'
    When method GET
    Then status 401