# CategoryApi

All URIs are relative to *https://productcatalog.dev.b2x.b2bx.mindcurv.io*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**categories**](CategoryApi.md#categories) | **GET** /aggregator/category |  |
| [**categoryById**](CategoryApi.md#categoryById) | **GET** /aggregator/category/{id} |  |
| [**categoryByKey**](CategoryApi.md#categoryByKey) | **GET** /aggregator/category/key&#x3D;{key} |  |
| [**categoryBySlug**](CategoryApi.md#categoryBySlug) | **GET** /aggregator/category/slug&#x3D;{slug} |  |
| [**categoryTree**](CategoryApi.md#categoryTree) | **GET** /aggregator/category/tree |  |


<a name="categories"></a>
# **categories**
> PageableOfCategoryAggregation categories(offset, limit)



    All categories (scope: not required).

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **offset** | **Long**| Page index | [optional] [default to 0] |
| **limit** | **Long**| Items per page | [optional] [default to 20] |

### Return type

[**PageableOfCategoryAggregation**](../Models/PageableOfCategoryAggregation.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

<a name="categoryById"></a>
# **categoryById**
> CategoryAggregation categoryById(id)



    Category by id (scope: not required).

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **id** | **String**| Category id | [default to null] |

### Return type

[**CategoryAggregation**](../Models/CategoryAggregation.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

<a name="categoryByKey"></a>
# **categoryByKey**
> CategoryAggregation categoryByKey(key)



    Category by key (scope: not required).

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **key** | **String**| Category key | [default to null] |

### Return type

[**CategoryAggregation**](../Models/CategoryAggregation.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

<a name="categoryBySlug"></a>
# **categoryBySlug**
> CategoryAggregation categoryBySlug(slug)



    Category by slug (scope: not required).

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **slug** | **String**| Category slug | [default to null] |

### Return type

[**CategoryAggregation**](../Models/CategoryAggregation.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

<a name="categoryTree"></a>
# **categoryTree**
> PageableOfCategoryNavigationAggregation categoryTree()



    Category tree for store (scope: not required). The level amount (default &#x3D; 2) is configured on Store.navigationLevels.

### Parameters
This endpoint does not need any parameter.

### Return type

[**PageableOfCategoryNavigationAggregation**](../Models/PageableOfCategoryNavigationAggregation.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

