# AttributeGroupApi

All URIs are relative to *https://productcatalog.dev.b2x.b2bx.mindcurv.io*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**attributeGroupById**](AttributeGroupApi.md#attributeGroupById) | **GET** /aggregator/attribute-group/{id} |  |
| [**attributeGroupByKey**](AttributeGroupApi.md#attributeGroupByKey) | **GET** /aggregator/attribute-group/key&#x3D;{key} |  |
| [**attributeGroups**](AttributeGroupApi.md#attributeGroups) | **GET** /aggregator/attribute-group |  |
| [**attributeGroupsForType**](AttributeGroupApi.md#attributeGroupsForType) | **GET** /aggregator/attribute-group/type&#x3D;{key} |  |


<a name="attributeGroupById"></a>
# **attributeGroupById**
> AttributeGroupAggregation attributeGroupById(id)



    AttributeGroup by id (scope: catalog).

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **id** | **String**| AttributeGroup id | [default to null] |

### Return type

[**AttributeGroupAggregation**](../Models/AttributeGroupAggregation.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

<a name="attributeGroupByKey"></a>
# **attributeGroupByKey**
> AttributeGroupAggregation attributeGroupByKey(key)



    AttributeGroup using the key (scope: catalog).

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **key** | **String**| AttributeGroup key | [default to null] |

### Return type

[**AttributeGroupAggregation**](../Models/AttributeGroupAggregation.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

<a name="attributeGroups"></a>
# **attributeGroups**
> PageableOfAttributeGroupAggregation attributeGroups(offset, limit)



    All AttributeGroups (scope: catalog).

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **offset** | **Long**| Page index | [optional] [default to 0] |
| **limit** | **Long**| Items per page | [optional] [default to 20] |

### Return type

[**PageableOfAttributeGroupAggregation**](../Models/PageableOfAttributeGroupAggregation.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

<a name="attributeGroupsForType"></a>
# **attributeGroupsForType**
> MapOfAttributeGroupAggregation attributeGroupsForType(key)



    All AttributeGroups for Product Type (scope: catalog).

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **key** | **String**| ProductType key | [default to null] |

### Return type

[**MapOfAttributeGroupAggregation**](../Models/MapOfAttributeGroupAggregation.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

