package com.mindcurv.b2x.productcatalog.aggregator.resolver.impl;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import com.commercetools.api.models.custom_object.CustomObject;
import com.commercetools.api.models.store.Store;
import com.mindcurv.b2x.commons.models.B2xContext;
import com.mindcurv.b2x.productcatalog.aggregator.models.CategoryNavigationAggregation;
import com.mindcurv.b2x.productcatalog.aggregator.services.CategoryTreeService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

@Tag("UnitTest")
final class CachedCategoryTreeResolverTest {

  @Mock
  private CategoryTreeService categoryTreeService;

  @Mock
  private Store store;
  @Mock
  private CustomObject customObject;
  private B2xContext context;

  private CachedCategoryTreeResolver resolver;

  @BeforeEach
  void setUp() {
    openMocks(this);
    context = B2xContext.builder().store("store").currentLanguage("en").languages(List.of("en"))
        .build();
    when(categoryTreeService.generateTree(any())).thenReturn(
        List.of(new CategoryNavigationAggregation()));

    resolver = new CachedCategoryTreeResolver(categoryTreeService);
  }

  @Nested
  class resolve {

    @Test
    void defaults() {
      assertFalse(resolver.resolve(context).isEmpty(), "unexpected");
    }

    @Test
    void unsupported() {
      assertTrue(resolver.resolve("unsupported").isEmpty(), "unexpected");
    }

    @Test
    void nullValue() {
      assertTrue(resolver.resolve().isEmpty(), "unexpected");
    }
  }

  @Nested
  class getTree {

    @Test
    void defaults() {
      assertFalse(resolver.getTree(context).isEmpty(), "unexpected");
    }

    @Test
    void noCache() {
      assertFalse(resolver.getTree(context).isEmpty(), "unexpected");
    }

    @Test
    void noCacheFailed() {
      when(categoryTreeService.generateTree(any())).thenReturn(List.of());
      assertTrue(resolver.getTree(context).isEmpty(), "unexpected");
    }
  }


}