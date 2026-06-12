Feature: Productcatalog Aggregator - category

  Background:
    * url baseUrl
    * print baseUrl
    * header X-Store = storeIdentifier
    * def responseSchema = {offset: '#number', limit: '#number', total: '#number', totalPages: '#number', count: '#number', results: '#array'}
    * def categorySchema = { id: '#uuid', key: '#string', name: '#string', description: '#string', image: '#object',restricted: '#boolean', restrictedCustomers:'#array', metaData:'#object', ancestors:'#array', assets: '#array' }
    * def categoryKey = 'pse-group-6'
    * if (!isGatling)  testData = karate.callSingle('setupTestData.feature@category')
    * def categoryId = isGatling ? karate.get('categoryId') : testData.categoryId
    * def slug = isGatling ? karate.get('categorySlug') : testData.categorySlug

  @test @Performance
  Scenario: Get categories
    Given path '/aggregator/category'
    And header Authorization = 'Bearer ' + ACCESS_TOKEN_ADMIN
    And header karate-name = 'Get categories'
    When method GET
    Then status 200
    And match response contains responseSchema
    And def category = response.results[0]
    And match category contains { id: '#uuid', key: '#string', name: '#string', description: '#string', image: '#object'}

  Scenario: Get categories - invalid authorization
    Given path '/aggregator/category'
    And header Authorization = 'Bearer ' + 'invalidAccessToken'
    When method GET
    Then status 401

  @test @Performance
  Scenario: Get category tree by store
    Given path '/aggregator/category/tree'
    And header Authorization = 'Bearer ' + ACCESS_TOKEN_ADMIN
    And header karate-name = 'Get category tree by store'
    When method GET
    Then status 200
    And match response contains responseSchema

  @test
  Scenario: Get category tree by store - invalid authorization
    Given path '/aggregator/category/tree'
    And header Authorization = 'Bearer ' + 'invalidAccessToken'
    When method GET
    Then status 401

  Scenario: category by Key -  invalid category key
    Given path '/aggregator/category/key=UNKNOWN' + categoryKey
    And header Authorization = 'Bearer ' + ACCESS_TOKEN_ADMIN
    When method GET
    Then status 404

  Scenario: category by Key - invalid authorization
    Given path '/aggregator/category/key=' + categoryKey
    And header Authorization = 'Bearer ' + 'invalidAccessToken'
    When method GET
    Then status 401

  @Performance
  Scenario: category by Key
    Given path '/aggregator/category/key=' + categoryKey
    And header Authorization = 'Bearer ' + ACCESS_TOKEN_ADMIN
    And header karate-name = 'Get category details by key'
    When method GET
    Then status 200
    And match response contains categorySchema

  @Performance
  Scenario: category by slug
    Given path '/aggregator/category/slug=' + slug
    And header Authorization = 'Bearer ' + ACCESS_TOKEN_ADMIN
    And header karate-name = 'Get category details by slug'
    When method GET
    Then status 200
    And match response contains categorySchema

  Scenario: category by slug - invalid slug
    Given path '/aggregator/category/slug=' + 'invalid_slug'
    And header Authorization = 'Bearer ' + ACCESS_TOKEN_ADMIN
    When method GET
    Then status 404

  Scenario: category by slug - invalid authorization
    Given path '/aggregator/category/slug=' + slug
    And header Authorization = 'Bearer ' + 'invalidAccessToken'
    When method GET
    Then status 401

  Scenario: category by id - invalid category Id
    Given path '/aggregator/category/' + categoryId + 'UNKNOWN'
    And header Authorization = 'Bearer ' + ACCESS_TOKEN_ADMIN
    When method GET
    Then status 404

  Scenario: category by id - invalid authorization
    Given path '/aggregator/category/' + categoryId
    And header Authorization = 'Bearer ' + 'invalidAccessToken'
    When method GET
    Then status 401

  @Performance
  Scenario: category by id
    Given path '/aggregator/category/' + categoryId
    And header Authorization = 'Bearer ' + ACCESS_TOKEN_ADMIN
    And header karate-name = 'Get category details by id'
    When method GET
    Then status 200
    And match response contains categorySchema
