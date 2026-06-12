package com.mindcurv.b2x.productcatalog.aggregator.aggregation;

import static com.mindcurv.b2x.commons.models.Pageable.emptyPageable;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.MockitoAnnotations.openMocks;

import com.mindcurv.b2x.commons.models.B2xContext;
import com.mindcurv.b2x.commons.models.Pageable;
import com.mindcurv.b2x.commons.models.PageableSearchRequest;
import com.mindcurv.b2x.productcatalog.aggregator.models.CategoryAggregation;
import com.mindcurv.b2x.productcatalog.aggregator.models.CategoryNavigationAggregation;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("UnitTest")
final class CategoryAggregationServiceTest {

  private CategoryAggregationService service;

  @BeforeEach
  void setUp() {
    openMocks(this);
    service = new CategoryAggregationService() {
      @Override
      @NotNull
      public Pageable<CategoryAggregation> getItems(@NotNull final B2xContext b2xContext,
          @NotNull final PageableSearchRequest searchRequest) {
        return emptyPageable();
      }

      @Override
      @NotNull
      public Optional<CategoryAggregation> findByKey(@Nullable final String key,
          @NotNull final B2xContext context) {
        return Optional.empty();
      }

      @Override
      @NotNull
      public Optional<CategoryAggregation> findById(@Nullable final String id,
          @NotNull final B2xContext context) {
        return Optional.empty();
      }

      @Override
      @NotNull
      public Optional<CategoryAggregation> findBySlug(@Nullable final String slug,
          @NotNull final B2xContext context) {
        return Optional.empty();
      }

      @Override
      @NotNull
      public List<CategoryNavigationAggregation> getAncestorsById(
          @NotNull final String categoryId, @NotNull final B2xContext context) {
        return List.of();
      }

      @Override
      @NotNull
      public List<CategoryNavigationAggregation> getTree(@NotNull B2xContext context) {
        return List.of();
      }
    };
  }

  @Test
  void getListExpansions() {
    assertEquals(List.of("custom.type"), service.getListExpansions(), "unexpected");
  }

  @Test
  void getDetailExpansions() {
    final var result = service.getDetailExpansions();
    assertAll(
        () -> assertTrue(result.contains("ancestors[*]"), "expected ancestor expand"),
        () -> assertTrue(result.contains("custom.type"), "expected custom type expand"),
        () -> assertEquals(2, result.size(), "expected 2 entries")
    );
  }
}