package com.mindcurv.b2x.productcatalog.aggregator.aggregation.impl;

import static com.mindcurv.b2x.commons.models.Pageable.emptyPageable;
import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import com.commercetools.api.models.cart.Cart;
import com.commercetools.api.models.cart.LineItem;
import com.commercetools.api.models.order.Order;
import com.commercetools.api.models.product.ProductProjection;
import com.commercetools.api.models.product.ProductVariant;
import com.commercetools.api.models.product_selection.AssignedProductSelection;
import com.commercetools.api.models.shopping_list.ShoppingList;
import com.commercetools.api.models.shopping_list.ShoppingListLineItem;
import com.commercetools.api.models.store.Store;
import com.mindcurv.b2x.commons.catalog.services.ProductListingService;
import com.mindcurv.b2x.commons.commercetools.services.impl.CartCommercetoolsService;
import com.mindcurv.b2x.commons.commercetools.services.impl.OrderCommercetoolsService;
import com.mindcurv.b2x.commons.commercetools.services.impl.ProductProjectionCommercetoolsService;
import com.mindcurv.b2x.commons.commercetools.services.impl.ShoppingListCommercetoolsService;
import com.mindcurv.b2x.commons.commercetools.services.impl.StoreCommercetoolsService;
import com.mindcurv.b2x.commons.converter.BaseConverter;
import com.mindcurv.b2x.commons.models.B2xContext;
import com.mindcurv.b2x.commons.models.Reference;
import com.mindcurv.b2x.productcatalog.aggregator.models.ListingAggregationEntry;
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
final class DefaultListingAggregationServiceTest {

  @Mock
  private ProductListingService productListingService;

  @Mock
  private CartCommercetoolsService cartCommercetoolsService;
  @Mock
  private OrderCommercetoolsService orderCommercetoolsService;
  @Mock
  private ProductProjectionCommercetoolsService productProjectionCommercetoolsService;
  @Mock
  private ShoppingListCommercetoolsService shoppingListCommercetoolsService;
  @Mock
  private StoreCommercetoolsService storeCommercetoolsService;
  @Mock
  private BaseConverter<AssignedProductSelection, ListingAggregationEntry> listingConverter;
  @Mock
  private Store store;
  @Mock
  private Order order;
  @Mock
  private Cart cart;
  @Mock
  private ShoppingList shoppinglist;

  private B2xContext context;
  @Spy
  @InjectMocks
  private DefaultListingAggregationService service;

  @BeforeEach
  void setUp() {
    openMocks(this);
    context =
        B2xContext.builder()
            .customerId("customerId")
            .store("store")
            .languages(List.of("en"))
            .build();

    when(storeCommercetoolsService.findByKey(anyString())).thenReturn(Optional.of(store));

    when(productListingService.getProductStoreAssignmentsById(any(), anyString()))
        .thenReturn(List.of(AssignedProductSelection.of()));
    when(productListingService.getProductStoreAssignmentsByKey(any(), anyString()))
        .thenReturn(List.of(AssignedProductSelection.of()));
    when(productListingService.isListed(any(), anyString())).thenReturn(true);
    when(productListingService.isListedByKey(any(), anyString())).thenReturn(true);

    final var lineItem = mock(LineItem.class);
    when(lineItem.getId()).thenReturn("lineitemId");
    when(lineItem.getProductId()).thenReturn("productId");
    when(lineItem.getProductKey()).thenReturn("productKey");
    final var variant = mock(ProductVariant.class);
    when(variant.getSku()).thenReturn("sku");
    when(lineItem.getVariant()).thenReturn(variant);

    when(cart.getLineItems()).thenReturn(List.of(lineItem));
    when(order.getLineItems()).thenReturn(List.of(lineItem));

    final var shoppingListLineItem = mock(ShoppingListLineItem.class);
    when(shoppingListLineItem.getId()).thenReturn("lineitemId");
    when(shoppingListLineItem.getProductId()).thenReturn("productId");
    when(shoppingListLineItem.getVariant()).thenReturn(variant);
    when(shoppinglist.getLineItems()).thenReturn(List.of(shoppingListLineItem));

    when(cartCommercetoolsService.findCartByCustomerId(anyString())).thenReturn(Optional.of(cart));
    when(cartCommercetoolsService.findById(anyString())).thenReturn(Optional.of(cart));
    when(orderCommercetoolsService.findById(anyString())).thenReturn(Optional.of(order));
    when(shoppingListCommercetoolsService.findById(anyString()))
        .thenReturn(Optional.of(shoppinglist));

    when(listingConverter.convertList(anyList()))
        .thenReturn(List.of(new ListingAggregationEntry()));
    when(listingConverter.convertList(List.of())).thenReturn(List.of());
    final var product = mock(ProductProjection.class);
    when(product.getId()).thenReturn("productId");
    when(product.getKey()).thenReturn("productKey");
    when(productProjectionCommercetoolsService.findBySku(anyString())).thenReturn(
        Optional.of(product));
  }

