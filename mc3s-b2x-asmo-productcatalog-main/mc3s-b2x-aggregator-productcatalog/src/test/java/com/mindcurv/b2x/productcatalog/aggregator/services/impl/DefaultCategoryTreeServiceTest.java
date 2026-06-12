package com.mindcurv.b2x.productcatalog.aggregator.services.impl;

import static com.mindcurv.b2x.productcatalog.aggregator.services.impl.DefaultCategoryTreeService.DEFAULT_LEVELS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import com.commercetools.api.models.category.Category;
import com.commercetools.api.models.category.CategoryReference;
import com.commercetools.api.models.category.CategoryTree;
import com.commercetools.api.models.common.LocalizedString;
import com.commercetools.api.models.custom_object.CustomObject;
import com.commercetools.api.models.product_selection.ProductSelection;
import com.commercetools.api.models.product_selection.ProductSelectionReference;
import com.commercetools.api.models.store.ProductSelectionSetting;
import com.commercetools.api.models.store.Store;
import com.commercetools.api.models.type.CustomFields;
import com.commercetools.api.models.type.FieldContainer;
import com.mindcurv.b2x.commons.models.B2xContext;
import com.mindcurv.b2x.commons.resolver.BaseResolver;
import com.mindcurv.b2x.commons.resolver.NullableBaseResolver;
import com.mindcurv.b2x.customizing.configuration.impl.ProductSelectionConfiguration;
import com.mindcurv.b2x.customizing.configuration.impl.StoreConfiguration;
import com.mindcurv.b2x.productcatalog.aggregator.converter.CategoryNavigationAggregationCategoryConverter;
import com.mindcurv.b2x.productcatalog.aggregator.models.CategoryNavigationAggregation;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang3.Range;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

@Tag("UnitTest")
final class DefaultCategoryTreeServiceTest {

  @Mock
  private NullableBaseResolver<Store> storeResolver;
  @Mock
  private BaseResolver<CategoryTree> categoryTreeBaseResolver;
  @Mock
  private CategoryNavigationAggregationCategoryConverter categoryNavigationAggregationConverter;
  @Mock
  private ProductSelectionConfiguration productSelectionConfiguration;
  @Mock
  private StoreConfiguration storeConfiguration;

  @Mock
  private Store store;
  @Mock
  private CustomObject customObject;
  @Mock
  private Category category;
  @Mock
  private Range<Integer> range;
  private CategoryTree categoryTree;
  private B2xContext context;

  private DefaultCategoryTreeService service;

  @BeforeEach
  void setUp() {
    openMocks(this);
    context = B2xContext.builder().store("store").currentLanguage("en").languages(List.of("en"))
        .build();

    when(range.getMaximum()).thenReturn(5);
    when(store.getKey()).thenReturn("key");
    when(store.getLanguages()).thenReturn(List.of("de", "en"));
    when(store.getProductSelections()).thenReturn(List.of(
            ProductSelectionSetting.builder().active(true)
                .productSelection(ProductSelectionReference.builder().id("selectionId1").obj(
                    ProductSelection.builder().custom(CustomFields.builder()
                        .fields(FieldContainer.builder()
                            .values(Map.of("catalogKeys", List.of("categoryKey")))
                            .buildUnchecked())
                        .buildUnchecked()).buildUnchecked()).build()).build(),
            ProductSelectionSetting.builder().active(false).buildUnchecked()
        )
    );
    when(store.getCustom()).thenReturn(CustomFields.builder()
        .fields(FieldContainer.builder().addValue("navigationLevels", 5.0d).build())
        .buildUnchecked());

    when(category.getKey()).thenReturn("categoryKey");
    when(category.getId()).thenReturn("categoryId");
    when(category.getSlug()).thenReturn(LocalizedString.of());
    when(category.getName()).thenReturn(LocalizedString.of());

    final var childCategory = mock(Category.class);
    when(childCategory.getKey()).thenReturn("childCategoryKey");
    when(childCategory.getId()).thenReturn("childCategoryId");
    when(childCategory.getSlug()).thenReturn(LocalizedString.of());
    when(childCategory.getName()).thenReturn(LocalizedString.of());
    when(childCategory.getParent()).thenReturn(
        CategoryReference.builder().id("categoryId").build());

    when(storeResolver.resolve(anyString())).thenReturn(Optional.of(store));
    when(storeResolver.getAllItems()).thenReturn(List.of(store));

    categoryTree = CategoryTree.of(List.of(category, childCategory));
    when(categoryTreeBaseResolver.resolve()).thenReturn(categoryTree);
    when(categoryTreeBaseResolver.resolve(anyString())).thenReturn(categoryTree);
    when(categoryNavigationAggregationConverter.convertAsOptional(notNull(), any())).thenReturn(
        Optional.of(new CategoryNavigationAggregation()));
    when(categoryNavigationAggregationConverter.convertAsOptional(eq(null), any())).thenReturn(
        Optional.empty());
    when(customObject.getValue()).thenReturn(
        List.of(new CategoryNavigationAggregation().id("cachedId")));

    when(productSelectionConfiguration.getCatalogKeysName()).thenReturn(Optional.of("catalogKeys"));

    when(storeConfiguration.getNavigationLevelsName()).thenReturn(Optional.of("navigationLevels"));

    service = new DefaultCategoryTreeService(storeResolver,
        categoryTreeBaseResolver,
        categoryNavigationAggregationConverter,
        productSelectionConfiguration,
        storeConfiguration
    );

  }

