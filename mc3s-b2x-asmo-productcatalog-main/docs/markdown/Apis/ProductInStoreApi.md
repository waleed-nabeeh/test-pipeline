# ProductInStoreApi

All URIs are relative to *https://productcatalog.dev.b2x.b2bx.mindcurv.io*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**productInStoreById**](ProductInStoreApi.md#productInStoreById) | **GET** /aggregator/product-store/{id} |  |
| [**productInStoreByKey**](ProductInStoreApi.md#productInStoreByKey) | **GET** /aggregator/product-store/key&#x3D;{key} |  |
| [**productInStoreBySku**](ProductInStoreApi.md#productInStoreBySku) | **GET** /aggregator/product-store/sku&#x3D;{sku} |  |
| [**productInStoreBySlug**](ProductInStoreApi.md#productInStoreBySlug) | **GET** /aggregator/product-store/slug&#x3D;{slug} |  |


<a name="productInStoreById"></a>
# **productInStoreById**
> ProductAggregation productInStoreById(id)



    Product in store by id (scope: not required).

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **id** | **String**| Product id | [default to null] |

### Return type

[**ProductAggregation**](../Models/ProductAggregation.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

<a name="productInStoreByKey"></a>
# **productInStoreByKey**
> ProductAggregation productInStoreByKey(key)



    Product in store by key (scope: not required).

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **key** | **String**| Product key | [default to null] |

### Return type

[**ProductAggregation**](../Models/ProductAggregation.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

<a name="productInStoreBySku"></a>
# **productInStoreBySku**
> ProductAggregation productInStoreBySku(sku)



    Product in store by sku (scope: not required).

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **sku** | **String**| Sku | [default to null] |

### Return type

[**ProductAggregation**](../Models/ProductAggregation.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

<a name="productInStoreBySlug"></a>
# **productInStoreBySlug**
> ProductAggregation productInStoreBySlug(slug)



    Product in store by slug (scope: not required).

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **slug** | **String**| slug | [default to null] |

### Return type

[**ProductAggregation**](../Models/ProductAggregation.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

