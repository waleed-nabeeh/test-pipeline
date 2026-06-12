package com.mindcurv.b2x.productreview.aggregator.aggregation.impl;

import static com.mindcurv.b2x.commons.models.Pageable.emptyPageable;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import com.mindcurv.b2x.commons.models.B2xContext;
import com.mindcurv.b2x.commons.models.Pageable;
import com.mindcurv.b2x.commons.resolver.NullableBaseResolver;
import com.mindcurv.b2x.connector.adapter.DTOCreateAdapter;
import com.mindcurv.b2x.connector.adapter.DTOReadAdapter;
import com.mindcurv.b2x.connector.product.models.ProductDTO;
import com.mindcurv.b2x.connector.review.models.ReviewDTO;
import com.mindcurv.b2x.connector.review.models.ReviewDTOCreateRequest;
import com.mindcurv.b2x.productcatalog.review.models.ProductReviewAggregation;
import com.mindcurv.b2x.productcatalog.review.models.ProductReviewCreateRequest;
import com.mindcurv.b2x.productcatalog.review.models.ProductReviewSummary;
import com.mindcurv.b2x.productreview.aggregator.converter.ProductReviewAggregationReviewDTOConverter;
import com.mindcurv.b2x.productreview.aggregator.converter.ProductReviewSummaryProductDTOConverter;
import com.mindcurv.b2x.productreview.aggregator.converter.ReviewDTOCreateRequestProductReviewCreateRequestConverter;
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
final class DefaultProductReviewAggregationServiceTest {

  @Mock
  private NullableBaseResolver<ProductDTO> productDTOResolver;
  @Mock
  private DTOReadAdapter<ReviewDTO> reviewReadAdapter;
  @Mock
  private DTOCreateAdapter<ReviewDTO, ReviewDTOCreateRequest> reviewCreateAdapter;
  @Mock
  private ProductReviewAggregationReviewDTOConverter productReviewAggregationReviewDTOConverter;
  @Mock
  private ReviewDTOCreateRequestProductReviewCreateRequestConverter reviewDTOCreateRequestProductReviewCreateRequestConverter;
  @Mock
  private ProductReviewSummaryProductDTOConverter productReviewSummaryProductDTOConverter;
  @Mock
  private ProductDTO productDTO;
  @Mock
  private ReviewDTO reviewDTO;
  @Mock
  private ProductReviewCreateRequest createRequest;
  @Mock
  private B2xContext context;

  @Spy
  @InjectMocks
  private DefaultProductReviewAggregationService service;

  @BeforeEach
  void setUp() {
    openMocks(this);

    when(productDTOResolver.resolve(anyString())).thenReturn(Optional.of(productDTO));

    when(reviewReadAdapter.findById(anyString())).thenReturn(Optional.of(reviewDTO));
    when(reviewReadAdapter.getItems(any())).thenReturn(
        Pageable.initPageableFromList(List.of(reviewDTO)));

    when(reviewCreateAdapter.createAsOptional(any())).thenReturn(Optional.of(reviewDTO));

    when(reviewReadAdapter.findById(anyString())).thenReturn(Optional.of(reviewDTO));
    when(reviewReadAdapter.findByKey(anyString())).thenReturn(Optional.of(reviewDTO));

    when(productReviewAggregationReviewDTOConverter.convertAsOptional(any())).thenReturn(
        Optional.of(new ProductReviewAggregation()));
    when(productReviewSummaryProductDTOConverter.convertAsOptional(any())).thenReturn(
        Optional.of(new ProductReviewSummary()));

    when(reviewDTOCreateRequestProductReviewCreateRequestConverter.convertAsOptional(
        any())).thenReturn(Optional.of(new ReviewDTOCreateRequest()));
    when(reviewDTOCreateRequestProductReviewCreateRequestConverter.convertAsOptional(
        any(), any())).thenReturn(Optional.of(new ReviewDTOCreateRequest()));
  }

  @Nested
  class createProductReview {

    @Test
    void defaults() {
      final var result = service.createProductReview(createRequest, context);
      assertTrue(result.isPresent(), "unexpected");
    }

    @Test
    void failed() {
      when(reviewCreateAdapter.createAsOptional(any())).thenReturn(Optional.empty());
      final var result = service.createProductReview(createRequest, context);
      assertTrue(result.isEmpty(), "unexpected");
    }

  }

  @Nested
  class findReviewById {

    @Test
    void defaults() {
      final var result = service.findReviewById("id", context);
      assertTrue(result.isPresent(), "unexpected");
    }

    @Test
    void failed() {
      when(reviewReadAdapter.findById(any())).thenReturn(Optional.empty());
      final var result = service.findReviewById("id", context);
      assertTrue(result.isEmpty(), "unexpected");
    }

  }

  @Nested
  class findReviewByKey {

    @Test
    void defaults() {
      final var result = service.findReviewByKey("key", context);
      assertTrue(result.isPresent(), "unexpected");
    }

    @Test
    void failed() {
      when(reviewReadAdapter.findByKey(any())).thenReturn(Optional.empty());
      final var result = service.findReviewByKey("key", context);
      assertTrue(result.isEmpty(), "unexpected");
    }

  }

  @Nested
  class getProductReviewSummaryByProductKey {

    @Test
    void defaults() {
      final var result = service.getProductReviewSummaryByProductKey("key", 0L, 20L, context);
      assertTrue(result.isPresent(), "unexpected");
    }

    @Test
    void failed() {
      when(productDTOResolver.resolve(anyString())).thenReturn(Optional.empty());
      final var result = service.getProductReviewSummaryByProductKey("key", 0L, 20L, context);
      assertTrue(result.isEmpty(), "unexpected");
    }

  }

  @Nested
  class getProductReviewSummaryByProductId {

    @Test
    void defaults() {
      final var result = service.getProductReviewSummaryByProductId("id", 0L, 20L, context);
      assertTrue(result.isPresent(), "unexpected");
    }

    @Test
    void failed() {
      when(productDTOResolver.resolve(anyString())).thenReturn(Optional.empty());
      final var result = service.getProductReviewSummaryByProductId("id", 0L, 20L, context);
      assertTrue(result.isEmpty(), "unexpected");
    }

  }

  @Nested
  class getMyProductReviews {

    @Test
    void defaults() {
      final var result = service.getMyProductReviews(context);
      assertNotEquals(emptyPageable(), result, "unexpected");
    }

  }

  @Nested
  class fetchProductReviewSummary {

    @Test
    void defaults() {
      final var result = service.fetchProductReviewSummary(productDTO, 0L, 20L);
      assertNotNull(result, "unexpected");
    }

    @Test
    void failed() {
      final var result = service.fetchProductReviewSummary(productDTO, 0L, 20L);
      assertNotNull(result, "unexpected");
    }

  }
}