  @Nested
  class productListingsById {

    @Test
    void defaults() {
      final var result = service.productListingsById("id", context);
      assertAll(
          () -> assertFalse(emptyIfNull(result.getListings()).isEmpty(), "expected list values"),
          () -> assertTrue(result.getListed(), "expected true"));
    }

    @Test
    void noStore() {
      when(storeCommercetoolsService.findByKey(anyString())).thenReturn(Optional.empty());
      final var result = service.productListingsById("id", context);
      assertAll(
          () -> assertTrue(emptyIfNull(result.getListings()).isEmpty(), "expected empty list"),
          () -> assertFalse(result.getListed(), "expected false"));
    }

    @Test
    void blank() {
      final var result = service.productListingsById(EMPTY, context);
      assertAll(
          () -> assertTrue(emptyIfNull(result.getListings()).isEmpty(), "expected empty list"),
          () -> assertFalse(result.getListed(), "expected false"));
    }

    @Test
    void failed() {
      when(productListingService.getProductStoreAssignmentsById(any(), anyString()))
          .thenReturn(List.of());
      final var result = service.productListingsById("id", context);
      assertAll(
          () -> assertTrue(emptyIfNull(result.getListings()).isEmpty(), "expected empty list"),
          () -> assertFalse(result.getListed(), "expected false"));
    }
  }

  @Nested
  class productListingsByKey {

    @Test
    void defaults() {
      final var result = service.productListingsByKey("key", context);
      assertAll(
          () -> assertFalse(emptyIfNull(result.getListings()).isEmpty(), "expected list values"),
          () -> assertTrue(result.getListed(), "expected true"));
    }

    @Test
    void noStore() {
      when(storeCommercetoolsService.findByKey(anyString())).thenReturn(Optional.empty());
      final var result = service.productListingsByKey("key", context);
      assertAll(
          () -> assertTrue(emptyIfNull(result.getListings()).isEmpty(), "expected empty list"),
          () -> assertFalse(result.getListed(), "expected false"));
    }

    @Test
    void blank() {
      final var aggregation = service.productListingsByKey(EMPTY, context);
      assertAll(
          () -> assertTrue(emptyIfNull(aggregation.getListings()).isEmpty(), "expected empty list"),
          () -> assertFalse(aggregation.getListed(), "expected false"));
    }

    @Test
    void failed() {
      when(productListingService.getProductStoreAssignmentsByKey(any(), anyString()))
          .thenReturn(List.of());
      final var result = service.productListingsByKey("id", context);
      assertAll(
          () -> assertTrue(emptyIfNull(result.getListings()).isEmpty(), "expected empty list"),
          () -> assertFalse(result.getListed(), "expected false"));
    }
  }

  @Nested
  class productListingsBySku {

    @Test
    void defaults() {
      final var result = service.productListingsBySku("sku", context);
      assertAll(
          () -> assertFalse(emptyIfNull(result.getListings()).isEmpty(), "expected list values"),
          () -> assertTrue(result.getListed(), "expected true"));
    }

