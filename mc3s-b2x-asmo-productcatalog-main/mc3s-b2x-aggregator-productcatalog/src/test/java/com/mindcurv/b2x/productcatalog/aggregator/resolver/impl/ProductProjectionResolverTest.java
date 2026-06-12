package com.mindcurv.b2x.productcatalog.aggregator.resolver.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import com.commercetools.api.models.product.ProductProjection;
import com.mindcurv.b2x.commons.commercetools.services.impl.ProductProjectionCommercetoolsService;
import java.util.Arrays;
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
final class ProductProjectionResolverTest {

  @Mock
  private ProductProjectionCommercetoolsService productProjectionCommercetoolsService;

  private String[] expands;

  @Mock
  private ProductProjection item;

  @Spy
  @InjectMocks
  private ProductProjectionResolver resolver;

  @BeforeEach
  void setUp() {
    openMocks(this);

    expands = List.of("productType", "taxCategory", "categories[*].custom.type",
            "categories[*].ancestors[*]")
        .toArray(new String[0]);
    when(productProjectionCommercetoolsService.findById(anyString(), any())).thenReturn(
        Optional.of(item));
    when(productProjectionCommercetoolsService.findByKey(anyString(), any())).thenReturn(
        Optional.of(item));
    when(productProjectionCommercetoolsService.findBySku(anyString(), any())).thenReturn(
        Optional.of(item));
    when(productProjectionCommercetoolsService.findBySlug(anyString(), anyString(),
        eq(expands))).thenReturn(
        Optional.of(item));
    when(productProjectionCommercetoolsService.getAllItems(any())).thenReturn(List.of(item));
  }

  @Nested
  class resolve {

    @Test
    void id() {
      assertTrue(resolver.resolve("15880498-36fa-4379-aa31-9f59394a009f").isPresent(),
          "unexpected");
    }

    @Test
    void idNotFound() {
      when(productProjectionCommercetoolsService.findById(anyString(), anyList())).thenReturn(
          Optional.empty());
      assertTrue(resolver.resolve("15880498-36fa-4379-aa31-9f59394a009f").isEmpty(), "unexpected");
    }

    @Test
    void slug() {
      assertTrue(resolver.resolve("slug", "en").isPresent(), "unexpected");
    }

    @Test
    void slugNotFound() {
      when(productProjectionCommercetoolsService.findBySlug(anyString(), anyString(),
          eq(expands))).thenReturn(Optional.empty());
      assertTrue(resolver.resolve("slug", "en").isEmpty(), "unexpected");
    }

    @Test
    void sku() {
      assertTrue(resolver.resolve("sku=sku").isPresent(), "unexpected");
    }

    @Test
    void skuNotFound() {
      when(productProjectionCommercetoolsService.findBySku(anyString(), anyList())).thenReturn(
          Optional.empty());
      assertTrue(resolver.resolve("sku=sku").isEmpty(), "unexpected");
    }

    @Test
    void key() {
      assertTrue(resolver.resolve("key").isPresent(), "unexpected");
    }

    @Test
    void keyNotFound() {
      when(productProjectionCommercetoolsService.findByKey(anyString(), anyList())).thenReturn(
          Optional.empty());
      assertTrue(resolver.resolve("key").isEmpty(), "unexpected");
    }

  }

  @Test
  void getAllItems() {
    assertFalse(resolver.getAllItems().isEmpty(), "unexpected");
  }

  @Test
  void getExpansions() {
    assertEquals(Arrays.stream(expands).toList(),
        resolver.getExpansions(), "unexpected");
  }
}