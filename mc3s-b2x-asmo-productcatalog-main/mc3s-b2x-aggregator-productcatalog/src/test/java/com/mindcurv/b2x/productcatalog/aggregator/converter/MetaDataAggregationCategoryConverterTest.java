package com.mindcurv.b2x.productcatalog.aggregator.converter;

import static com.mindcurv.b2x.commons.models.ConverterType.DETAIL;
import static com.mindcurv.b2x.productcatalog.aggregator.helper.ConverterSetupHelper.initB2xContext;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import com.commercetools.api.models.category.Category;
import com.commercetools.api.models.common.LocalizedString;
import com.mindcurv.b2x.commons.base.converter.StringLocalizedStringConverter;
import com.mindcurv.b2x.commons.models.B2xContext;
import com.mindcurv.b2x.customizing.configuration.impl.CatalogCategoryConfiguration;
import com.mindcurv.b2x.productcatalog.aggregator.converter.impl.CommercetoolsMetaDataAggregationCategoryConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

@Tag("UnitTest")
final class MetaDataAggregationCategoryConverterTest {

  @Mock
  private StringLocalizedStringConverter stringLocalizedStringConverter;
  @Mock
  private CatalogCategoryConfiguration catalogCategoryConfiguration;
  @Mock
  private Category category;

  private B2xContext context;

  private MetaDataAggregationCategoryConverter converter;

  @BeforeEach
  void setUp() {
    openMocks(this);
    context = initB2xContext();
    when(category.getSlug()).thenReturn(LocalizedString.of());
    when(category.getMetaDescription()).thenReturn(LocalizedString.of());
    when(category.getMetaKeywords()).thenReturn(LocalizedString.of());
    when(category.getMetaTitle()).thenReturn(LocalizedString.of());

    when(stringLocalizedStringConverter.convert(notNull(), any())).thenReturn("localizedValue");

    converter = new CommercetoolsMetaDataAggregationCategoryConverter();
    setField(converter, "stringLocalizedStringConverter", stringLocalizedStringConverter);
    setField(converter, "catalogCategoryConfiguration", catalogCategoryConfiguration);
  }

  @Nested
  class convert {

    @Test
    void defaults() {
      final var aggregation = converter.convert(category, context);
      assertNotNull(aggregation, "unexpected");
      assertAll(
          () -> assertNotNull(aggregation.getSlug(), "unexpected slug"),
          () -> assertNull(aggregation.getDescription(), "unexpected description"),
          () -> assertNull(aggregation.getKeywords(), "unexpected keywords"),
          () -> assertNull(aggregation.getTitle(), "unexpected title"));
    }

    @Test
    void detail() {
      final var aggregation = converter.convert(category, context, DETAIL);
      assertNotNull(aggregation, "unexpected");
      assertAll(
          () -> assertNotNull(aggregation.getSlug(), "unexpected slug"),
          () -> assertNotNull(aggregation.getDescription(), "unexpected description"),
          () -> assertNotNull(aggregation.getKeywords(), "unexpected keywords"),
          () -> assertNotNull(aggregation.getTitle(), "unexpected title"));
    }

    @Test
    void nullValue() {
      assertNull(converter.convert(null, initB2xContext()), "unexpected");
    }
  }
}
