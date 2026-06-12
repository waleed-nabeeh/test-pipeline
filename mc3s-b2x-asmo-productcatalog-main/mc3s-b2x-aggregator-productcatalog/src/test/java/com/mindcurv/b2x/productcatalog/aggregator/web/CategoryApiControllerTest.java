package com.mindcurv.b2x.productcatalog.aggregator.web;

import static java.util.Collections.singletonList;
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
import com.mindcurv.b2x.productcatalog.aggregator.aggregation.CategoryAggregationService;
import com.mindcurv.b2x.productcatalog.aggregator.models.CategoryAggregation;
import com.mindcurv.b2x.productcatalog.aggregator.models.CategoryNavigationAggregation;
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
final class CategoryApiControllerTest {

  @Mock
  private CategoryAggregationService categoryAggregationService;
  @Mock
  private ContextService contextService;
  @Mock
  private CategoryAggregation aggregation;
  @Mock
  private CategoryNavigationAggregation navigationAggregation;
  @Mock
  private Pageable<CategoryAggregation> pageable;
  @Mock
  private TokenService tokenService;
  @Mock
  private NativeWebRequest nativeWebRequest;

  @Spy
  @InjectMocks
  private CategoryApiController controller;

  @BeforeEach
  void setUp() {
    openMocks(this);

    when(pageable.getResults()).thenReturn(singletonList(aggregation));
    when(pageable.getCount()).thenReturn(1L);
    when(pageable.getTotal()).thenReturn(1L);
    when(pageable.getTotalPages()).thenReturn(1L);
    when(pageable.getOffset()).thenReturn(0L);
    when(pageable.getLimit()).thenReturn(20L);

    when(categoryAggregationService.findById(anyString(), any()))
        .thenReturn(Optional.of(aggregation));
    when(categoryAggregationService.findByKey(anyString(), any()))
        .thenReturn(Optional.of(aggregation));
    when(categoryAggregationService.findBySlug(anyString(), any()))
        .thenReturn(Optional.of(aggregation));
    when(categoryAggregationService.getAncestorsById(any(), any()))
        .thenReturn(List.of(navigationAggregation));
    when(categoryAggregationService.getItems(any(), any())).thenReturn(pageable);
  }

  @Nested
  class getCategories {

    @Test
    void defaults() {
      final var responseEntity = controller.categories(0L, 20L);
      assertNotNull(responseEntity, "unexpected");
      assertAll(
          () -> assertEquals(OK, responseEntity.getStatusCode(), "unexpected"),
          () -> assertTrue(responseEntity.hasBody(), "unexpected"));
    }
  }

  @Nested
  class categoryByKey {

    @Test
    void defaults() {
      final var responseEntity = controller.categoryByKey("key");
      assertNotNull(responseEntity, "unexpected");
      assertAll(
          () -> assertEquals(OK, responseEntity.getStatusCode(), "unexpected"),
          () -> assertTrue(responseEntity.hasBody(), "unexpected"));
    }

    @Test
    void notFound() {
      when(categoryAggregationService.findByKey(anyString(), any()))
          .thenReturn(Optional.empty());
      final var responseEntity = controller.categoryByKey("key");
      assertNotNull(responseEntity, "unexpected");
      assertAll(
          () -> assertEquals(NOT_FOUND, responseEntity.getStatusCode(), "unexpected"),
          () -> assertFalse(responseEntity.hasBody(), "unexpected"));
    }
  }

  @Nested
  class categoryById {

    @Test
    void defaults() {
      final var responseEntity = controller.categoryById("id");
      assertNotNull(responseEntity, "unexpected");
      assertAll(
          () -> assertEquals(OK, responseEntity.getStatusCode(), "unexpected"),
          () -> assertTrue(responseEntity.hasBody(), "unexpected"));
    }

    @Test
    void notFound() {
      when(categoryAggregationService.findById(anyString(), any())).thenReturn(Optional.empty());
      final var responseEntity = controller.categoryById("key");
      assertNotNull(responseEntity, "unexpected");
      assertAll(
          () -> assertEquals(NOT_FOUND, responseEntity.getStatusCode(), "unexpected"),
          () -> assertFalse(responseEntity.hasBody(), "unexpected"));
    }
  }

  @Nested
  class categoryBySlug {

    @Test
    void defaults() {
      final var responseEntity = controller.categoryBySlug("slug");
      assertNotNull(responseEntity, "unexpected");
      assertAll(
          () -> assertEquals(OK, responseEntity.getStatusCode(), "unexpected"),
          () -> assertTrue(responseEntity.hasBody(), "unexpected"));
    }

    @Test
    void notFound() {
      when(categoryAggregationService.findBySlug(anyString(), any()))
          .thenReturn(Optional.empty());
      final var responseEntity = controller.categoryBySlug("slug");
      assertNotNull(responseEntity, "unexpected");
      assertAll(
          () -> assertEquals(NOT_FOUND, responseEntity.getStatusCode(), "unexpected"),
          () -> assertFalse(responseEntity.hasBody(), "unexpected"));
    }
  }

  @Nested
  class getCategoryTree {

    @Test
    void defaults() {
      final var responseEntity = controller.categoryTree();
      assertNotNull(responseEntity, "unexpected");
      assertAll(
          () -> assertEquals(OK, responseEntity.getStatusCode(), "unexpected"),
          () -> assertTrue(responseEntity.hasBody(), "unexpected"));
    }
  }
}
