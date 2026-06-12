package com.mindcurv.b2x.productcatalog.aggregator.converter;

import static com.mindcurv.b2x.commons.models.B2xContext.initBuilder;
import static com.mindcurv.b2x.commons.models.ConverterType.DETAIL;
import static com.mindcurv.b2x.commons.models.ConverterType.LIST;
import static com.mindcurv.b2x.productcatalog.aggregator.helper.ConverterSetupHelper.initB2xContext;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import com.commercetools.api.models.category.Category;
import com.commercetools.api.models.category.CategoryReference;
import com.commercetools.api.models.category.CategoryTree;
import com.commercetools.api.models.common.Asset;
import com.commercetools.api.models.common.LocalizedString;
import com.commercetools.api.models.customer.CustomerReference;
import com.commercetools.api.models.type.CustomFields;
import com.commercetools.api.models.type.FieldContainer;
import com.mindcurv.b2x.commons.base.converter.CustomAggregationCustomFieldsConverter;
import com.mindcurv.b2x.commons.base.converter.StringLocalizedStringConverter;
import com.mindcurv.b2x.commons.models.B2xContext;
import com.mindcurv.b2x.commons.models.CustomAggregation;
import com.mindcurv.b2x.commons.resolver.BaseResolver;
import com.mindcurv.b2x.customizing.configuration.impl.CatalogCategoryConfiguration;
import com.mindcurv.b2x.productcatalog.aggregator.converter.impl.CommercetoolsCategoryAggregationCategoryConverter;
import com.mindcurv.b2x.productcatalog.aggregator.models.AssetAggregation;
import com.mindcurv.b2x.productcatalog.aggregator.models.AssetSourceAggregation;
import com.mindcurv.b2x.productcatalog.aggregator.models.CategoryAggregation;
import com.mindcurv.b2x.productcatalog.aggregator.models.CategoryNavigationAggregation;
import com.mindcurv.b2x.productcatalog.aggregator.models.MetaDataAggregation;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

@Tag("UnitTest")
final class CategoryAggregationCategoryConverterTest {

  @Mock
  private BaseResolver<CategoryTree> categoryTreeResolver;
  @Mock
  private CategoryNavigationAggregationCategoryReferenceConverter categoryNavigationAggregationCategoryReferenceConverter;
  @Mock
  private CategoryNavigationAggregationCategoryConverter categoryNavigationAggregationCategoryConverter;
  @Mock
  private CustomAggregationCustomFieldsConverter customAggregationCustomFieldsConverter;
  @Mock
  private AssetAggregationAssetConverter assetAggregationAssetConverter;
  @Mock
  private StringLocalizedStringConverter stringLocalizedStringConverter;
  @Mock
  private MetaDataAggregationCategoryConverter metaDataAggregationCategoryConverter;

  @Mock
  private Category category;
  @Mock
  private AssetAggregation assetAggregation;
  @Mock
  private AssetSourceAggregation assetSourceAggregation;
  @Mock
  private CategoryReference categoryReference;

  @Mock
  private CatalogCategoryConfiguration catalogCategoryConfiguration;

  private B2xContext context;
  private CategoryAggregationCategoryConverter converter;

