package com.mindcurv.b2x.productcatalog.aggregator.converter;

import static com.mindcurv.b2x.commons.models.ConverterType.DETAIL;
import static com.mindcurv.b2x.commons.models.ConverterType.LIST;
import static com.mindcurv.b2x.productcatalog.aggregator.helper.ConverterSetupHelper.initB2xContext;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
import com.commercetools.api.models.category.CategoryReference;
import com.commercetools.api.models.common.Asset;
import com.commercetools.api.models.common.LocalizedString;
import com.mindcurv.b2x.commons.base.converter.CustomAggregationCustomFieldsConverter;
import com.mindcurv.b2x.commons.base.converter.StringLocalizedStringConverter;
import com.mindcurv.b2x.commons.models.B2xContext;
import com.mindcurv.b2x.commons.models.CustomAggregation;
import com.mindcurv.b2x.productcatalog.aggregator.converter.impl.CommercetoolsCatalogAggregationCategoryConverter;
import com.mindcurv.b2x.productcatalog.aggregator.models.AssetAggregation;
import com.mindcurv.b2x.productcatalog.aggregator.models.AssetSourceAggregation;
import com.mindcurv.b2x.productcatalog.aggregator.models.CatalogAggregation;
import com.mindcurv.b2x.productcatalog.aggregator.models.MetaDataAggregation;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

@Tag("UnitTest")
final class CatalogAggregationCategoryConverterTest {

  @Mock
  private CustomAggregationCustomFieldsConverter customAggregationCustomFieldsConverter;
  @Mock
  private AssetAggregationAssetConverter assetAggregationAssetConverter;
  @Mock
  private MetaDataAggregationCategoryConverter metaDataAggregationCategoryConverter;
  @Mock
  private StringLocalizedStringConverter stringLocalizedStringConverter;

  @Mock
  private Category category;
  @Mock
  private AssetAggregation assetAggregation;
  @Mock
  private AssetSourceAggregation assetSourceAggregation;
  @Mock
  private CategoryReference categoryReference;

  private B2xContext context;
  private CatalogAggregationCategoryConverter converter;

  @BeforeEach
  void setUp() {
    openMocks(this);
    context = initB2xContext();

    when(category.getId()).thenReturn("categoryId");
    when(category.getKey()).thenReturn("categoryKey");
    when(category.getName()).thenReturn(LocalizedString.of());
    when(category.getDescription()).thenReturn(LocalizedString.of());
    when(category.getSlug()).thenReturn(LocalizedString.of());
    when(category.getMetaDescription()).thenReturn(LocalizedString.of());
    when(category.getId()).thenReturn("categoryId");
    when(category.getAssets()).thenReturn(List.of(mock(Asset.class)));
    when(categoryReference.getId()).thenReturn("ancestorId");
    when(category.getAncestors()).thenReturn(singletonList(categoryReference));

    when(assetAggregation.getSources()).thenReturn(List.of(assetSourceAggregation));
    when(assetSourceAggregation.getUri()).thenReturn("uri");

    when(assetAggregationAssetConverter.convertList(anyCollection(), any(), any()))
        .thenReturn(List.of(assetAggregation));
    when(customAggregationCustomFieldsConverter.convert(notNull(), any(), any())).thenReturn(
        new CustomAggregation());
    when(metaDataAggregationCategoryConverter.convert(notNull(), any(), any()))
        .thenReturn(mock(MetaDataAggregation.class));
    when(stringLocalizedStringConverter.convert(notNull(), any(), any())).thenReturn(
        "localizedValue");

    converter = new CommercetoolsCatalogAggregationCategoryConverter();
    setField(converter, "customAggregationCustomFieldsConverter",
        customAggregationCustomFieldsConverter);
    setField(converter, "assetAggregationAssetConverter", assetAggregationAssetConverter);
    setField(converter, "metaDataAggregationCategoryConverter",
        metaDataAggregationCategoryConverter);
    setField(converter, "stringLocalizedStringConverter", stringLocalizedStringConverter);

  }

  @Nested
  final class convert {

    @Test
    void defaults() {

      final var aggregation = converter.convert(category);
      assertNotNull(aggregation, "unexpected");
      assertAll(
          () -> assertEquals("categoryId", aggregation.getId(), "unexpected id"),
          () -> assertEquals("categoryKey", aggregation.getKey(), "unexpected key")
      );
    }

    @Test
    void nullValue() {
      assertNull(converter.convert(null, context), "unexpected");
    }
  }

  @Nested
  final class processContextMapping {

    @Test
    void list() {
      final var aggregation = converter.processContextMapping(category, new CatalogAggregation(),
          context, LIST);
      assertNotNull(aggregation, "unexpected");
      assertAll(
          () -> assertNotNull(aggregation.getName(), "unexpected name"),
          () -> assertNotNull(aggregation.getDescription(), "unexpected description"),
          () -> assertNotNull(aggregation.getSlug(), "unexpected slug"),
          () -> assertNotNull(aggregation.getImage(), "unexpected image"),
          () -> assertNull(aggregation.getMetaData(), "unexpected meta data"),
          () -> assertEquals(List.of(), aggregation.getAssets(), "unexpected assets"));
    }

    @Test
    void detail() {
      final var aggregation = converter.processContextMapping(category, new CatalogAggregation(),
          context, DETAIL);
      assertNotNull(aggregation, "unexpected");
      assertAll(
          () -> assertNotNull(aggregation.getName(), "unexpected name"),
          () -> assertNotNull(aggregation.getDescription(), "unexpected description"),
          () -> assertNotNull(aggregation.getSlug(), "unexpected slug"),
          () -> assertNotNull(aggregation.getImage(), "unexpected image"),
          () -> assertNotNull(aggregation.getMetaData(), "unexpected meta data"),
          () -> assertFalse(aggregation.getAssets().isEmpty(), "unexpected assets"));
    }

    @Test
    void nullValue() {
      final var target = new CatalogAggregation();
      assertEquals(target, converter.processContextMapping(null, target, context, DETAIL),
          "unexpected");
    }

  }
}
