package com.mindcurv.b2x.productcatalog.aggregator.aggregation;

import static com.mindcurv.b2x.commons.models.Pageable.emptyPageable;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.MockitoAnnotations.openMocks;

import com.mindcurv.b2x.commons.models.B2xContext;
import com.mindcurv.b2x.commons.models.Pageable;
import com.mindcurv.b2x.commons.models.PageableSearchRequest;
import com.mindcurv.b2x.productcatalog.aggregator.models.ProductAggregation;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("UnitTest")
final class ProductAggregationServiceTest {

  private ProductAggregationService service;

  @BeforeEach
  void setUp() {
    openMocks(this);
    service = new ProductAggregationService() {
      @Override
      @NotNull
      public Pageable<ProductAggregation> getItems(@NotNull final B2xContext context,
          @NotNull final PageableSearchRequest searchRequest) {
        return emptyPageable();
      }

      @Override
      @NotNull
      public Optional<ProductAggregation> findByKey(@Nullable final String key,
          @NotNull B2xContext context) {
        return Optional.empty();
      }

      @Override
      @NotNull
      public Optional<ProductAggregation> findById(@Nullable final String id,
          @NotNull final B2xContext context) {
        return Optional.empty();
      }

      @Override
      @NotNull
      public Optional<ProductAggregation> findBySku(@Nullable final String sku,
          @NotNull final B2xContext context) {
        return Optional.empty();
      }

      @Override
      @NotNull
      public Optional<ProductAggregation> findBySlug(@Nullable final String slug,
          @NotNull final B2xContext context) {
        return Optional.empty();
      }
    };
  }

  @Test
  void getDetailExpansions() {
    final var result = service.getDetailExpansions();
    assertAll(
        () -> assertTrue(result.contains("categories[*].ancestors[*]"), "expected ancestor expand"),
        () -> assertTrue(result.contains("categories[*].custom.type"),
            "expected category custom type expand")
    );
  }
}