  @BeforeEach
  void setUp() {
    openMocks(this);
    context = initB2xContext();

    when(category.getId()).thenReturn("categoryId");
    when(category.getKey()).thenReturn("categoryKey");
    when(category.getName()).thenReturn(LocalizedString.of());
    when(category.getSlug()).thenReturn(LocalizedString.of());
    when(category.getDescription()).thenReturn(LocalizedString.of());
    when(category.getMetaDescription()).thenReturn(LocalizedString.of());
    when(category.getId()).thenReturn("categoryId");
    when(category.getAssets()).thenReturn(List.of(mock(Asset.class)));
    when(categoryReference.getId()).thenReturn("ancestorId");
    when(categoryReference.getObj()).thenReturn(category);
    when(category.getAncestors()).thenReturn(singletonList(categoryReference));

    final var customFields = mock(CustomFields.class);
    when(customFields.getFields()).thenReturn(
        FieldContainer.builder().addValue("restrictedTo", List.of(
            CustomerReference.builder().id("customerId").build())).build());
    when(category.getCustom()).thenReturn(customFields);

    when(categoryNavigationAggregationCategoryConverter.convertList(anyCollection(), any(),
        any())).thenReturn(List.of(new CategoryNavigationAggregation()));
    when(categoryNavigationAggregationCategoryReferenceConverter.convertList(anyCollection(), any(),
        any())).thenReturn(List.of(new CategoryNavigationAggregation()));
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
    when(stringLocalizedStringConverter.convertAsOptional(notNull(), any(), any())).thenReturn(
        Optional.of("localizedValue"));

    when(catalogCategoryConfiguration.getRestrictedToName()).thenReturn(
        Optional.of("restrictedTo"));
    final var categoryTree = CategoryTree.of(List.of(category));

    when(categoryTreeResolver.resolve()).thenReturn(categoryTree);
    when(categoryTreeResolver.resolve(anyString())).thenReturn(categoryTree);

    converter = new CommercetoolsCategoryAggregationCategoryConverter();
    setField(converter, "categoryTreeResolver", categoryTreeResolver);
    setField(converter, "categoryNavigationAggregationCategoryReferenceConverter",
        categoryNavigationAggregationCategoryReferenceConverter);
    setField(converter, "customAggregationCustomFieldsConverter",
        customAggregationCustomFieldsConverter);
    setField(converter, "categoryNavigationAggregationCategoryConverter",
        categoryNavigationAggregationCategoryConverter);
    setField(converter, "assetAggregationAssetConverter", assetAggregationAssetConverter);
    setField(converter, "metaDataAggregationCategoryConverter",
        metaDataAggregationCategoryConverter);
    setField(converter, "stringLocalizedStringConverter", stringLocalizedStringConverter);
    setField(converter, "catalogCategoryConfiguration", catalogCategoryConfiguration);

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
      assertNull(converter.convert(null), "unexpected");
    }
  }

  @Nested
  final class processContextMapping {

    @Test
    void restricted() {
      assertNull(converter.processContextMapping(category, new CategoryAggregation(),
              initBuilder(context).customerId("anotherId").build(),
              DETAIL),
          "unexpected");
    }

    @Test
    void detail() {

      final var aggregation = converter.convert(category, context, DETAIL);
      assertNotNull(aggregation, "unexpected");
      assertAll(
          () -> assertEquals("categoryId", aggregation.getId(), "unexpected id"),
          () -> assertEquals("categoryKey", aggregation.getKey(), "unexpected key"),
          () -> assertNotNull(aggregation.getName(), "unexpected name"),
          () -> assertNotNull(aggregation.getDescription(), "unexpected description"),
          () -> assertNotNull(aggregation.getMetaData(), "unexpected meta data"),
          () -> assertNotNull(aggregation.getAssets(), "unexpected assets"),
          () -> assertNotNull(aggregation.getImage(), "unexpected image"),
          () -> assertNotNull(aggregation.getAncestors(), "unexpected ancestors"),
          () -> assertNull(aggregation.getParent(), "unexpected parent"),
          () -> assertNotNull(aggregation.getChildren(), "unexpected children"));
    }

    @Test
    void list() {

      final var aggregation = converter.convert(category, context, LIST);
      assertNotNull(aggregation, "unexpected");
      assertAll(
          () -> assertEquals("categoryId", aggregation.getId(), "unexpected"),
          () -> assertEquals("categoryKey", aggregation.getKey(), "unexpected"),
          () -> assertNotNull(aggregation.getName(), "unexpected name"),
          () -> assertNotNull(aggregation.getDescription(), "unexpected description"),
          () -> assertNotNull(aggregation.getImage(), "unexpected image"),
          () -> assertNull(aggregation.getMetaData(), "unexpected meta data"),
          () -> assertEquals(List.of(), aggregation.getAssets(), "unexpected assets"),
          () -> assertNull(aggregation.getParent(), "unexpected parent"),
          () -> assertEquals(List.of(), aggregation.getChildren(), "unexpected children"));
    }

  }
}
