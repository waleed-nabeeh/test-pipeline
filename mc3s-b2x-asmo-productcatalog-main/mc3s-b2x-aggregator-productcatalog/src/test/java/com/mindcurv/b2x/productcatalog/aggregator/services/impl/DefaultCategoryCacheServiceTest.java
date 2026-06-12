package com.mindcurv.b2x.productcatalog.aggregator.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import com.commercetools.api.models.category.Category;
import com.mindcurv.b2x.commons.commercetools.services.impl.CategoryCommercetoolsService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

@Tag("UnitTest")
final class DefaultCategoryCacheServiceTest {

  @Mock
  private CategoryCommercetoolsService categoryCommercetoolsService;

  @Mock
  private Category item;

  @Spy
  @InjectMocks
  private DefaultCategoryCacheService service;

  @BeforeEach
  void setUp() {
    openMocks(this);
    when(categoryCommercetoolsService.findAll(anyList())).thenReturn(List.of(item));
  }

  @Nested
  class findAllCategories {

    @Test
    void defaults() {
      assertEquals(1, service.findAllCategories(anyList()).size(), "Result size should be 1");
    }

    @Test
    void noCategories() {
      assertFalse(service.findAllCategories(anyList()).isEmpty(), "unexpected");
    }

    @Test
    void noCategoriesFailed() {
      when(categoryCommercetoolsService.findAll(anyList())).thenReturn(List.of());
      assertTrue(service.findAllCategories(anyList()).isEmpty(), "unexpected");
    }
  }
}
