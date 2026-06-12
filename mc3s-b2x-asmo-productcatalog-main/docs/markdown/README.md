# Documentation for API - mc3s-b2x-productcatalog

<a name="documentation-for-api-endpoints"></a>
## Documentation for API Endpoints

All URIs are relative to *https://productcatalog.dev.b2x.b2bx.mindcurv.io*

| Class | Method | HTTP request | Description |
|------------ | ------------- | ------------- | -------------|
| *AttributeGroupApi* | [**attributeGroupById**](Apis/AttributeGroupApi.md#attributeGroupById) | **GET** /aggregator/attribute-group/{id} | AttributeGroup by id (scope: catalog). |
*AttributeGroupApi* | [**attributeGroupByKey**](Apis/AttributeGroupApi.md#attributeGroupByKey) | **GET** /aggregator/attribute-group/key&#x3D;{key} | AttributeGroup using the key (scope: catalog). |
*AttributeGroupApi* | [**attributeGroups**](Apis/AttributeGroupApi.md#attributeGroups) | **GET** /aggregator/attribute-group | All AttributeGroups (scope: catalog). |
*AttributeGroupApi* | [**attributeGroupsForType**](Apis/AttributeGroupApi.md#attributeGroupsForType) | **GET** /aggregator/attribute-group/type&#x3D;{key} | All AttributeGroups for Product Type (scope: catalog). |
| *CatalogApi* | [**catalogs**](Apis/CatalogApi.md#catalogs) | **GET** /aggregator/catalog | All catalogs for context (scope: not required). |
| *CategoryApi* | [**categories**](Apis/CategoryApi.md#categories) | **GET** /aggregator/category | All categories (scope: not required). |
*CategoryApi* | [**categoryById**](Apis/CategoryApi.md#categoryById) | **GET** /aggregator/category/{id} | Category by id (scope: not required). |
*CategoryApi* | [**categoryByKey**](Apis/CategoryApi.md#categoryByKey) | **GET** /aggregator/category/key&#x3D;{key} | Category by key (scope: not required). |
*CategoryApi* | [**categoryBySlug**](Apis/CategoryApi.md#categoryBySlug) | **GET** /aggregator/category/slug&#x3D;{slug} | Category by slug (scope: not required). |
*CategoryApi* | [**categoryTree**](Apis/CategoryApi.md#categoryTree) | **GET** /aggregator/category/tree | Category tree for store (scope: not required). The level amount (default = 2) is configured on Store.navigationLevels. |
| *ListingApi* | [**listingByCart**](Apis/ListingApi.md#listingByCart) | **GET** /aggregator/listing/cart | Product listing by cart (scope: cart). |
*ListingApi* | [**listingById**](Apis/ListingApi.md#listingById) | **GET** /aggregator/listing/{id} | Product listing by id (scope: not required). |
*ListingApi* | [**listingByKey**](Apis/ListingApi.md#listingByKey) | **GET** /aggregator/listing/key&#x3D;{key} | Product listing by key (scope: not required). |
*ListingApi* | [**listingByOrder**](Apis/ListingApi.md#listingByOrder) | **GET** /aggregator/listing/order/{id} | Product listing by order (scope: order). |
*ListingApi* | [**listingByShoppinglist**](Apis/ListingApi.md#listingByShoppinglist) | **GET** /aggregator/listing/shoppinglist/{id} | Product Listing by shoppinglist (scope: shoppinglist). |
*ListingApi* | [**listingBySku**](Apis/ListingApi.md#listingBySku) | **GET** /aggregator/listing/sku&#x3D;{sku} | Product listing by sku (scope: not required). |
*ListingApi* | [**listingsByReference**](Apis/ListingApi.md#listingsByReference) | **POST** /aggregator/listing | Product Listings (scope: not required). |
| *ProductApi* | [**productById**](Apis/ProductApi.md#productById) | **GET** /aggregator/product/{id} | Product by id (scope: not required). |
*ProductApi* | [**productByKey**](Apis/ProductApi.md#productByKey) | **GET** /aggregator/product/key&#x3D;{key} | Product by key (scope: not required). |
*ProductApi* | [**productBySku**](Apis/ProductApi.md#productBySku) | **GET** /aggregator/product/sku&#x3D;{sku} | Product by sku (scope: not required). |
*ProductApi* | [**productBySlug**](Apis/ProductApi.md#productBySlug) | **GET** /aggregator/product/slug&#x3D;{slug} | Product by slug (scope: not required). |
*ProductApi* | [**products**](Apis/ProductApi.md#products) | **GET** /aggregator/product | All products (scope: not required). |
| *ProductInStoreApi* | [**productInStoreById**](Apis/ProductInStoreApi.md#productInStoreById) | **GET** /aggregator/product-store/{id} | Product in store by id (scope: not required). |
*ProductInStoreApi* | [**productInStoreByKey**](Apis/ProductInStoreApi.md#productInStoreByKey) | **GET** /aggregator/product-store/key&#x3D;{key} | Product in store by key (scope: not required). |
*ProductInStoreApi* | [**productInStoreBySku**](Apis/ProductInStoreApi.md#productInStoreBySku) | **GET** /aggregator/product-store/sku&#x3D;{sku} | Product in store by sku (scope: not required). |
*ProductInStoreApi* | [**productInStoreBySlug**](Apis/ProductInStoreApi.md#productInStoreBySlug) | **GET** /aggregator/product-store/slug&#x3D;{slug} | Product in store by slug (scope: not required). |
| *ProductTypesApi* | [**productAttributeMetaDataById**](Apis/ProductTypesApi.md#productAttributeMetaDataById) | **GET** /aggregator/attributeMetaData/{id} | ProductType Attribute MetaData by Id (scope: catalog). |
*ProductTypesApi* | [**productAttributeMetaDataByKey**](Apis/ProductTypesApi.md#productAttributeMetaDataByKey) | **GET** /aggregator/attributeMetaData/key&#x3D;{key} | ProductType Attribute MetaData by key (scope: not required). |
*ProductTypesApi* | [**productTypeById**](Apis/ProductTypesApi.md#productTypeById) | **GET** /aggregator/producttypes/{id} | Product Type by id (scope: catalog). |
*ProductTypesApi* | [**productTypeByKey**](Apis/ProductTypesApi.md#productTypeByKey) | **GET** /aggregator/producttypes/key&#x3D;{key} | Product Type by key (scope: catalog). |
*ProductTypesApi* | [**productTypes**](Apis/ProductTypesApi.md#productTypes) | **GET** /aggregator/producttypes | All product types (scope: catalog). |


<a name="documentation-for-models"></a>
## Documentation for Models

 - [AssetAggregation](./Models/AssetAggregation.md)
 - [AssetSourceAggregation](./Models/AssetSourceAggregation.md)
 - [AttributeAggregation](./Models/AttributeAggregation.md)
 - [AttributeElementType](./Models/AttributeElementType.md)
 - [AttributeGroupAggregation](./Models/AttributeGroupAggregation.md)
 - [AttributeMetaData](./Models/AttributeMetaData.md)
 - [CatalogAggregation](./Models/CatalogAggregation.md)
 - [CategoryAggregation](./Models/CategoryAggregation.md)
 - [CategoryNavigationAggregation](./Models/CategoryNavigationAggregation.md)
 - [CustomAggregation](./Models/CustomAggregation.md)
 - [EnumValueAggregation](./Models/EnumValueAggregation.md)
 - [ImageAggregation](./Models/ImageAggregation.md)
 - [ListingAggregation](./Models/ListingAggregation.md)
 - [ListingAggregationEntry](./Models/ListingAggregationEntry.md)
 - [MapOfAttributeGroupAggregation](./Models/MapOfAttributeGroupAggregation.md)
 - [MetaDataAggregation](./Models/MetaDataAggregation.md)
 - [PageableFilterOption](./Models/PageableFilterOption.md)
 - [PageableOfAttributeGroupAggregation](./Models/PageableOfAttributeGroupAggregation.md)
 - [PageableOfAttributeMetaData](./Models/PageableOfAttributeMetaData.md)
 - [PageableOfCatalogAggregation](./Models/PageableOfCatalogAggregation.md)
 - [PageableOfCategoryAggregation](./Models/PageableOfCategoryAggregation.md)
 - [PageableOfCategoryNavigationAggregation](./Models/PageableOfCategoryNavigationAggregation.md)
 - [PageableOfListingAggregation](./Models/PageableOfListingAggregation.md)
 - [PageableOfProductAggregation](./Models/PageableOfProductAggregation.md)
 - [PageableOfProductTypeAggregation](./Models/PageableOfProductTypeAggregation.md)
 - [PageableSearchRequest](./Models/PageableSearchRequest.md)
 - [PageableSortOption](./Models/PageableSortOption.md)
 - [ProductAggregation](./Models/ProductAggregation.md)
 - [ProductAttributeAggregation](./Models/ProductAttributeAggregation.md)
 - [ProductTypeAggregation](./Models/ProductTypeAggregation.md)
 - [ProductVariantAggregation](./Models/ProductVariantAggregation.md)
 - [Reference](./Models/Reference.md)
 - [StateAggregation](./Models/StateAggregation.md)
 - [TaxCategoryAggregation](./Models/TaxCategoryAggregation.md)
 - [TaxRateAggregation](./Models/TaxRateAggregation.md)


<a name="documentation-for-authorization"></a>
## Documentation for Authorization

All endpoints do not require authorization.
