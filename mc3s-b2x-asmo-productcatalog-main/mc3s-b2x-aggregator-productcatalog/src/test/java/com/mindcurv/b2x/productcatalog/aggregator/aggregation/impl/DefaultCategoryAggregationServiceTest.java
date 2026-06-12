package com.mindcurv.b2x.productcatalog.aggregator.aggregation.impl;

import static com.mindcurv.b2x.commons.models.Pageable.initPageableFromList;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import com.commercetools.api.models.ResourcePagedQueryResponse;
import com.commercetools.api.models.category.Category;
import com.mindcurv.b2x.commons.commercetools.services.impl.CategoryCommercetoolsService;
import com.mindcurv.b2x.commons.models.B2xContext;
import com.mindcurv.b2x.commons.models.PageableSearchRequest;
import com.mindcurv.b2x.commons.resolver.BaseResolver;
import com.mindcurv.b2x.commons.resolver.NullableBaseResolver;
import com.mindcurv.b2x.productcatalog.aggregator.converter.CategoryAggregationCategoryConverter;
import com.mindcurv.b2x.productcatalog.aggregator.converter.CategoryNavigationAggregationCategoryReferenceConverter;
import com.mindcurv.b2x.productcatalog.aggregator.models.CategoryAggregation;
import com.mindcurv.b2x.productcatalog.aggregator.models.CategoryNavigationAggregation;
import java.util.Collection;
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
final class DefaultCategoryAggregationServiceTest {

  @Mock
  private CategoryCommercetoolsService categoryCommercetoolsService;
  @Mock
  private CategoryAggregationCategoryConverter categoryAggregationConverter;
  @Mock
  private BaseResolver<Collection<CategoryNavigationAggregation>> categoryListResolver;
  @Mock
  private CategoryNavigationAggregationCategoryReferenceConverter categoryNavigationConverter;
  @Mock
  private ResourcePagedQueryResponse<Category> pagedQueryResponse;
  @Mock
  private Category category;
  @Mock
  private NullableBaseResolver<Category> categoryResolver;

  @Mock
  private PageableSearchRequest searchRequest;
  private B2xContext context;

  @Spy
  @InjectMocks
  private DefaultCategoryAggregationService service;

  @BeforeEach
  void setUp() {
    openMocks(this);
    context = B2xContext.builder().store("store").currentLanguage("en").languages(List.of("en"))
        .build();
    when(pagedQueryResponse.getResults()).thenReturn(List.of(category));
    when(categoryCommercetoolsService.getItems(any())).thenReturn(pagedQueryResponse);
    when(categoryCommercetoolsService.findBySlug(anyString(), anyString(), anyList())).thenReturn(
        Optional.of(category));
    when(categoryCommercetoolsService.findByKey(anyString(), anyList())).thenReturn(
        Optional.of(category));
    when(categoryCommercetoolsService.findById(anyString(), anyList())).thenReturn(
        Optional.of(category));

    final var categoryAggregation = mock(CategoryAggregation.class);
    when(categoryAggregationConverter.convert(any(), any(), any())).thenReturn(categoryAggregation);
    when(categoryAggregationConverter.convert(any(), any())).thenReturn(categoryAggregation);
    when(categoryAggregationConverter.convertPageableFromResponse(any(), any()))
        .thenReturn(initPageableFromList(List.of(categoryAggregation)));

    when(categoryListResolver.resolve(any())).thenReturn(List.of());

    service = new DefaultCategoryAggregationService(categoryCommercetoolsService,
        categoryNavigationConverter,
        categoryAggregationConverter,
        categoryListResolver,
        categoryResolver
    );
  }

  @Test
  void getItems() {
    assertNotNull(service.getItems(context, searchRequest), "unexpected");
  }

  @Nested
  class getItemByKey {

    @Test
    void defaults() {
      assertNotNull(service.findByKey("key", context), "unexpected");
    }

    @Test
    void nullValue() {
      assertTrue(service.findByKey(null, context).isEmpty(), "unexpected");
    }

    @Test
    void empty() {
      assertTrue(service.findByKey(EMPTY, context).isEmpty(), "unexpected");
    }
  }

  @Nested
  class getItemById {

    @Test
    void defaults() {
      assertNotNull(service.findById("id", context), "unexpected");
    }

    @Test
    void nullValue() {
      assertTrue(service.findById(null, context).isEmpty(), "unexpected");
    }

    @Test
    void empty() {
      assertTrue(service.findById(EMPTY, context).isEmpty(), "unexpected");
    }
  }

  @Nested
  class getItemBySlug {

    @Test
    void defaults() {
      assertNotNull(service.findBySlug("slug", context), "unexpected");
    }

    @Test
    void nullValue() {
      assertTrue(service.findBySlug(null, context).isEmpty(), "unexpected");
    }

    @Test
    void empty() {
      assertTrue(service.findBySlug(EMPTY, context).isEmpty(), "unexpected");
    }
  }

  @Test
  void getAncestorsById() {
    assertNotNull(service.getAncestorsById("id", context), "unexpected");
  }

}
