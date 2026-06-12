Feature: Productcatalog Aggregator - listing

  Background:
    * header X-Store = storeIdentifier
    * def responseSchema = {offset: '#number', limit: '#number', total: '#number', totalPages: '#number', count: '#number', results: '#array'}
    * def listingSchema = { store: '#string', reference: '#object', listed: '#boolean', listings: '#array' }
    * if (!isGatling)  testData = karate.callSingle('setupTestData.feature@listing')
    * def productId = isGatling ? karate.get('listingProductId') : testData.listingProductId
    * def productKey = isGatling ? karate.get('listingProductKey') : testData.listingProductKey
    * def sku = 'pse-6_2'
    * if (!isGatling)  testData = karate.callSingle('setupTestData.feature@cart')
    * def current_cartId = isGatling ? karate.get('cartId') : testData.cartId
    * if (!isGatling)  testData = karate.callSingle('setupTestData.feature@order')
    * def orderId = isGatling ? karate.get('orderId') : testData.orderId
    * if (!isGatling)  testData = karate.callSingle('setupTestData.feature@shoppingList')
    * def shoppingListId = isGatling ? karate.get('shoppingListId') : testData.shoppingListId
    * url baseUrl

  @test @Performance
  Scenario: Product listing
    Given path '/aggregator/listing'
    And header karate-name = 'Product Listings for store:post'
    When request [{'key': '#(productKey)'}]
    And method POST
    Then status 200
    And match response contains responseSchema
    And def listing = response.results[0]
    And match listing contains listingSchema
    And def reponseProductKey = listing.reference.key
    And match reponseProductKey contains '#(productKey)'

  Scenario: Product listing - missing request body
    Given path '/aggregator/listing'
    And header Authorization = 'Bearer ' + ACCESS_TOKEN_ADMIN
    When method POST
    Then status 400

  @Performance
  Scenario: Product listing by id
    Given path '/aggregator/listing/' + productId
    And header karate-name = 'Get Product listing by id'
    When method GET
    Then status 200
    And match response contains {store: '#string', listed: '#boolean', listings: '#array'}
    And def reponseProductId = response.reference.id
    And match reponseProductId contains '#(productId)'

  Scenario: Product listing by id - invalid product Id
    * def invalid_productId = 'karate-1234'
    Given path '/aggregator/listing/' + invalid_productId
    And header Authorization = 'Bearer ' + ACCESS_TOKEN_ADMIN
    When method GET
    Then status 200
    And match response.listed == false

  @Performance
  Scenario: Product listing by key
    Given path '/aggregator/listing/key=' + productKey
    And header Authorization = 'Bearer ' + ACCESS_TOKEN_ADMIN
    And header karate-name = 'Get Product listing by key'
    When method GET
    Then status 200
    And match response contains listingSchema
    And def reponseProductKey = response.reference.key
    And match reponseProductKey contains '#(productKey)'

  Scenario: Product listing by key - invalid product key
    * def invalid_productKey = 'karate-1234'
    Given path '/aggregator/listing/key=' + invalid_productKey
    When method GET
    Then status 200
    And match response.listed == false

  @Performance
  Scenario: Product listing by sku
    Given path '/aggregator/listing/sku=' + sku
    And header karate-name = 'Get Product listing sku'
    When method GET
    Then status 200
    And match response contains listingSchema

  Scenario:Product listing by sku - invalid sku
    Given path '/aggregator/listing/sku=Unknown'
    When method GET
    Then status 200
    And match response.listed == false

  @test @Performance
  Scenario: listings for Cart
    Given path '/aggregator/listing/cart'
    And header Authorization = 'Bearer ' + ACCESS_TOKEN_ADMIN
    And header karate-name = 'Get Product listing by cart'
    When method GET
    Then status 200
    And match response contains responseSchema

  Scenario: listings for Cart  [Failure - Unauthorized]
    Given path '/aggregator/listing/cart'
    And header Authorization = 'Bearer ' + 'invalidAccessToken'
    When method GET
    Then status 401

  @test @Performance
  Scenario: listings for Cart by id
    Given path '/aggregator/listing/cart'
    And param id = current_cartId
    And header Authorization = 'Bearer ' + ACCESS_TOKEN_ADMIN
    And header karate-name = 'Get Product listing for cart by id'
    When method GET
    Then status 200
    And match response contains responseSchema

  Scenario: listings for Cart by id [Failed - Unauthorized]
    Given path '/aggregator/listing/cart'
    And param id = current_cartId
    And header Authorization = 'Bearer ' + 'invalidAccessToken'
    When method GET
    Then status 401

  Scenario: listings for Cart by id - invalid cart Id
    Given path '/aggregator/listing/cart'
    And param id = 'invalid_cartId'
    And header Authorization = 'Bearer ' + ACCESS_TOKEN_ADMIN
    When method GET
    Then status 200
    And match response.results == []

  @test @Performance
  Scenario: listings for order by id
    Given path '/aggregator/listing/order/' + orderId
    And header Authorization = 'Bearer ' + ACCESS_TOKEN_ADMIN
    And header karate-name = 'Get Product listing for order by id'
    When method GET
    Then status 200
    And match response contains responseSchema
    And def listing = response.results[0]
    And match listing contains listingSchema

  Scenario: listings for order by id [Failure - Unauthorized]
    Given path '/aggregator/listing/order/' + orderId
    And header Authorization = 'Bearer ' + 'invalidAccessToken'
    When method GET
    Then status 401

  Scenario: listings for order by id - invalid order Id
    Given path '/aggregator/listing/order/' + 'invalid_orderId'
    And header Authorization = 'Bearer ' + ACCESS_TOKEN_ADMIN
    When method GET
    Then status 200
    And match response.results == []

  @test @Performance
  Scenario: listings for shoppinglist by id
    Given path '/aggregator/listing/shoppinglist/' + shoppingListId
    And header Authorization = 'Bearer ' + ACCESS_TOKEN_ADMIN
    And header karate-name = 'Get Product listing for shoppinglist by id'
    When method GET
    Then status 200
    And match response contains responseSchema

  Scenario: listings for shoppinglist by id [Failure - Unauthorized]
    Given path '/aggregator/listing/shoppinglist/' + shoppingListId
    And header Authorization = 'Bearer ' + 'invalidAccessToken'
    When method GET
    Then status 401

  Scenario: listings for shoppinglist by id - invalid shopping list Id
    Given path '/aggregator/listing/shoppinglist/' + 'invalid_shoppingListId'
    And header Authorization = 'Bearer ' + ACCESS_TOKEN_ADMIN
    When method GET
    Then status 200
    And match response.results == []