  @Nested
  class generateTree {

    @Test
    void defaults() {
      final var result = service.generateTree(context);
      assertFalse(result.isEmpty(), "unexpected");
    }
  }

  @Nested
  class getStoreCategoryTrees {

    @Test
    void defaults() {
      final var result = service.getStoreCategoryTrees(context);
      assertFalse(result.getAllAsFlatList().isEmpty(), "unexpected");
    }

    @Test
    void noKeys() {
      when(productSelectionConfiguration.getCatalogKeysName()).thenReturn(Optional.empty());
      final var result = service.getStoreCategoryTrees(context);
      assertFalse(result.getAllAsFlatList().isEmpty(), "unexpected");
    }
  }

  @Nested
  class getStoreCatalogs {

    @Test
    void defaults() {
      assertFalse(service.getStoreCatalogs(context).isEmpty(), "unexpected");
    }

    @Test
    void failed() {
      when(store.getProductSelections()).thenReturn(List.of());
      assertTrue(service.getStoreCatalogs(context).isEmpty(), "unexpected");
    }

    @Test
    void noAtt() {
      when(productSelectionConfiguration.getCatalogKeysName()).thenReturn(Optional.empty());
      assertTrue(service.getStoreCatalogs(context).isEmpty(), "unexpected");
    }
  }

  @Test
  void processChildren() {
    final var result = service.processChildren(category, categoryTree, 0, range, context);
    assertNotNull(result, "unexpected");
  }

  @Nested
  class getMaxLevels {

    @Test
    void defaults() {
      assertEquals(5, service.getMaxLevels(context), "unexpected");
    }

    @Test
    void noAttName() {
      when(storeConfiguration.getNavigationLevelsName()).thenReturn(Optional.empty());
      assertEquals(DEFAULT_LEVELS, service.getMaxLevels(context), "unexpected");
    }

    @Test
    void noCustom() {
      when(store.getCustom()).thenReturn(null);
      assertEquals(DEFAULT_LEVELS, service.getMaxLevels(context), "unexpected");
    }

    @Test
    void noCustomField() {
      when(storeConfiguration.getNavigationLevelsName()).thenReturn(Optional.of("unknown"));
      assertEquals(DEFAULT_LEVELS, service.getMaxLevels(context), "unexpected");
    }

    @Test
    void noCStore() {
      when(storeResolver.resolve(anyString())).thenReturn(Optional.empty());
      assertEquals(DEFAULT_LEVELS, service.getMaxLevels(context), "unexpected");
    }
  }

}