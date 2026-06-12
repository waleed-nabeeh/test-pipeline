package com.mindcurv.b2x.productreview.aggregator.converter;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import com.mindcurv.b2x.connector.models.Statistics;
import com.mindcurv.b2x.connector.product.models.ProductDTO;
import com.mindcurv.b2x.productreview.aggregator.converter.impl.DtoProductReviewSummaryProductDTOConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

@Tag("UnitTest")
final class ProductReviewSummaryProductDTOConverterTest {

  @Mock
  private ProductDTO productDTO;

  private ProductReviewSummaryProductDTOConverter converter;

  @BeforeEach
  void setUp() {
    openMocks(this);

    final var stats = mock(Statistics.class);
    when(stats.getCount()).thenReturn(1);
    when(stats.getAverage()).thenReturn(2.0);
    when(stats.getMax()).thenReturn(3.0);
    when(stats.getMin()).thenReturn(4.0);
    when(productDTO.getReviewStatistics()).thenReturn(stats);

    converter = new DtoProductReviewSummaryProductDTOConverter();
  }

  @Nested
  class convert {

    @Test
    void defaults() {
      final var result = converter.convert(productDTO);
      assertNotNull(result, "result is null");
      assertAll(
          () -> assertEquals(1, result.getCount(), "unexpected count"),
          () -> assertEquals(2.0, result.getAverageRating(), "unexpected average rating "),
          () -> assertEquals(3.0, result.getMaxRating(), "unexpected max rating"),
          () -> assertEquals(4.0, result.getMinRating(), "unexpected min rating")
      );
    }

    @Test
    void nullValue() {
      final var result = converter.convert(null);
      assertNull(result, "result is null");
    }
  }
}