package com.mindcurv.b2x.productcatalog.aggregator.aggregation.impl;

import static com.mindcurv.b2x.commons.models.Pageable.emptyPageable;
import static com.mindcurv.b2x.commons.models.Pageable.initPageableFromList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import com.commercetools.api.models.custom_object.CustomObject;
import com.commercetools.api.models.product_type.AttributeDefinition;
import com.commercetools.api.models.product_type.ProductType;
import com.commercetools.api.models.product_type.ProductTypeReference;
import com.mindcurv.b2x.commons.base.converter.ProductTypeAggregationProductTypeConverter;
import com.mindcurv.b2x.commons.commercetools.services.impl.ProductTypeCommercetoolsService;
import com.mindcurv.b2x.commons.models.AttributeMetaData;
import com.mindcurv.b2x.commons.models.B2xContext;
import com.mindcurv.b2x.commons.models.ProductTypeAggregation;
import com.mindcurv.b2x.commons.resolver.BaseResolver;
import com.mindcurv.b2x.commons.resolver.NullableBaseResolver;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

@Tag("UnitTest")
final class DefaultProductTypeAggregationServiceTest {

  @Mock
  private ProductTypeCommercetoolsService productTypeCommercetoolsService;
  @Mock
  private BaseResolver<List<AttributeMetaData>> metaDataResolver;
  @Mock
  private ProductTypeAggregationProductTypeConverter productTypeAggregationProductTypeConverter;
  @Mock
  private NullableBaseResolver<ProductType> productTypeResolver;

  @Mock
  private ProductType productType;
  @Mock
  private CustomObject customObject;

  private B2xContext context;
  @Spy
  @InjectMocks
  private DefaultProductTypeAggregationService service;

  @BeforeEach
  void setUp() {
    openMocks(this);
    context = B2xContext.builder().store("store").languages(List.of("en")).build();
    when(customObject.getValue()).thenReturn(Map.of(
        "attributeName",
        new AttributeMetaData().name("theName").group("theGroup").unitOfMeasure("uom")));
    when(metaDataResolver.resolve(notNull())).thenReturn(List.of(new AttributeMetaData()));
    when(productType.getId()).thenReturn("typeId");
    when(productType.getKey()).thenReturn("typeKey");
    when(productType.getAttributes()).thenReturn(List.of(
        AttributeDefinition.builder().name("attributeName").buildUnchecked(),
        AttributeDefinition.builder().name("unknown").buildUnchecked()));

    when(productTypeCommercetoolsService.getAllItems()).thenReturn(List.of(productType));
    when(productTypeCommercetoolsService.findById(anyString()))
        .thenReturn(Optional.of(productType));
    when(productTypeCommercetoolsService.findByKey(anyString()))
        .thenReturn(Optional.of(productType));

    when(productTypeAggregationProductTypeConverter.convertAsOptional(any(), any(), any()))
        .thenReturn(Optional.of(new ProductTypeAggregation()));
    when(productTypeAggregationProductTypeConverter.convertList(anyList(), any()))
        .thenReturn(List.of(new ProductTypeAggregation()));

    when(productTypeAggregationProductTypeConverter.convertPageable(anyList(), any()))
        .thenReturn(initPageableFromList(List.of(new ProductTypeAggregation())));
    when(productTypeResolver.resolve(any())).thenReturn(Optional.of(productType));
  }

  @Nested
  class getProductTypes {

    @Test
    void defaults() {
      assertNotEquals(emptyPageable(), service.getProductTypes(context), "unexpected");
    }
  }

  @Nested
  class getProductTypeById {

    @Test
    void defaults() {
      assertTrue(service.getProductTypeById("id", context).isPresent(), "unexpected");
    }

    @Test
    void notFound() {
      when(productTypeResolver.resolve(any())).thenReturn(Optional.empty());
      assertTrue(service.getProductTypeById("id", context).isEmpty(), "unexpected");
    }
  }

  @Nested
  class getProductTypeByKey {

    @Test
    void defaults() {
      assertTrue(service.getProductTypeByKey("key", context).isPresent(), "unexpected");
    }

    @Test
    void notFound() {
      when(productTypeResolver.resolve(any())).thenReturn(Optional.empty());
      assertTrue(service.getProductTypeByKey("key", context).isEmpty(), "unexpected");
    }
  }

  @Nested
  class getProductAttributeMetaDataByKey {

    @Test
    void defaults() {
      assertNotEquals(List.of(), service.getProductAttributeMetaDataByKey("key"), "unexpected");
    }

    @Test
    void notFound() {
      when(metaDataResolver.resolve(anyString())).thenReturn(List.of());
      assertEquals(List.of(), service.getProductAttributeMetaDataByKey("key"), "unexpected");
    }
  }

  @Nested
  class getProductAttributeMetaDataById {

    @Test
    void defaults() {
      assertNotEquals(List.of(), service.getProductAttributeMetaDataById("id"), "unexpected");
    }

    @Test
    void notFound() {
      when(metaDataResolver.resolve(anyString())).thenReturn(List.of());
      assertEquals(List.of(), service.getProductAttributeMetaDataById("id"), "unexpected");
    }
  }

  @Nested
  class getProductAttributeMetaData {

    @Test
    void defaults() {
      assertNotEquals(List.of(), service.getProductAttributeMetaData(productType), "unexpected");
    }

    @Test
    void reference() {
      assertNotEquals(List.of(),
          service.getProductAttributeMetaData(ProductTypeReference.builder().id("id").build()),
          "unexpected");
    }

    @Test
    void nullValue() {
      assertEquals(List.of(), service.getProductAttributeMetaData((ProductType) null),
          "unexpected");
    }

    @Test
    void nullReference() {
      assertEquals(List.of(), service.getProductAttributeMetaData((ProductTypeReference) null),
          "unexpected");
    }
  }

}
