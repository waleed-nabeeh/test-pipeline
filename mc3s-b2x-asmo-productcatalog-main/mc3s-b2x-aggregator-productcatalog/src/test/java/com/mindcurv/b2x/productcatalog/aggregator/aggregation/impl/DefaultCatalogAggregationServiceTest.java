package com.mindcurv.b2x.productcatalog.aggregator.aggregation.impl;

import static org.apache.commons.collections4.CollectionUtils.emptyCollection;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import com.commercetools.api.models.ResourcePagedQueryResponse;
import com.commercetools.api.models.category.Category;
import com.commercetools.api.models.product_selection.ProductSelection;
import com.commercetools.api.models.product_selection.ProductSelectionReference;
import com.commercetools.api.models.store.ProductSelectionSetting;
import com.commercetools.api.models.store.Store;
import com.commercetools.api.models.type.CustomFields;
import com.commercetools.api.models.type.FieldContainer;
import com.commercetools.api.models.type.Type;
import com.mindcurv.b2x.commons.commercetools.services.impl.CategoryCommercetoolsService;
import com.mindcurv.b2x.commons.commercetools.services.impl.StoreCommercetoolsService;
import com.mindcurv.b2x.commons.models.B2xContext;
import com.mindcurv.b2x.commons.models.PageableSearchRequest;
import com.mindcurv.b2x.commons.resolver.NullableBaseResolver;
import com.mindcurv.b2x.customizing.configuration.impl.CatalogCategoryConfiguration;
import com.mindcurv.b2x.customizing.configuration.impl.ProductSelectionConfiguration;
import com.mindcurv.b2x.productcatalog.aggregator.converter.CatalogAggregationCategoryConverter;
import com.mindcurv.b2x.productcatalog.aggregator.models.CatalogAggregation;
import com.mindcurv.b2x.productcatalog.dto.converter.CatalogAggregationCatalogDTOConverter;
import com.mindcurv.b2x.productcatalog.dto.services.CatalogDTOService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

@Tag("UnitTest")
final class DefaultCatalogAggregationServiceTest {

  @Mock
  private CategoryCommercetoolsService categoryCommercetoolsService;
  @Mock
  private StoreCommercetoolsService storeCommercetoolsService;
  @Mock
  private NullableBaseResolver<Type> typeResolver;
  @Mock
  private CatalogAggregationCategoryConverter catalogAggregationConverter;
  @Mock
  private CatalogCategoryConfiguration catalogCategoryConfiguration;
  @Mock
  private ProductSelectionConfiguration productSelectionConfiguration;

  @Mock
  private CatalogAggregationCatalogDTOConverter catalogAggregationCatalogDTOConverter;
  @Mock
  private CatalogDTOService catalogDTOService;

  @Mock
  private ResourcePagedQueryResponse<Category> pagedQueryResponse;
  @Mock
  private Category category;
  @Mock
  private ProductSelectionSetting productSelectionSetting;

  @Mock
  private PageableSearchRequest searchRequest;
  private B2xContext context;
  @Spy
  @InjectMocks
  private DefaultCatalogAggregationService service;

  @BeforeEach
  void setUp() {
    openMocks(this);
    context = B2xContext.builder()
        .store("store")
        .currentLanguage("en")
        .build();
    when(pagedQueryResponse.getResults()).thenReturn(List.of(category));
    when(categoryCommercetoolsService.getItems(any(), anyLong(), anyLong()))
        .thenReturn(pagedQueryResponse);

    when(category.getKey()).thenReturn("categoryKey");

    final var catalogAggregation = mock(CatalogAggregation.class);
    when(catalogAggregationConverter.convertAsOptional(notNull(), any())).thenReturn(
        Optional.of(catalogAggregation));
    when(catalogAggregationConverter.convertAsOptional(eq(null), any())).thenReturn(
        Optional.empty());

    final var type = mock(Type.class);
    when(catalogCategoryConfiguration.getTypeKey()).thenReturn("typeKey");
    when(productSelectionConfiguration.getCatalogKeysName()).thenReturn(Optional.of("catalogKeys"));
    when(typeResolver.resolve(any())).thenReturn(Optional.of(type));
    final var store = mock(Store.class);
    when(store.getKey()).thenReturn("storeKey");
    final var selection = mock(ProductSelection.class);
    when(selection.getCustom()).thenReturn(CustomFields.builder()
        .fields(FieldContainer.builder().addValue("catalogKeys", List.of("categoryKey")).build())
        .buildUnchecked());
    when(productSelectionSetting.getActive()).thenReturn(true);
    when(productSelectionSetting.getProductSelection()).thenReturn(
        ProductSelectionReference.builder().id("selectionId").obj(selection).build());
    when(store.getProductSelections()).thenReturn(List.of(productSelectionSetting));
    when(storeCommercetoolsService.findByKey(anyString(), anyList())).thenReturn(
        Optional.of(store));

    when(catalogDTOService.getActiveItems(anyString())).thenReturn(emptyCollection());
    when(catalogAggregationCatalogDTOConverter.convertList(any())).thenReturn(emptyCollection());
  }

  @Nested
  class getCatalogs {

    @Test
    void defaults() {
      final var result = service.getCatalogs(context, searchRequest);
      assertAll(
          () -> assertFalse(result.getResults().isEmpty(), "expected results")
      );
    }

    @Test
    void noType() {
      when(typeResolver.resolve(any())).thenReturn(Optional.empty());
      final var result = service.getCatalogs(context, searchRequest);
      assertAll(
          () -> assertTrue(result.getResults().isEmpty(), "expected empty")
      );
    }

    @Test
    void inactive() {
      when(productSelectionSetting.getActive()).thenReturn(false);
      final var result = service.getCatalogs(context, searchRequest);
      assertAll(
          () -> assertTrue(result.getResults().isEmpty(), "expected empty")
      );
    }

    @Test
    void noStore() {
      when(storeCommercetoolsService.findByKey(anyString(), anyList())).thenReturn(
          Optional.empty());
      final var result = service.getCatalogs(context, searchRequest);
      assertAll(
          () -> assertTrue(result.getResults().isEmpty(), "expected empty")
      );
    }
  }

  @Test
  void getStoreExpands() {
    assertEquals(List.of("productSelections[*].productSelection"), service.getStoreExpands(),
        "expected store expands");
  }
}
