package com.mindcurv.b2x.productcatalog.aggregator.converter;

import static com.mindcurv.b2x.commons.models.ConverterType.DETAIL;
import static com.mindcurv.b2x.productcatalog.aggregator.helper.ConverterSetupHelper.initB2xContext;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import com.commercetools.api.models.category.Category;
import com.commercetools.api.models.category.CategoryReference;
import com.mindcurv.b2x.commons.models.B2xContext;
import com.mindcurv.b2x.commons.resolver.NullableBaseResolver;
import com.mindcurv.b2x.productcatalog.aggregator.converter.impl.CommercetoolsCategoryNavigationAggregationCategoryReferenceConverter;
import com.mindcurv.b2x.productcatalog.aggregator.models.CategoryNavigationAggregation;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

@Tag("UnitTest")
final class CategoryNavigationAggregationCategoryReferenceConverterTest {

  @Mock
  private NullableBaseResolver<Category> categoryResolver;
  @Mock
  private CategoryNavigationAggregationCategoryConverter categoryNavigationAggregationCategoryConverter;
  @Mock
  private CategoryReference categoryReference;
  @Mock
  private Category category;

  private B2xContext context;
  private CategoryNavigationAggregationCategoryReferenceConverter converter;

  @BeforeEach
  void setUp() {
    openMocks(this);
    context = initB2xContext();

    when(category.getId()).thenReturn("categoryId");
    when(categoryResolver.resolve(anyString())).thenReturn(Optional.of(category));

    when(categoryReference.getId()).thenReturn("categoryId");
    when(categoryReference.getObj()).thenReturn(category);
    when(
        categoryNavigationAggregationCategoryConverter.convertAsOptional(any(Category.class), any(),
            any())).thenReturn(
        Optional.of(new CategoryNavigationAggregation().id("categoryId").key("categoryKey")));

    converter = new CommercetoolsCategoryNavigationAggregationCategoryReferenceConverter();
    setField(converter, "categoryResolver", categoryResolver);
    setField(converter, "categoryNavigationAggregationCategoryConverter",
        categoryNavigationAggregationCategoryConverter);
  }

  @Nested
  final class convert {

    @Test
    void nullValue() {
      assertNull(converter.convert(null, context, DETAIL), "unexpected");
    }

    @Test
    void defaults() {
      final var aggregation = converter.convert(categoryReference, context, DETAIL);
      assertNotNull(aggregation, "unexpected");
      assertAll(
          () -> assertEquals("categoryId", aggregation.getId(), "unexpected"),
          () -> assertEquals("categoryKey", aggregation.getKey(), "unexpected"));
    }

    @Test
    void resolved() {
      when(categoryReference.getObj()).thenReturn(null);
      final var aggregation = converter.convert(categoryReference, context, DETAIL);
      assertNotNull(aggregation, "unexpected");
      assertAll(
          () -> assertEquals("categoryId", aggregation.getId(), "unexpected"),
          () -> assertEquals("categoryKey", aggregation.getKey(), "unexpected"));
    }

    @Test
    void noObjectUnresolved() {
      when(categoryReference.getObj()).thenReturn(null);
      when(categoryResolver.resolve(anyString())).thenReturn(Optional.empty());
      final var aggregation = converter.convert(categoryReference, context, DETAIL);
      assertNotNull(aggregation, "unexpected");
      assertAll(
          () -> assertEquals("categoryId", aggregation.getId(), "unexpected"),
          () -> assertNull(aggregation.getKey(), "unexpected"));
    }
  }
}
