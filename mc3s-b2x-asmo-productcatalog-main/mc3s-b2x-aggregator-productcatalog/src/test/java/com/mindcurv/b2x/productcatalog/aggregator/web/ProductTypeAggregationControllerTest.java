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
import com.mindcurv.b2x.commons.models.ProductTypeAggregation;
import com.mindcurv.b2x.commons.token.services.ContextService;
import com.mindcurv.b2x.commons.token.services.TokenService;
import com.mindcurv.b2x.productcatalog.aggregator.aggregation.ProductTypeAggregationService;
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
final class ProductTypeAggregationControllerTest {

  @Mock
  private ProductTypeAggregationService aggregationService;
  @Mock
  private ContextService contextService;
  @Mock
  private ProductTypeAggregation aggregation;
  @Mock
  private Pageable<ProductTypeAggregation> pageable;
  @Mock
  private TokenService tokenService;
  @Mock
  private NativeWebRequest nativeWebRequest;

  @Spy
  @InjectMocks
  private ProductTypeApiController controller;

  @BeforeEach
  void setUp() {
    openMocks(this);

    when(pageable.getResults()).thenReturn(List.of(aggregation));
    when(pageable.getCount()).thenReturn(1L);
    when(pageable.getTotal()).thenReturn(1L);
    when(pageable.getTotalPages()).thenReturn(1L);
    when(pageable.getOffset()).thenReturn(0L);
    when(pageable.getLimit()).thenReturn(20L);

    when(aggregationService.getProductTypeById(anyString(), any()))
        .thenReturn(Optional.of(aggregation));
    when(aggregationService.getProductTypeByKey(anyString(), any()))
        .thenReturn(Optional.of(aggregation));
    when(aggregationService.getProductTypes(any())).thenReturn(pageable);
    when(aggregationService.getProductAttributeMetaDataById(anyString())).thenReturn(List.of());
    when(aggregationService.getProductAttributeMetaDataByKey(anyString())).thenReturn(List.of());
  }

  @Nested
  class getProducts {

    @Test
    void defaults() {
      final var responseEntity = controller.productTypes();
      assertNotNull(responseEntity, "unexpected");
      assertAll(
          () -> assertEquals(OK, responseEntity.getStatusCode(), "unexpected"),
          () -> assertTrue(responseEntity.hasBody(), "unexpected"));
    }
  }

  @Nested
  class productTypeByKey {

    @Test
    void defaults() {
      final var responseEntity = controller.productTypeByKey("key");
      assertNotNull(responseEntity, "unexpected");
      assertAll(
          () -> assertEquals(OK, responseEntity.getStatusCode(), "unexpected"),
          () -> assertTrue(responseEntity.hasBody(), "unexpected"));
    }

    @Test
    void notFound() {
      when(aggregationService.getProductTypeByKey(anyString(), any())).thenReturn(Optional.empty());
      final var responseEntity = controller.productTypeByKey("key");
      assertNotNull(responseEntity, "unexpected");
      assertAll(
          () -> assertEquals(NOT_FOUND, responseEntity.getStatusCode(), "unexpected"),
          () -> assertFalse(responseEntity.hasBody(), "unexpected"));
    }
  }

  @Nested
  class productTypeById {

    @Test
    void defaults() {
      final var responseEntity = controller.productTypeById("key");
      assertNotNull(responseEntity, "unexpected");
      assertAll(
          () -> assertEquals(OK, responseEntity.getStatusCode(), "unexpected"),
          () -> assertTrue(responseEntity.hasBody(), "unexpected"));
    }

    @Test
    void notFound() {
      when(aggregationService.getProductTypeById(anyString(), any())).thenReturn(Optional.empty());
      final var responseEntity = controller.productTypeById("key");
      assertNotNull(responseEntity, "unexpected");
      assertAll(
          () -> assertEquals(NOT_FOUND, responseEntity.getStatusCode(), "unexpected"),
          () -> assertFalse(responseEntity.hasBody(), "unexpected"));
    }
  }

  @Nested
  class productAttributeMetaDataById {

    @Test
    void defaults() {
      final var responseEntity = controller.productAttributeMetaDataById("id");
      assertNotNull(responseEntity, "unexpected");
      assertAll(
          () -> assertEquals(OK, responseEntity.getStatusCode(), "unexpected"),
          () -> assertTrue(responseEntity.hasBody(), "unexpected"));
    }
  }
}