    @Test
    void failed() {
      when(productProjectionCommercetoolsService.findBySku(anyString())).thenReturn(
          Optional.empty());
      final var result = service.productListingsBySku("sku", context);
      assertAll(
          () -> assertTrue(emptyIfNull(result.getListings()).isEmpty(), "expected empty list"),
          () -> assertFalse(result.getListed(), "expected false"));
    }
  }

  @Nested
  class isListedById {

    @Test
    void defaults() {
      assertTrue(service.isListedById("id", context), "expected listing");
    }

    @Test
    void blank() {
      assertFalse(service.isListedById(EMPTY, context), "expected no listing");
    }

    @Test
    void failed() {
      when(productListingService.isListed(any(), anyString())).thenReturn(false);
      assertFalse(service.isListedById("id", context), "expected no listing");
    }
  }

  @Nested
  class isListedByKey {

    @Test
    void defaults() {
      assertTrue(service.isListedByKey("key", context), "expected listing");
    }

    @Test
    void blank() {
      assertFalse(service.isListedByKey(EMPTY, context), "expected no listing");
    }

    @Test
    void failed() {
      when(productListingService.isListedByKey(any(), anyString())).thenReturn(false);
      assertFalse(service.isListedByKey("key", context), "expected no listing");
    }
  }

  @Nested
  class productListingsByCart {

    @Test
    void defaults() {
      assertNotEquals(
          List.of(), service.productListingsByCart("id", context).getResults(),
          "expected pageable");
    }

    @Test
    void failed() {
      when(cartCommercetoolsService.findById(anyString())).thenReturn(Optional.empty());
      assertEquals(
          List.of(), service.productListingsByCart("id", context).getResults(),
          "expected empty pageable");
    }
  }

  @Nested
  class productListingsByCurrentCart {

    @Test
    void defaults() {
      assertNotEquals(
          List.of(), service.productListingsByCurrentCart(context).getResults(),
          "expected pageable");
    }

    @Test
    void failed() {
      when(cartCommercetoolsService.findCartByCustomerId(anyString())).thenReturn(Optional.empty());
      assertEquals(
          List.of(),
          service.productListingsByCurrentCart(context).getResults(),
          "expected empty pageable");
    }
  }

  @Nested
  class productListingsByOrder {

    @Test
    void defaults() {
      assertNotEquals(
          List.of(), service.productListingsByOrder("id", context).getResults(),
          "expected pageable");
    }

    @Test
    void failed() {
      when(orderCommercetoolsService.findById(anyString())).thenReturn(Optional.empty());
      assertEquals(
          List.of(),
          service.productListingsByOrder("id", context).getResults(),
          "expected empty pageable");
    }
  }

  @Nested
  class productListingsByShoppinglist {

    @Test
    void defaults() {
      assertNotEquals(
          List.of(),
          service.productListingsByShoppinglist("id", context).getResults(),
          "expected pageable");
    }

    @Test
    void failed() {
      when(shoppingListCommercetoolsService.findById(anyString())).thenReturn(Optional.empty());
      assertEquals(
          List.of(),
          service.productListingsByShoppinglist("id", context).getResults(),
          "expected empty pageable");
    }
  }

  @Nested
  class productListings {

    @Test
    void defaults() {
      assertNotNull(
          service.productListings("49995afa-bb3d-4ec1-8815-66adfd418b90", context).getReference(),
          "expected not null");
    }
  }

  @Nested
  class productListingsByReferences {

    @Test
    void defaults() {
      assertNotEquals(
          emptyPageable(),
          service.productListingsByReferences(
              List.of(new Reference().id("49995afa-bb3d-4ec1-8815-66adfd418b90")), context),
          "unexpected");
    }
  }

  @Nested
  class productListingsBySkus {

    @Test
    void defaults() {
      assertNotEquals(
          emptyPageable(), service.productListingsBySkus(List.of("sku"), context), "unexpected");
    }
  }
}
