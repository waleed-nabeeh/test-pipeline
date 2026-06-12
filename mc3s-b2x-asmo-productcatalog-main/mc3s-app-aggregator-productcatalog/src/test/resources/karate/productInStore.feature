Feature: Productcatalog Aggregator - productInStore

  Background:
    * url baseUrl
    * print baseUrl
    * header X-Store = storeIdentifier
    * def productSchema = { id: '#uuid', key: '#string', name: '#string', description: '#string', categories: '#array', variants: '#array', metaData: '#object', type: '#object' }
    * def variantSchema = { id: '#number', key: '#string', sku: '#string', defaultVariant: '#boolean', images: '#array', assets: '#array', attributes: '#array' }
    * if (!isGatling)  testData = karate.callSingle('setupTestData.feature@productInStore')
    * def productId = isGatling ? karate.get('productIdInStore') : testData.productIdInStore
    * def productKey = isGatling ? karate.get('productKeyInStore') : testData.productKeyInStore
    * def slug = isGatling ? karate.get('productInStoreSlug') : testData.productInStoreSlug
    * def sku = 'pse-6_2'

  @test @Performance
  Scenario: Get product in store by SKU
    Given path '/aggregator/product-store/sku=' + sku
    And header Authorization = 'Bearer ' + ACCESS_TOKEN_ADMIN
    And header karate-name = 'Get product in store by SKU'
    When method GET
    Then status 200
    And match response contains productSchema
    And def variant = response.variants[0]
    And match variant contains variantSchema

  @Performance
  Scenario: Get product in store by key
    Given path '/aggregator/product-store/key=' + productKey
    And header Authorization = 'Bearer ' + ACCESS_TOKEN_ADMIN
    And header karate-name = 'Get product in store by key'
    When method GET
    Then status 200
    And match response contains productSchema
    And def variant = response.variants[0]
    And match variant contains variantSchema

  @Performance
  Scenario: Get product in store by ID
    Given path '/aggregator/product-store/' + productId
    And header Authorization = 'Bearer ' + ACCESS_TOKEN_ADMIN
    And header karate-name = 'Get product in store by id'
    When method GET
    Then status 200
    And match response contains productSchema
    And def variant = response.variants[0]
    And match variant contains variantSchema

  @Performance
  Scenario: Get product in store by slug
    Given path '/aggregator/product-store/slug=' + slug
    And header Authorization = 'Bearer ' + ACCESS_TOKEN_ADMIN
    And header karate-name = 'Get product in store by slug'
    When method GET
    Then status 200
    And match response contains productSchema
    And def variant = response.variants[0]
    And match variant contains variantSchema

  @test
  Scenario Outline: Get product in store by <type> [failure - Unauthorized]
    Given path '/aggregator/product-store' + <suffix>
    And header Authorization = 'Bearer ' + 'invalidAccessToken'
    When method GET
    Then status 401

    Examples:
      | type | suffix               |
      | SKU  | '/sku=' + sku        |
      | key  | '/key=' + productKey |
      | ID   | '/' + productId      |
      | slug | '/slug=' + slug      |

  @test
  Scenario Outline: Get product in store by <type> with failure response [invalid parameter]
    Given path '/aggregator/product-store/<suffix>'
    And header Authorization = 'Bearer ' + ACCESS_TOKEN_ADMIN
    When method GET
    Then status 404

    Examples:
      | type | suffix      |
      | sku  | sku=unknown |
      | key  | key=unknown |
      | id   | unknown     |
      | slug | unknown     |