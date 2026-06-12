package com.mindcurv.b2x.productreview.aggregator.web;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

import com.mindcurv.b2x.commons.models.Pageable;
import com.mindcurv.b2x.commons.token.services.ContextService;
import com.mindcurv.b2x.commons.token.services.TokenService;
import com.mindcurv.b2x.productcatalog.review.models.ProductReviewAggregation;
import com.mindcurv.b2x.productcatalog.review.models.ProductReviewCreateRequest;
import com.mindcurv.b2x.productcatalog.review.models.ProductReviewSummary;
import com.mindcurv.b2x.productreview.aggregator.aggregation.ProductReviewAggregationService;
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
final class ProductReviewApiControllerTest {

  @Mock
  private ProductReviewAggregationService productReviewAggregationService;
  @Mock
  private ContextService contextService;
  @Mock
  private ProductReviewAggregation aggregation;
  @Mock
  private Pageable<ProductReviewAggregation> pageable;
  @Mock
  private TokenService tokenService;
  @Mock
  private NativeWebRequest nativeWebRequest;

  @Spy
  @InjectMocks
  private ProductReviewApiController controller;

  @BeforeEach
  void setUp() {
    openMocks(this);

    when(pageable.getResults()).thenReturn(singletonList(aggregation));
    when(pageable.getCount()).thenReturn(1L);
    when(pageable.getTotal()).thenReturn(1L);
    when(pageable.getTotalPages()).thenReturn(1L);
    when(pageable.getOffset()).thenReturn(0L);
    when(pageable.getLimit()).thenReturn(20L);

    when(productReviewAggregationService.findReviewById(anyString(), any()))
        .thenReturn(Optional.of(aggregation));
    when(productReviewAggregationService.findReviewByKey(anyString(), any()))
        .thenReturn(Optional.of(aggregation));
    when(productReviewAggregationService.createProductReview(any(), any()))
        .thenReturn(Optional.of(aggregation));
    when(productReviewAggregationService.getMyProductReviews(any())).thenReturn(pageable);
    when(productReviewAggregationService.getProductReviewSummaryByProductId(anyString(), anyLong(),
        anyLong(), any())).thenReturn(Optional.of(new ProductReviewSummary()));
    when(productReviewAggregationService.getProductReviewSummaryByProductKey(anyString(), anyLong(),
        anyLong(), any())).thenReturn(Optional.of(new ProductReviewSummary()));

  }

  @Nested
  class productReview {

    @Test
    void defaults() {
      final var responseEntity = controller.productReview(new ProductReviewCreateRequest());
      assertNotNull(responseEntity, "unexpected");
      assertAll(
          () -> assertEquals(OK, responseEntity.getStatusCode(), "unexpected"),
          () -> assertTrue(responseEntity.hasBody(), "unexpected"));
    }

    @Test
    void failed() {
      when(productReviewAggregationService.createProductReview(any(), any()))
          .thenReturn(Optional.empty());
      final var responseEntity = controller.productReview(new ProductReviewCreateRequest());
      assertNotNull(responseEntity, "unexpected");
      assertAll(
          () -> assertEquals(BAD_REQUEST, responseEntity.getStatusCode(), "unexpected"),
          () -> assertFalse(responseEntity.hasBody(), "unexpected"));
    }
  }

  @Nested
  class productReviewById {

    @Test
    void defaults() {
      final var responseEntity = controller.productReviewById("id");
      assertNotNull(responseEntity, "unexpected");
      assertAll(
          () -> assertEquals(OK, responseEntity.getStatusCode(), "unexpected productReviewById"),
          () -> assertTrue(responseEntity.hasBody(), "unexpected"));
    }

    @Test
    void failed() {
      when(productReviewAggregationService.findReviewById(anyString(), any()))
          .thenReturn(Optional.empty());
      final var responseEntity = controller.productReviewById("id");
      assertNotNull(responseEntity, "unexpected");
      assertAll(
          () -> assertEquals(NOT_FOUND, responseEntity.getStatusCode(), "unexpected productReviewById"),
          () -> assertFalse(responseEntity.hasBody(), "unexpected"));
    }
  }

  @Nested
  class productReviewByKey {

    @Test
    void defaults() {
      final var responseEntity = controller.productReviewByKey("key");
      assertNotNull(responseEntity, "unexpected");
      assertAll(
          () -> assertEquals(OK, responseEntity.getStatusCode(), "unexpected productReviewByKey"),
          () -> assertTrue(responseEntity.hasBody(), "unexpected"));
    }

    @Test
    void failed() {
      when(productReviewAggregationService.findReviewByKey(anyString(), any()))
          .thenReturn(Optional.empty());
      final var responseEntity = controller.productReviewByKey("key");
      assertNotNull(responseEntity, "unexpected");
      assertAll(
          () -> assertEquals(NOT_FOUND, responseEntity.getStatusCode(), "unexpected productReviewByKey"),
          () -> assertFalse(responseEntity.hasBody(), "unexpected"));
    }
  }

  @Nested
  class productReviewsByKey {

    @Test
    void defaults() {
      final var responseEntity = controller.productReviewsByKey("key", 0L, 20L);
      assertNotNull(responseEntity, "unexpected");
      assertAll(
          () -> assertEquals(OK, responseEntity.getStatusCode(), "unexpected productReviewsByKey"),
          () -> assertTrue(responseEntity.hasBody(), "unexpected"));
    }

    @Test
    void failed() {
      when(productReviewAggregationService.getProductReviewSummaryByProductKey(anyString(), anyLong(), anyLong(),any()))
          .thenReturn(Optional.empty());
      final var responseEntity = controller.productReviewsByKey("key", 0L, 20L);
      assertNotNull(responseEntity, "unexpected");
      assertAll(
          () -> assertEquals(NOT_FOUND, responseEntity.getStatusCode(), "unexpected productReviewsByKey"),
          () -> assertFalse(responseEntity.hasBody(), "unexpected"));
    }
  }

  @Nested
  class productReviewsByProductId {

    @Test
    void defaults() {
      final var responseEntity = controller.productReviewsByProductId("id", 0L, 20L);
      assertNotNull(responseEntity, "unexpected");
      assertAll(
          () -> assertEquals(OK, responseEntity.getStatusCode(), "unexpected productReviewsByProductId"),
          () -> assertTrue(responseEntity.hasBody(), "unexpected"));
    }

    @Test
    void failed() {
      when(productReviewAggregationService.getProductReviewSummaryByProductId(anyString(), anyLong(), anyLong(),any()))
          .thenReturn(Optional.empty());
      final var responseEntity = controller.productReviewsByProductId("id", 0L, 20L);
      assertNotNull(responseEntity, "unexpected");
      assertAll(
          () -> assertEquals(NOT_FOUND, responseEntity.getStatusCode(), "unexpected productReviewsByProductId"),
          () -> assertFalse(responseEntity.hasBody(), "unexpected"));
    }
  }
}