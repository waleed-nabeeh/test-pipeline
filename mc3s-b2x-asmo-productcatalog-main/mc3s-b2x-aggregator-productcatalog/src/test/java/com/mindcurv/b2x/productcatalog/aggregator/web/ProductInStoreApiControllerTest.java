package com.mindcurv.b2x.productcatalog.aggregator.web;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

import com.mindcurv.b2x.commons.models.Pageable;
import com.mindcurv.b2x.commons.token.services.ContextService;
import com.mindcurv.b2x.commons.token.services.TokenService;
import com.mindcurv.b2x.productcatalog.aggregator.aggregation.ProductInStoreAggregationService;
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
import org.springframework.web.context.request.NativeWebRequest;

@Tag("UnitTest")
final class ProductInStoreApiControllerTest {

  @Mock
  private ProductInStoreAggregationService productAggregationService;
  @Mock
  private ContextService contextService;
  @Mock
  private ProductAggregation aggregation;
  @Mock
  private Pageable<ProductAggregation> pageable;
  @Mock
  private TokenService tokenService;
  @Mock
  private NativeWebRequest nativeWebRequest;

  @Spy
  @InjectMocks
  private ProductInStoreApiController controller;

  @BeforeEach
  void setUp() {
    openMocks(this);

    when(pageable.getResults()).thenReturn(List.of(aggregation));
    when(pageable.getCount()).thenReturn(1L);
    when(pageable.getTotal()).thenReturn(1L);
    when(pageable.getTotalPages()).thenReturn(1L);
    when(pageable.getOffset()).thenReturn(0L);
    when(pageable.getLimit()).thenReturn(20L);

    when(productAggregationService.findById(anyString(), any()))
        .thenReturn(Optional.of(aggregation));
    when(productAggregationService.findByKey(anyString(), any()))
        .thenReturn(Optional.of(aggregation));
    when(productAggregationService.findBySku(anyString(), any()))
        .thenReturn(Optional.of(aggregation));
    when(productAggregationService.findBySlug(anyString(), any())).thenReturn(
        Optional.of(aggregation));

  }

  @Nested
  class productInStoreByKey {

    @Test
    void defaults() {
      final var responseEntity = controller.productInStoreByKey("key");
      assertNotNull(responseEntity, "unexpected");
      assertAll(
          () -> assertEquals(OK, responseEntity.getStatusCode(), "unexpected"),
          () -> assertTrue(responseEntity.hasBody(), "unexpected"));
    }

    @Test
    void notFound() {
      when(productAggregationService.findByKey(anyString(), any())).thenReturn(Optional.empty());
      final var responseEntity = controller.productInStoreByKey("key");
      assertNotNull(responseEntity, "unexpected");
      assertAll(
          () -> assertEquals(NOT_FOUND, responseEntity.getStatusCode(), "unexpected"),
          () -> assertFalse(responseEntity.hasBody(), "unexpected"));
    }
  }

  @Nested
  class getProductById {

    @Test
    void defaults() {
      final var responseEntity = controller.productInStoreById("key");
      assertNotNull(responseEntity, "unexpected");
      assertAll(
          () -> assertEquals(OK, responseEntity.getStatusCode(), "unexpected"),
          () -> assertTrue(responseEntity.hasBody(), "unexpected"));
    }

    @Test
    void notFound() {
      when(productAggregationService.findById(anyString(), any())).thenReturn(Optional.empty());
      final var responseEntity = controller.productInStoreById("key");
      assertNotNull(responseEntity, "unexpected");
      assertAll(
          () -> assertEquals(NOT_FOUND, responseEntity.getStatusCode(), "unexpected"),
          () -> assertFalse(responseEntity.hasBody(), "unexpected"));
    }
  }

  @Nested
  class getProductBySku {

    @Test
    void defaults() {
      final var responseEntity = controller.productInStoreBySku("sku");
      assertNotNull(responseEntity, "unexpected");
      assertAll(
          () -> assertEquals(OK, responseEntity.getStatusCode(), "unexpected"),
          () -> assertTrue(responseEntity.hasBody(), "unexpected"));
    }

    @Test
    void notFound() {
      when(productAggregationService.findBySku(anyString(), any())).thenReturn(Optional.empty());
      final var responseEntity = controller.productInStoreBySku("sku");
      assertNotNull(responseEntity, "unexpected");
      assertAll(
          () -> assertEquals(NOT_FOUND, responseEntity.getStatusCode(), "unexpected"),
          () -> assertFalse(responseEntity.hasBody(), "unexpected"));
    }
  }

  @Nested
  class getProductBySlug {

    @Test
    void defaults() {
      final var responseEntity = controller.productInStoreBySlug("slug");
      assertNotNull(responseEntity, "unexpected");
      assertAll(
          () -> assertEquals(OK, responseEntity.getStatusCode(), "unexpected"),
          () -> assertTrue(responseEntity.hasBody(), "unexpected"));
    }

    @Test
    void notFound() {
      when(productAggregationService.findBySlug(anyString(), any())).thenReturn(Optional.empty());
      final var responseEntity = controller.productInStoreBySlug("slug");
      assertNotNull(responseEntity, "unexpected");
      assertAll(
          () -> assertEquals(NOT_FOUND, responseEntity.getStatusCode(), "unexpected"),
          () -> assertFalse(responseEntity.hasBody(), "unexpected"));
    }
  }
}
