# ProductTypesApi

All URIs are relative to *https://productcatalog.dev.b2x.b2bx.mindcurv.io*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**productAttributeMetaDataById**](ProductTypesApi.md#productAttributeMetaDataById) | **GET** /aggregator/attributeMetaData/{id} |  |
| [**productAttributeMetaDataByKey**](ProductTypesApi.md#productAttributeMetaDataByKey) | **GET** /aggregator/attributeMetaData/key&#x3D;{key} |  |
| [**productTypeById**](ProductTypesApi.md#productTypeById) | **GET** /aggregator/producttypes/{id} |  |
| [**productTypeByKey**](ProductTypesApi.md#productTypeByKey) | **GET** /aggregator/producttypes/key&#x3D;{key} |  |
| [**productTypes**](ProductTypesApi.md#productTypes) | **GET** /aggregator/producttypes |  |


<a name="productAttributeMetaDataById"></a>
# **productAttributeMetaDataById**
> PageableOfAttributeMetaData productAttributeMetaDataById(id)



    ProductType Attribute MetaData by Id (scope: catalog).

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **id** | **String**| Product Type ID | [default to null] |

### Return type

[**PageableOfAttributeMetaData**](../Models/PageableOfAttributeMetaData.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

<a name="productAttributeMetaDataByKey"></a>
# **productAttributeMetaDataByKey**
> PageableOfAttributeMetaData productAttributeMetaDataByKey(key)



    ProductType Attribute MetaData by key (scope: not required).

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **key** | **String**| Product Type Key | [default to null] |

### Return type

[**PageableOfAttributeMetaData**](../Models/PageableOfAttributeMetaData.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

<a name="productTypeById"></a>
# **productTypeById**
> ProductTypeAggregation productTypeById(id)



    Product Type by id (scope: catalog).

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **id** | **String**| Product Type ID | [default to null] |

### Return type

[**ProductTypeAggregation**](../Models/ProductTypeAggregation.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

<a name="productTypeByKey"></a>
# **productTypeByKey**
> ProductTypeAggregation productTypeByKey(key)



    Product Type by key (scope: catalog).

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **key** | **String**| Product Type key | [default to null] |

### Return type

[**ProductTypeAggregation**](../Models/ProductTypeAggregation.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

<a name="productTypes"></a>
# **productTypes**
> PageableOfProductTypeAggregation productTypes()



    All product types (scope: catalog).

### Parameters
This endpoint does not need any parameter.

### Return type

[**PageableOfProductTypeAggregation**](../Models/PageableOfProductTypeAggregation.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

