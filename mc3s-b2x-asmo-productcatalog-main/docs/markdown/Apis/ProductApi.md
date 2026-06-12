# ProductApi

All URIs are relative to *https://productcatalog.dev.b2x.b2bx.mindcurv.io*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**productById**](ProductApi.md#productById) | **GET** /aggregator/product/{id} |  |
| [**productByKey**](ProductApi.md#productByKey) | **GET** /aggregator/product/key&#x3D;{key} |  |
| [**productBySku**](ProductApi.md#productBySku) | **GET** /aggregator/product/sku&#x3D;{sku} |  |
| [**productBySlug**](ProductApi.md#productBySlug) | **GET** /aggregator/product/slug&#x3D;{slug} |  |
| [**products**](ProductApi.md#products) | **GET** /aggregator/product |  |


<a name="productById"></a>
# **productById**
> ProductAggregation productById(id)



    Product by id (scope: not required).

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

<a name="productByKey"></a>
# **productByKey**
> ProductAggregation productByKey(key)



    Product by key (scope: not required).

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

<a name="productBySku"></a>
# **productBySku**
> ProductAggregation productBySku(sku)



    Product by sku (scope: not required).

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **sku** | **String**| Sku id | [default to null] |

### Return type

[**ProductAggregation**](../Models/ProductAggregation.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

<a name="productBySlug"></a>
# **productBySlug**
> ProductAggregation productBySlug(slug)



    Product by slug (scope: not required).

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **slug** | **String**| Product slug | [default to null] |

### Return type

[**ProductAggregation**](../Models/ProductAggregation.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

<a name="products"></a>
# **products**
> PageableOfProductAggregation products(offset, limit)



    All products (scope: not required).

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **offset** | **Long**| Page index | [optional] [default to 0] |
| **limit** | **Long**| Items per page | [optional] [default to 20] |

### Return type

[**PageableOfProductAggregation**](../Models/PageableOfProductAggregation.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

