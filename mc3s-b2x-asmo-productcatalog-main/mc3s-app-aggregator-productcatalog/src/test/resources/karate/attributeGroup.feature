Feature:APIs to fetch product AttributeGroup information

  Background:
    * url baseUrl
    * print baseUrl
    * header X-Store = storeIdentifier
    * def attributeSchema = {id: '#string', key: '#string', name: '#string', description: '#string', attributes: '#array'}
    * if (!isGatling)  testData = karate.callSingle('setupTestData.feature@attributeGroups')
    * def attributeGroupID = isGatling ?karate.get('attributeGroupID') : testData.attributeGroupID
    * def attributeGroupKey = isGatling ? karate.get('attributeGroupKey') : testData.attributeGroupKey
    * def productTypeKey = 'EclassProduct'

  Scenario: All AttributeGroups
    Given path '/aggregator/attribute-group'
    And header  Authorization = 'Bearer ' + ACCESS_TOKEN_ADMIN
    When method GET
    Then status 200
    And match response contains {offset: '#number', limit: '#number', total: '#number? _ > 0', totalPages: '#number', count: '#number? _ > 0', results: '#array'}
    And def singleEntry = response.results[0]
    And match singleEntry contains attributeSchema

  Scenario: All AttributeGroups [Failure - Unauthorized]
    Given path '/aggregator/attribute-group'
    And header  Authorization = 'Bearer ' + 'invalidAccessToken'
    When method GET
    Then status 401

  @Performance
  Scenario: All AttributeGroups by ID
    Given path '/aggregator/attribute-group/' + attributeGroupID
    And header  Authorization = 'Bearer ' + ACCESS_TOKEN_ADMIN
    And header karate-name = 'Get AttributeGroup by id'
    When method GET
    Then status 200
    And match response contains attributeSchema

  Scenario: All AttributeGroups by ID [Failure- Unauthorized]
    Given path '/aggregator/attribute-group/' + attributeGroupID
    And header  Authorization = 'Bearer ' + 'invalidAccessToken'
    When method GET
    Then status 401

  Scenario: All AttributeGroups by ID [Failure - invalid atrribute group ID]
    Given path '/aggregator/attribute-group/unkown' + attributeGroupID
    And header  Authorization = 'Bearer ' + ACCESS_TOKEN_ADMIN
    When method GET
    Then status 404

  @Performance
  Scenario: All AttributeGroups for Product Type
    Given path '/aggregator/attribute-group/key=' + attributeGroupKey
    And header  Authorization = 'Bearer ' + ACCESS_TOKEN_ADMIN
    And header karate-name = 'Get AttributeGroup for Product key'
    When method GET
    Then status 200
    And match response contains attributeSchema

  Scenario: All AttributeGroups for Product Type [Failure - Unauthorized]
    Given path '/aggregator/attribute-group/key=' + attributeGroupKey
    And header  Authorization = 'Bearer ' + 'invalidAccessToken'
    When method GET
    Then status 401

  Scenario: All AttributeGroups for Product Type [Failure - Invalid attribute group key]
    Given path '/aggregator/attribute-group/key=unknown' + attributeGroupKey
    And header  Authorization = 'Bearer ' + ACCESS_TOKEN_ADMIN
    When method GET
    Then status 404

  @Performance
  Scenario: All AttributeGroups for Product Type key
    Given path '/aggregator/attribute-group/type=' + productTypeKey
    And header  Authorization = 'Bearer ' + ACCESS_TOKEN_ADMIN
    And header karate-name = 'Get AttributeGroup for Product type'
    When method GET
    Then status 200

  Scenario: All AttributeGroups for Product Type key [failure- Unauthorized]
    Given path '/aggregator/attribute-group/type=' + productTypeKey
    And header  Authorization = 'Bearer ' + 'invalidAccessToken'
    When method GET
    Then status 401

  Scenario: All AttributeGroups for Product Type key [failure- invalid product type key]
    Given path '/aggregator/attribute-group/type=1234' + productTypeKey
    And header  Authorization = 'Bearer ' + ACCESS_TOKEN_ADMIN
    When method GET
    Then status 200
    And match response == {}