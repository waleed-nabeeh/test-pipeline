# ListingApi

All URIs are relative to *https://productcatalog.dev.b2x.b2bx.mindcurv.io*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**listingByCart**](ListingApi.md#listingByCart) | **GET** /aggregator/listing/cart |  |
| [**listingById**](ListingApi.md#listingById) | **GET** /aggregator/listing/{id} |  |
| [**listingByKey**](ListingApi.md#listingByKey) | **GET** /aggregator/listing/key&#x3D;{key} |  |
| [**listingByOrder**](ListingApi.md#listingByOrder) | **GET** /aggregator/listing/order/{id} |  |
| [**listingByShoppinglist**](ListingApi.md#listingByShoppinglist) | **GET** /aggregator/listing/shoppinglist/{id} |  |
| [**listingBySku**](ListingApi.md#listingBySku) | **GET** /aggregator/listing/sku&#x3D;{sku} |  |
| [**listingsByReference**](ListingApi.md#listingsByReference) | **POST** /aggregator/listing |  |


<a name="listingByCart"></a>
# **listingByCart**
> PageableOfListingAggregation listingByCart(id)



    Product listing by cart (scope: cart).

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **id** | **String**| cart Id | [optional] [default to null] |

### Return type

[**PageableOfListingAggregation**](../Models/PageableOfListingAggregation.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

<a name="listingById"></a>
# **listingById**
> ListingAggregation listingById(id)



    Product listing by id (scope: not required).

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **id** | **String**| Product id | [default to null] |

### Return type

[**ListingAggregation**](../Models/ListingAggregation.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

<a name="listingByKey"></a>
# **listingByKey**
> ListingAggregation listingByKey(key)



    Product listing by key (scope: not required).

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **key** | **String**| Product key | [default to null] |

### Return type

[**ListingAggregation**](../Models/ListingAggregation.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

<a name="listingByOrder"></a>
# **listingByOrder**
> PageableOfListingAggregation listingByOrder(id)



    Product listing by order (scope: order).

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **id** | **String**| order id | [default to null] |

### Return type

[**PageableOfListingAggregation**](../Models/PageableOfListingAggregation.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

<a name="listingByShoppinglist"></a>
# **listingByShoppinglist**
> PageableOfListingAggregation listingByShoppinglist(id)



    Product Listing by shoppinglist (scope: shoppinglist).

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **id** | **String**| shoppinglist id | [default to null] |

### Return type

[**PageableOfListingAggregation**](../Models/PageableOfListingAggregation.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

<a name="listingBySku"></a>
# **listingBySku**
> ListingAggregation listingBySku(sku)



    Product listing by sku (scope: not required).

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **sku** | **String**| SKU | [default to null] |

### Return type

[**ListingAggregation**](../Models/ListingAggregation.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

<a name="listingsByReference"></a>
# **listingsByReference**
> PageableOfListingAggregation listingsByReference(Reference)



    Product Listings (scope: not required).

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **Reference** | [**List**](../Models/Reference.md)|  | |

### Return type

[**PageableOfListingAggregation**](../Models/PageableOfListingAggregation.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

