Feature: Productcatalog Aggregator - product

  Background:
    * url baseUrl
    * print baseUrl
    * header X-Store = storeIdentifier
    * def responseSchema = {offset: '#number', limit: '#number', total: '#number', totalPages: '#number', count: '#number', results: '#array'}
    * def productSchema = { id: '#uuid', key: '#string', name: '#string', description: '#string', categories: '#array', variants: '#array', metaData: '#object', type: '#object' }
    * def variantSchema = { id: '#number', key: '#string', sku: '#string', defaultVariant: '#boolean', images: '#array', assets: '#array', attributes: '#array' }
    * if (!isGatling)  testData = karate.callSingle('setupTestData.feature@product')
    * def productId = isGatling ? karate.get('productId') : testData.productId
    * def productKey = isGatling ? karate.get('productKey') : testData.productKey
    * def slug = isGatling ? karate.get('productSlug') : testData.productSlug
    * def sku = 'pse-6_2'

  @test @Performance
  Scenario: Get Products
    Given path '/aggregator/product'
    And header Authorization = 'Bearer ' + ACCESS_TOKEN_ADMIN
    And header karate-name = 'Get Products'
    When method GET
    Then status 200
    And match response contains responseSchema
    And def singleEntry = response.results[0]
    And match singleEntry contains productSchema
    And def variant = singleEntry.variants[0]
    And match variant contains variantSchema

  @test @Performance
  Scenario: Get product by SKU
    Given path '/aggregator/product/sku=' + sku
    And header Authorization = 'Bearer ' + ACCESS_TOKEN_ADMIN
    And header karate-name = 'Get product by SKU'
    When method GET
    Then status 200
    And match response contains productSchema
    And def variant = response.variants[0]
    * print variant
    And match variant contains variantSchema

  @Performance
  Scenario: Get product by key
    Given path '/aggregator/product/key=' + productKey
    And header Authorization = 'Bearer ' + ACCESS_TOKEN_ADMIN
    And header karate-name = 'Get product by key'
    When method GET
    Then status 200
    And match response contains productSchema
    And def variant = response.variants[0]
    And match variant contains variantSchema

  @Performance
  Scenario: Get product by Product ID
    Given path '/aggregator/product/' + productId
    And header Authorization = 'Bearer ' + ACCESS_TOKEN_ADMIN
    And header karate-name = 'Get product by Product ID'
    When method GET
    Then status 200
    And match response contains productSchema
    And def variant = response.variants[0]
    And match variant contains variantSchema

  @Performance
  Scenario: Get product by slug
    Given path '/aggregator/product/slug=' + slug
    And header Authorization = 'Bearer ' + ACCESS_TOKEN_ADMIN
    And header karate-name = 'Get product by slug'
    When method GET
    Then status 200
    And match response contains productSchema
    And def variant = response.variants[0]
    And match variant contains variantSchema

  @test
  Scenario Outline: Get product by <type> [negative - Unauthorized]
    Given path '/aggregator/product' + <suffix>
    And header Authorization = 'Bearer ' + ' invalidAccessToken'
    When method GET
    Then status 401

    Examples:
      | type       | suffix               |
      | Products   | ''                   |
      | SKU        | '/sku=' + sku        |
      | key        | '/key=' + productKey |
      | Product ID | '/' + productId      |
      | slug       | '/slug=' + slug      |

  @test
  Scenario Outline: Get product by <type> with failure response
    Given path '/aggregator/product/<suffix>'
    And header Authorization = 'Bearer ' + ACCESS_TOKEN_ADMIN
    When method GET
    Then status 404

    Examples:
      | type | suffix          |
      | sku  | sku=UNKNOWN     |
      | key  | key=UNKNOWN     |
      | id   | UNKNOWN         |
      | slug | slug=unknowSlug |