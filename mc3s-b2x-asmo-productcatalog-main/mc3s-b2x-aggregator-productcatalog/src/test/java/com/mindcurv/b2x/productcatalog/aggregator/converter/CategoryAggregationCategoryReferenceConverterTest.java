package com.mindcurv.b2x.productcatalog.aggregator.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import com.commercetools.api.models.category.CategoryReference;
import com.mindcurv.b2x.productcatalog.aggregator.converter.impl.CommercetoolsCategoryAggregationCategoryReferenceConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

@Tag("UnitTest")
final class CategoryAggregationCategoryReferenceConverterTest {

  @Mock
  private CategoryReference categoryReference;

  private CategoryAggregationCategoryReferenceConverter converter;

  @BeforeEach
  void setUp() {
    openMocks(this);

    when(categoryReference.getId()).thenReturn("categoryId");
    converter = new CommercetoolsCategoryAggregationCategoryReferenceConverter();
  }

  @Nested
  final class convert {

    @Test
    void nullValue() {
      assertNull(converter.convert(null), "unexpected");
    }

    @Test
    void defaults() {
      final var aggregation = converter.convert(categoryReference);
      assertNotNull(aggregation, "unexpected");
      assertEquals("categoryId", aggregation.getId(), "unexpected");
    }
  }
}
