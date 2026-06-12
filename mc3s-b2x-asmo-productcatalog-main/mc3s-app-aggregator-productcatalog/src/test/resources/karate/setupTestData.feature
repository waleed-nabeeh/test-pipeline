Feature: Setup Test Data

  Background:
    * url baseUrl

  @productTypes
  Scenario: Get product type by key
    * def typeKey = 'PseProduct'
    Given path '/aggregator/producttypes/key=' + typeKey
    And header Authorization = 'Bearer ' + ACCESS_TOKEN_ADMIN
    And header karate-name = 'Setup: Get product type by key'
    When method GET
    Then status 200
    * karate.set('productTypeId', response.id)

  @product
  Scenario: Get product by SKU
    * def productSku = 'pse-6_2'
    Given path '/aggregator/product/sku=' + productSku
    And header Authorization = 'Bearer ' + ACCESS_TOKEN_ADMIN
    And header karate-name = 'Setup: Get product by SKU'
    When method GET
    Then status 200
    * karate.set('productId', response.id)
    * karate.set('productKey', response.key)
    * karate.set('productSlug', response.metaData.slug)

  @productInStore
  Scenario: Get product in store by SKU
    Given path '/aggregator/product-store/sku=pse-55_1'
    And header X-Store = storeIdentifier
    And header Authorization = 'Bearer ' + ACCESS_TOKEN_ADMIN
    And header karate-name = 'Get product in store by SKU'
    When method GET
    Then status 200
    * karate.set('productIdInStore', response.id)
    * karate.set('productKeyInStore', response.key)
    * karate.set('productInStoreSlug',  response.metaData.slug)

  @category
  Scenario: Setup Category Data by Key
    * def categoryKey = 'pse-group-6'
    Given path '/aggregator/category/key=' + categoryKey
    And header X-Store = storeIdentifier
    And header Authorization = 'Bearer ' + ACCESS_TOKEN_ADMIN
    And header karate-name = 'Setup: Get Category Data by Key'
    When method GET
    Then status 200
    * karate.set('categoryId', response.id)
    * karate.set('categorySlug', response.metaData.slug)

  @attributeGroups
  Scenario: Setup AttributeGroup Data
    Given path '/aggregator/attribute-group'
    And header X-Store = storeIdentifier
    And header karate-name = 'Setup: Get All AttributeGroups'
    And header Authorization = 'Bearer ' + ACCESS_TOKEN_ADMIN
    When method GET
    Then status 200
    * karate.set('attributeGroupID', response.results[0].id)
    * karate.set('attributeGroupKey', response.results[0].key)

  @listing
  Scenario: Get product listing
    Given path '/aggregator/product'
    And header X-Store = storeIdentifier
    And header Authorization = 'Bearer ' + ACCESS_TOKEN_ADMIN
    And header karate-name = 'Setup: Get product listing'
    When method GET
    Then status 200
    And def singleEntry = response.results[0]
    And match singleEntry contains {key: '#string'}
    And match singleEntry contains {id: '#uuid'}
    * karate.set('listingProductKey', singleEntry.key)
    * karate.set('listingProductId', singleEntry.id)

  @cart
  Scenario: Get cart id
    * url checkoutUrl
    Given path '/aggregator/cart'
    And header Authorization = 'Bearer ' + ACCESS_TOKEN_ADMIN
    And header X-Store = storeIdentifier
    And header karate-name = 'Setup: Get cart id'
    When method GET
    Then status 200
    * karate.set('cartId' , response.id)

  @order
  Scenario: Get order ID from list
    * url ordermanagementUrl
    Given path '/aggregator/orders'
    And header Authorization = 'Bearer ' + ACCESS_TOKEN_ADMIN
    And header X-Store = storeIdentifier
    And header karate-name = 'Setup: Get order ID from list'
    When method GET
    Then status 200
    * karate.set('orderId' , response.results[0].id)

  @shoppingList
  Scenario: Get shopping list id
    * url customerUrl
    Given path '/aggregator/customer/shoppinglist'
    And header X-Store = storeIdentifier
    And header Authorization = 'Bearer ' + ACCESS_TOKEN_ADMIN
    And header karate-name = 'Setup: Get shopping list by id'
    When method GET
    Then status 200
    * karate.set('shoppingListId' , response.results[0].id)