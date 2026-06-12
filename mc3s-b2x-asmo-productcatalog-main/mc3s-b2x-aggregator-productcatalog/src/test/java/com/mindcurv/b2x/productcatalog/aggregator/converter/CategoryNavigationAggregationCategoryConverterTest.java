package com.mindcurv.b2x.productcatalog.aggregator.converter;

import static com.mindcurv.b2x.commons.models.ConverterType.DETAIL;
import static com.mindcurv.b2x.productcatalog.aggregator.helper.ConverterSetupHelper.initB2xContext;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import com.commercetools.api.models.category.Category;
import com.commercetools.api.models.common.LocalizedString;
import com.commercetools.api.models.customer.CustomerReference;
import com.commercetools.api.models.type.CustomFields;
import com.commercetools.api.models.type.FieldContainer;
import com.mindcurv.b2x.commons.base.converter.CustomAggregationCustomFieldsConverter;
import com.mindcurv.b2x.commons.base.converter.StringLocalizedStringConverter;
import com.mindcurv.b2x.commons.models.B2xContext;
import com.mindcurv.b2x.commons.models.CustomAggregation;
import com.mindcurv.b2x.customizing.configuration.impl.CatalogCategoryConfiguration;
import com.mindcurv.b2x.productcatalog.aggregator.converter.impl.CommercetoolsCategoryNavigationAggregationCategoryConverter;
import com.mindcurv.b2x.productcatalog.aggregator.models.CategoryNavigationAggregation;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

@Tag("UnitTest")
final class CategoryNavigationAggregationCategoryConverterTest {

  @Mock
  private CustomAggregationCustomFieldsConverter customAggregationCustomFieldsConverter;
  @Mock
  private AssetAggregationAssetConverter assetAggregationAssetConverter;
  @Mock
  private StringLocalizedStringConverter stringLocalizedStringConverter;
  @Mock
  private CatalogCategoryConfiguration catalogCategoryConfiguration;

  @Mock
  private Category category;
  private B2xContext context;

  private CategoryNavigationAggregationCategoryConverter converter;

  @BeforeEach
  void setUp() {
    openMocks(this);
    context = initB2xContext();

    when(category.getId()).thenReturn("categoryId");
    when(category.getKey()).thenReturn("categoryKey");
    when(category.getName()).thenReturn(LocalizedString.of());
    when(category.getDescription()).thenReturn(LocalizedString.of());
    when(category.getSlug()).thenReturn(LocalizedString.of());
    when(category.getId()).thenReturn("categoryId");

    final var customFields = mock(CustomFields.class);
    when(customFields.getFields()).thenReturn(
        FieldContainer.builder().addValue("restrictedTo", List.of(
            CustomerReference.builder().id("customerId").build())).build());
    when(category.getCustom()).thenReturn(customFields);

    when(customAggregationCustomFieldsConverter.convert(notNull(), any(), any())).thenReturn(
        new CustomAggregation());
    when(stringLocalizedStringConverter.convert(notNull(), any(), any())).thenReturn(
        "localizedValue");
    when(stringLocalizedStringConverter.convertAsOptional(notNull(), any(), any())).thenReturn(
        Optional.of("localizedValue"));
    when(assetAggregationAssetConverter.convertList(anyCollection(), any(), any())).thenReturn(
        List.of());

    when(catalogCategoryConfiguration.getRestrictedToName()).thenReturn(
        Optional.of("restrictedTo"));

    converter = new CommercetoolsCategoryNavigationAggregationCategoryConverter();
    setField(converter, "assetAggregationAssetConverter", assetAggregationAssetConverter);
    setField(converter, "customAggregationCustomFieldsConverter",
        customAggregationCustomFieldsConverter);
    setField(converter, "stringLocalizedStringConverter", stringLocalizedStringConverter);
    setField(converter, "catalogCategoryConfiguration", catalogCategoryConfiguration);

  }

  @Nested
  class convert {

    @Test
    void defaults() {

      final var aggregation = converter.convert(category);
      assertNotNull(aggregation, "unexpected");
      assertAll(
          () -> assertEquals("categoryId", aggregation.getId(), "unexpected"),
          () -> assertEquals("categoryKey", aggregation.getKey(), "unexpected"),
          () -> assertNull(aggregation.getName(), "unexpected"),
          () -> assertNull(aggregation.getSlug(), "unexpected"),
          () -> assertNull(aggregation.getCustom(), "unexpected")
      );
    }

    @Test
    void nullSource() {
      assertNull(converter.convert(null), "unexpected");
    }
  }

  @Nested
  class processContextMapping {

    @Test
    void defaults() {

      final var aggregation = converter.processContextMapping(category,
          new CategoryNavigationAggregation(), context, DETAIL);
      assertNotNull(aggregation, "unexpected");
      assertAll(
          () -> assertNotNull(aggregation.getName(), "unexpected"),
          () -> assertNotNull(aggregation.getSlug(), "unexpected"),
          () -> assertNotNull(aggregation.getCustom(), "unexpected")
      );
    }

    @Test
    void nullSource() {
      final var target = new CategoryNavigationAggregation();
      assertEquals(target, converter.processContextMapping(null, target,
          context, DETAIL), "unexpected");
    }

    @Test
    void nullTarget() {
      assertNull(converter.processContextMapping(category, null, context, DETAIL), "unexpected");
    }
  }
}
