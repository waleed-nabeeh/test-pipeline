package com.mindcurv.b2x.productcatalog.aggregator.resolver.impl;

import static com.mindcurv.b2x.commons.base.BaseAggregatorCacheSpringConfig.CATEGORY_CACHE;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import com.commercetools.api.models.category.Category;
import com.commercetools.api.models.common.LocalizedString;
import com.mindcurv.b2x.commons.commercetools.services.impl.CategoryCommercetoolsService;
import com.mindcurv.b2x.productcatalog.aggregator.services.CategoryCacheService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;


@Tag("UnitTest")
final class CategoryTreeResolverTest {

  @Mock
  private CategoryCommercetoolsService categoryCommercetoolsService;
  @Mock
  private CategoryCacheService categoryCacheService;
  @Mock
  private CacheManager cacheManager;

  @Mock
  private Category root;
  @Mock
  private Category item;
  @Mock
  private Cache cache;

  @Spy
  @InjectMocks
  private CategoryTreeResolver resolver;

  @BeforeEach
  void setUp() {
    openMocks(this);

    when(root.getKey()).thenReturn("rootKey");
    when(root.getId()).thenReturn("rootId");
    when(root.getSlug()).thenReturn(LocalizedString.builder().addValue("en", "root").build());
    when(item.getKey()).thenReturn("categoryKey");
    when(item.getId()).thenReturn("categoryId");
    when(item.getSlug()).thenReturn(LocalizedString.builder().addValue("en", "slug").build());
    when(categoryCommercetoolsService.findAll(anyList())).thenReturn(List.of(root, item));
    when(categoryCacheService.findAllCategories(anyList())).thenReturn(List.of(root, item));
    when(cacheManager.getCache(CATEGORY_CACHE)).thenReturn(cache);
  }

  @Nested
  class resolve {

    @Test
    void defaults() {
      assertNotNull(resolver.resolve(), "unexpected");
    }

    @Test
    void byArg() {
      assertNotNull(resolver.resolve("id"), "unexpected");
    }
  }

  @Test
  void getAllItems() {
    assertEquals(1, resolver.getAllItems().size(), "unexpected");
  }

  @Test
  void reload() {
    assertDoesNotThrow(resolver::reload, "unexpected");
  }

  @Test
  void getExpansions() {
    assertEquals(List.of("ancestors[*]"), resolver.getExpansions(), "unexpected");
  }
}