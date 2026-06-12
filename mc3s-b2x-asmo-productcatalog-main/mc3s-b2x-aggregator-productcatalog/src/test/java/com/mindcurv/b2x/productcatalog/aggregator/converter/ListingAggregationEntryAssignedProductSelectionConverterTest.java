package com.mindcurv.b2x.productcatalog.aggregator.converter;

import static com.commercetools.api.models.product_selection.ProductSelectionMode.INDIVIDUAL;
import static com.commercetools.api.models.product_selection.ProductVariantSelection.includeAllExceptBuilder;
import static com.mindcurv.b2x.commons.models.ConverterType.DETAIL;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import com.commercetools.api.models.product_selection.AssignedProductSelection;
import com.commercetools.api.models.product_selection.ProductSelection;
import com.commercetools.api.models.product_selection.ProductSelectionReference;
import com.commercetools.api.models.product_selection.ProductVariantSelection;
import com.mindcurv.b2x.commons.models.B2xContext;
import com.mindcurv.b2x.productcatalog.aggregator.converter.impl.CommercetoolsListingAggregationEntryAssignedProductSelectionConverter;
import com.mindcurv.b2x.productcatalog.aggregator.models.ListingAggregationEntry;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

@Tag("UnitTest")
final class ListingAggregationEntryAssignedProductSelectionConverterTest {

  @Mock
  private AssignedProductSelection source;
  @Mock
  private ProductSelectionReference productSelectionReference;
  @Mock
  private B2xContext context;

  private ListingAggregationEntryAssignedProductSelectionConverter converter;

  @BeforeEach
  void setUp() {
    openMocks(this);

    final var selection = mock(ProductSelection.class);
    when(selection.getId()).thenReturn("selectionId");
    when(selection.getKey()).thenReturn("selectionKey");
    when(selection.getMode()).thenReturn(INDIVIDUAL);

    when(productSelectionReference.getId()).thenReturn("selectionIdFromReference");
    when(productSelectionReference.getObj()).thenReturn(selection);

    when(source.getProductSelection()).thenReturn(productSelectionReference);
    when(source.getVariantSelection()).thenReturn(
        ProductVariantSelection.includeOnlyBuilder().plusSkus("sku").buildUnchecked());

    converter = new CommercetoolsListingAggregationEntryAssignedProductSelectionConverter();
  }

  @Nested
  class convert {

    @Test
    void defaults() {
      final var entry = converter.convert(source);
      assertNotNull(entry, "expected not null");
      assertAll(
          () -> assertEquals("selectionIdFromReference", entry.getId(), "expected product id"),
          () -> assertEquals("selectionKey", entry.getKey(), "expected product id"),
          () -> assertEquals(List.of("sku"), entry.getIncludedSkus(), "expected list entries"),
          () -> assertEquals(List.of(), entry.getExcludedSkus(), "expected null"));
    }

    @Test
    void excluded() {
      when(productSelectionReference.getObj()).thenReturn(null);
      when(source.getVariantSelection())
          .thenReturn(
              includeAllExceptBuilder().plusSkus("sku").buildUnchecked());
      final var entry = converter.convert(source);
      assertNotNull(entry, "expected not null");
      assertAll(
          () -> assertEquals("selectionIdFromReference", entry.getId(), "expected product id"),
          () -> assertNull(entry.getKey(), "expected empty"),
          () -> assertEquals(List.of("sku"), entry.getExcludedSkus(), "expected list entries"),
          () -> assertEquals(List.of(), entry.getIncludedSkus(), "expected null"));
    }

    @Test
    void noExclusions() {
      when(source.getVariantSelection()).thenReturn(null);
      final var entry = converter.convert(source);
      assertNotNull(entry, "expected not null");
      assertAll(
          () -> assertEquals("selectionIdFromReference", entry.getId(), "expected product id"),
          () -> assertEquals(List.of(), entry.getExcludedSkus(), "expected null"),
          () -> assertEquals(List.of(), entry.getIncludedSkus(), "expected null"));
    }

    @Test
    void nullValue() {
      assertNull(converter.convert(null), "expected null");
    }
  }

  @Nested
  class processContextMapping {

    @Test
    void defaults() {
      final var entry = converter.processContextMapping(source, new ListingAggregationEntry(),
          context, DETAIL);
      assertNotNull(entry, "expected not null");
    }

    @Test
    void nullValue() {
      final var target = new ListingAggregationEntry();
      assertEquals(target, converter.processContextMapping(null, target,
          context, DETAIL), "expected target");
    }
  }
}
