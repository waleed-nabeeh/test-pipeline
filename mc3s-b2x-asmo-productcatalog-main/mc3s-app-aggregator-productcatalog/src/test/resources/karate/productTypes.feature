Feature: Productcatalog Aggregator - product types

  Background:
    * url baseUrl
    * print baseUrl
    * def responseSchema = {offset: '#number', limit: '#number', total: '#number', totalPages: '#number', count: '#number', results: '#array'}
    * header X-Store = storeIdentifier
    * def typeKey = 'PseProduct'
    * if (!isGatling)  testData = karate.callSingle('setupTestData.feature@productTypes')
    * def productTypeId = isGatling ? karate.get('productTypeId') : testData.productTypeId

  @test @Performance
  Scenario: Get product types
    Given path '/aggregator/producttypes'
    And header Authorization = 'Bearer ' + ACCESS_TOKEN_ADMIN
    And header karate-name = 'Get product types'
    When method GET
    Then status 200
    And match response contains responseSchema
    And def singleEntry = response.results[0]
    And match singleEntry contains {id: '#uuid', key: '#string', name: '#string'}

  Scenario: Get product types with 'BUYER' permission only
    Given path '/aggregator/producttypes'
    And header Authorization = 'Bearer ' + ACCESS_TOKEN_BUYER
    When method GET
    Then status 200

  Scenario: Get product types with 'USER' permission [Failure:- 'USER' don't have permission]
    Given path '/aggregator/producttypes'
    And header Authorization = 'Bearer ' + ACCESS_TOKEN_USER
    When method GET
    Then status 403

  @test
  Scenario: Get product types [failure - unauthorized]
    Given path '/aggregator/producttypes'
    And header Authorization = 'Bearer ' + 'invalidAccessToken'
    When method GET
    Then status 401

  @test
  Scenario: Get product type by Key failed - no token
    Given path '/aggregator/producttypes/key=' + typeKey
    When method GET
    Then status 401

  Scenario: product type by Key failed - not found
    Given path '/aggregator/producttypes/key=' + typeKey + 'UNKOWN'
    And header Authorization = 'Bearer ' + ACCESS_TOKEN_ADMIN
    When method GET
    Then status 404

  @Performance
  Scenario: product type by Key
    Given path '/aggregator/producttypes/key=' + typeKey
    And header Authorization = 'Bearer ' + ACCESS_TOKEN_ADMIN
    And header karate-name = 'Get product type by Key'
    When method GET
    Then status 200
    And match response contains {id: '#uuid', key: '#(typeKey)', name: '#string', attributes: '#array'}
    And def attribute = response.attributes[0]
    And match attribute contains {name: '#string', label: '#string', inputTip: '#string', constraint: '#string', set: '#boolean', type: '#string'}

  Scenario: product type by id failed - no token
    Given path '/aggregator/producttypes/' + productTypeId
    When method GET
    Then status 401

  Scenario: product type by id failed - not found
    Given path '/aggregator/producttypes/' + productTypeId + 'UNKNOWN'
    And header Authorization = 'Bearer ' + ACCESS_TOKEN_ADMIN
    When method GET
    Then status 404

  @Performance
  Scenario: product type by id
    Given path '/aggregator/producttypes/' + productTypeId
    And header Authorization = 'Bearer ' + ACCESS_TOKEN_ADMIN
    And header karate-name = 'Get product type by Key'
    When method GET
    Then status 200
    And match response contains {id: '#uuid', key: '#(typeKey)', name: '#string', attributes: '#array'}
    And def attribute = response.attributes[0]
    And match attribute contains {name: '#string', label: '#string', inputTip: '#string', constraint: '#string', set: '#boolean', type: '#string'}

  Scenario: attributeMetaData by key failed - no token
    Given path '/aggregator/attributeMetaData/key=' + typeKey
    When method GET
    Then status 401

  Scenario: attributeMetaData by key failed - not found
    Given path '/aggregator/attributeMetaData/key=' + typeKey + 'UNKOWN'
    And header Authorization = 'Bearer ' + ACCESS_TOKEN_ADMIN
    When method GET
    Then status 200
    And match response contains {offset: '#number', limit: '#number', total: 0, totalPages: '#number', count: 0, results: '#array'}

  Scenario: attributeMetaData by key
    Given path '/aggregator/attributeMetaData/key=' + typeKey
    And header Authorization = 'Bearer ' + 'invalidAccessToken'
    When method GET
    Then status 401

  @Performance
  Scenario: attributeMetaData by key
    Given path '/aggregator/attributeMetaData/key=' + typeKey
    And header Authorization = 'Bearer ' + ACCESS_TOKEN_ADMIN
    And header karate-name = 'Get attributeMetaData by key'
    When method GET
    Then status 200
    And match response contains responseSchema
    And def singleEntry = response.results[0]
    And match singleEntry contains {group:'#string'}

  Scenario: attributeMetaData by id failed - no token
    Given path '/aggregator/attributeMetaData/' + productTypeId
    When method GET
    Then status 401

  Scenario: attributeMetaData by id failed - not found
    Given path '/aggregator/attributeMetaData/' + productTypeId + 'UNKOWN'
    And header Authorization = 'Bearer ' + ACCESS_TOKEN_ADMIN
    When method GET
    Then status 200
    And match response contains {offset: '#number', limit: '#number', total: 0, totalPages: '#number', count: 0, results: '#array'}

  @Performance
  Scenario: attributeMetaData by id
    Given path '/aggregator/attributeMetaData/' + productTypeId
    And header Authorization = 'Bearer ' + ACCESS_TOKEN_ADMIN
    And header karate-name = 'Get attributeMetaData by id'
    When method GET
    Then status 200
    And match response contains responseSchema
    And def singleEntry = response.results[0]
    And match singleEntry contains {group:'#string'}