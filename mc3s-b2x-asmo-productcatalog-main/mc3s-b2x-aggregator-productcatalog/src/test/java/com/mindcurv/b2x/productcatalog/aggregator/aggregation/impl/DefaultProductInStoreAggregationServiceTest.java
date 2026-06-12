package com.mindcurv.b2x.productcatalog.aggregator.aggregation.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import com.commercetools.api.models.ResourcePagedQueryResponse;
import com.commercetools.api.models.product.ProductProjection;
import com.mindcurv.b2x.commons.commercetools.instore.services.impl.ProductProjectionInStoreService;
import com.mindcurv.b2x.commons.commercetools.services.impl.ProductProjectionCommercetoolsService;
import com.mindcurv.b2x.commons.models.B2xContext;
import com.mindcurv.b2x.productcatalog.aggregator.converter.ProductAggregationProductProjectionConverter;
import com.mindcurv.b2x.productcatalog.aggregator.models.ProductAggregation;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

@Tag("UnitTest")
final class DefaultProductInStoreAggregationServiceTest {

  @Mock
  private ProductProjectionInStoreService projectionInStoreService;
  @Mock
  private ProductProjectionCommercetoolsService productProjectionCommercetoolsService;
  @Mock
  private ProductAggregationProductProjectionConverter productAggregationProductProjectionConverter;
  @Mock
  private ResourcePagedQueryResponse<ProductProjection> pagedQueryResponse;

  @Mock
  private ProductProjection productProjection;
  private B2xContext context;
  @Spy
  @InjectMocks
  private DefaultProductInStoreAggregationService service;

  @BeforeEach
  void setUp() {
    openMocks(this);
    context = B2xContext.builder().store("store").currentLanguage("en").languages(List.of("en"))
        .build();

    when(productProjectionCommercetoolsService.getItems(anyLong(), anyLong()))
        .thenReturn(pagedQueryResponse);

    when(productProjection.getId()).thenReturn("productId");
    when(productProjection.getKey()).thenReturn("productKey");

    when(projectionInStoreService.findByIdInStore(anyString(), anyString(), anyList()))
        .thenReturn(Optional.of(productProjection));
    when(projectionInStoreService.findByKeyInStore(anyString(), anyString(), anyList()))
        .thenReturn(Optional.of(productProjection));

    when(productProjectionCommercetoolsService.findBySku(anyString()))
        .thenReturn(Optional.of(productProjection));
    when(productProjectionCommercetoolsService.findBySlug(anyString(), anyString()))
        .thenReturn(Optional.of(productProjection));

    when(
        productAggregationProductProjectionConverter.convertAsOptional(any(ProductProjection.class),
            any(), any()))
        .thenReturn(Optional.of(new ProductAggregation()));
  }

  @Test
  void getDetailExpansions() {
    assertEquals(
        List.of("productType", "taxCategory"), service.getDetailExpansions(), "unexpected");
  }

  @Nested
  class findByKey {

    @Test
    void defaults() {
      assertTrue(service.findByKey("key", context).isPresent(), "unexpected");
    }

    @Test
    void notFound() {
      when(projectionInStoreService.findByKeyInStore(anyString(), anyString(), anyList()))
          .thenReturn(Optional.empty());
      assertFalse(service.findByKey("key", context).isPresent(), "unexpected");
    }
  }

  @Nested
  class getItemById {

    @Test
    void defaults() {
      assertTrue(service.findById("id", context).isPresent(), "unexpected");
    }

    @Test
    void notFound() {
      when(projectionInStoreService.findByIdInStore(anyString(), anyString(), anyList()))
          .thenReturn(Optional.empty());
      assertFalse(service.findById("id", context).isPresent(), "unexpected");
    }
  }

  @Nested
  class findBySku {

    @Test
    void defaults() {
      assertTrue(service.findBySku("sku", context).isPresent(), "unexpected");
    }

    @Test
    void notFound() {
      when(productProjectionCommercetoolsService.findBySku(anyString()))
          .thenReturn(Optional.empty());
      assertFalse(service.findBySku("sku", context).isPresent(), "unexpected");
    }
  }

  @Nested
  class findBySlug {

    @Test
    void defaults() {
      assertTrue(service.findBySlug("slug", context).isPresent(), "unexpected");
    }

    @Test
    void notFound() {
      when(productProjectionCommercetoolsService.findBySlug(anyString(), anyString()))
          .thenReturn(Optional.empty());
      assertFalse(service.findBySlug("slug", context).isPresent(), "unexpected");
    }
  }
}
