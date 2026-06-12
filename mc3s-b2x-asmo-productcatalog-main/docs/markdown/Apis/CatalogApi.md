# CatalogApi

All URIs are relative to *https://productcatalog.dev.b2x.b2bx.mindcurv.io*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**catalogs**](CatalogApi.md#catalogs) | **GET** /aggregator/catalog |  |


<a name="catalogs"></a>
# **catalogs**
> PageableOfCatalogAggregation catalogs(offset, limit)



    All catalogs for context (scope: not required).

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **offset** | **Long**| Page index | [optional] [default to 0] |
| **limit** | **Long**| Items per page | [optional] [default to 20] |

### Return type

[**PageableOfCatalogAggregation**](../Models/PageableOfCatalogAggregation.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

