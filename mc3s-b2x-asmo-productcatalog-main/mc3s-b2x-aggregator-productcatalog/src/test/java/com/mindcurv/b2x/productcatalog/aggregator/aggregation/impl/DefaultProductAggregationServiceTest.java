package com.mindcurv.b2x.productcatalog.aggregator.aggregation.impl;

import static com.mindcurv.b2x.commons.models.Pageable.initPageableFromList;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import com.commercetools.api.models.ResourcePagedQueryResponse;
import com.commercetools.api.models.product.ProductProjection;
import com.mindcurv.b2x.commons.commercetools.services.impl.ProductProjectionCommercetoolsService;
import com.mindcurv.b2x.commons.models.B2xContext;
import com.mindcurv.b2x.commons.models.PageableSearchRequest;
import com.mindcurv.b2x.commons.resolver.NullableBaseResolver;
import com.mindcurv.b2x.productcatalog.aggregator.converter.ProductAggregationProductProjectionConverter;
import com.mindcurv.b2x.productcatalog.aggregator.models.CategoryNavigationAggregation;
import com.mindcurv.b2x.productcatalog.aggregator.models.ProductAggregation;
import com.mindcurv.b2x.productcatalog.aggregator.services.CategoryTreeService;
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
final class DefaultProductAggregationServiceTest {

  @Mock
  private ProductProjectionCommercetoolsService productProjectionCommercetoolsService;
  @Mock
  private NullableBaseResolver<ProductProjection> productProjectionResolver;
  @Mock
  private ProductAggregationProductProjectionConverter productAggregationProductProjectionConverter;
  @Mock
  private ResourcePagedQueryResponse<ProductProjection> pagedQueryResponse;
  @Mock
  private CategoryTreeService categoryTreeService;

  @Mock
  private PageableSearchRequest searchRequest;
  @Mock
  private ProductProjection productProjection;
  @Mock
  private ProductAggregation productAggregation;

  private B2xContext context;
  @Spy
  @InjectMocks
  private DefaultProductAggregationService service;

  @BeforeEach
  void setUp() {
    openMocks(this);
    context = B2xContext.builder().store("store").currentLanguage("en").languages(List.of("en"))
        .build();

    when(productProjectionCommercetoolsService.getItems(anyLong(), anyLong()))
        .thenReturn(pagedQueryResponse);

    when(productProjection.getId()).thenReturn("productId");
    when(productProjection.getKey()).thenReturn("productKey");

    when(productProjectionResolver.resolve(anyString()))
        .thenReturn(Optional.of(productProjection));
    when(productProjectionResolver.resolve(anyString(), anyString()))
        .thenReturn(Optional.of(productProjection));

    when(productAggregation.getCategories()).thenReturn(
        List.of(new CategoryNavigationAggregation().id("categoryId")));

    when(
        productAggregationProductProjectionConverter.convertAsOptional(any(ProductProjection.class),
            any(), any())).thenReturn(Optional.of(productAggregation));
    when(productAggregationProductProjectionConverter.convertPageableFromResponse(any(), any(),
        any())).thenReturn(initPageableFromList(List.of(productAggregation)));

    when(categoryTreeService.generateTree(any())).thenReturn(
        List.of(new CategoryNavigationAggregation().id("categoryId")));
  }

  @Test
  void getItems() {
    assertNotNull(service.getItems(context, searchRequest), "unexpected");
  }

  @Nested
  class getProductAggregation {

    @Test
    void defaults() {
      final var result = service.getProductAggregation(productProjection, context).orElse(null);
      assertNotNull(result, "unexpected");
      assertAll(
          () -> assertFalse(result.getCategories().isEmpty(), "unexpected categories")
      );
    }
  }

  @Nested
  class getRootCategoryIds {

    @Test
    void defaults() {
      assertEquals(List.of("categoryId"), service.getRootCategoryIds(context).stream().toList(),
          "unexpected");
    }

    @Test
    void failed() {
      when(categoryTreeService.generateTree(any())).thenReturn(List.of());
      assertTrue(service.getRootCategoryIds(context).isEmpty(), "unexpected");
    }
  }

  @Nested
  class getItemByKey {

    @Test
    void defaults() {
      assertTrue(service.findByKey("key", context).isPresent(), "unexpected");
    }

    @Test
    void notFound() {
      when(productProjectionResolver.resolve(anyString())).thenReturn(Optional.empty());
      assertFalse(service.findByKey("sku", context).isPresent(), "unexpected");
    }
  }

  @Nested
  class getItemById {

    @Test
    void defaults() {
      assertTrue(service.findById("key", context).isPresent(), "unexpected");
    }

    @Test
    void notFound() {
      when(productProjectionResolver.resolve(anyString())).thenReturn(Optional.empty());
      assertFalse(service.findById("sku", context).isPresent(), "unexpected");
    }
  }

  @Nested
  class getItemBySku {

    @Test
    void defaults() {
      assertTrue(service.findBySku("sku", context).isPresent(), "unexpected");
    }

    @Test
    void notFound() {
      when(productProjectionResolver.resolve(anyString())).thenReturn(Optional.empty());
      assertFalse(service.findBySku("sku", context).isPresent(), "unexpected");
    }
  }

  @Nested
  class getItemBySlug {

    @Test
    void defaults() {
      assertNotNull(service.findBySlug("slug", context), "unexpected");
    }

    @Test
    void notFound() {
      when(productProjectionResolver.resolve(anyString(), anyString())).thenReturn(
          Optional.empty());
      assertTrue(service.findBySlug("slug", context).isEmpty(), "unexpected");
    }
  }
}
