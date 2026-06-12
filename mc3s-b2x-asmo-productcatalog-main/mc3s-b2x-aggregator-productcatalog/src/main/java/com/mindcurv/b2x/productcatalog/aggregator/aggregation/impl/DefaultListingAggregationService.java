package com.mindcurv.b2x.productcatalog.aggregator.aggregation.impl;

import static com.commercetools.api.models.common.ReferenceTypeId.PRODUCT;
import static com.mindcurv.b2x.commons.helper.CompletableFutureHelper.allCompletableFutureOf;
import static com.mindcurv.b2x.commons.models.Pageable.emptyPageable;
import static com.mindcurv.b2x.commons.models.Pageable.initPageableFromList;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import com.commercetools.api.models.product_selection.AssignedProductSelection;
import com.mindcurv.b2x.commons.catalog.services.ProductListingService;
import com.mindcurv.b2x.commons.commercetools.services.impl.CartCommercetoolsService;
import com.mindcurv.b2x.commons.commercetools.services.impl.OrderCommercetoolsService;
import com.mindcurv.b2x.commons.commercetools.services.impl.ProductProjectionCommercetoolsService;
import com.mindcurv.b2x.commons.commercetools.services.impl.ShoppingListCommercetoolsService;
import com.mindcurv.b2x.commons.commercetools.services.impl.StoreCommercetoolsService;
import com.mindcurv.b2x.commons.converter.BaseConverter;
import com.mindcurv.b2x.commons.models.B2xContext;
import com.mindcurv.b2x.commons.models.Pageable;
import com.mindcurv.b2x.commons.models.Reference;
import com.mindcurv.b2x.productcatalog.aggregator.aggregation.ListingAggregationService;
import com.mindcurv.b2x.productcatalog.aggregator.models.ListingAggregation;
import com.mindcurv.b2x.productcatalog.aggregator.models.ListingAggregationEntry;
import jakarta.validation.Valid;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DefaultListingAggregationService implements ListingAggregationService {

  private static final Logger LOG = LoggerFactory.getLogger(DefaultListingAggregationService.class);

  @NotNull
  private final CartCommercetoolsService cartCommercetoolsService;
  @NotNull
  private final OrderCommercetoolsService orderCommercetoolsService;
  @NotNull
  private final ProductProjectionCommercetoolsService productProjectionCommercetoolsService;
  @NotNull
  private final ShoppingListCommercetoolsService shoppingListCommercetoolsService;
  @NotNull
  private final StoreCommercetoolsService storeCommercetoolsService;
  @NotNull
  private final ProductListingService productListingService;
  @NotNull
  @Valid
  private final BaseConverter<AssignedProductSelection, ListingAggregationEntry> listingConverter;

  @Autowired
  public DefaultListingAggregationService(
      @NotNull final CartCommercetoolsService cartCommercetoolsService,
      @NotNull final OrderCommercetoolsService orderCommercetoolsService,
      @NotNull final ProductProjectionCommercetoolsService productProjectionCommercetoolsService,
      @NotNull final ShoppingListCommercetoolsService shoppingListCommercetoolsService,
      @NotNull final StoreCommercetoolsService storeCommercetoolsService,
      @NotNull final ProductListingService productListingService,
      @NotNull final BaseConverter<AssignedProductSelection, ListingAggregationEntry> listingConverter) {
    this.cartCommercetoolsService = cartCommercetoolsService;
    this.orderCommercetoolsService = orderCommercetoolsService;
    this.productProjectionCommercetoolsService = productProjectionCommercetoolsService;
    this.shoppingListCommercetoolsService = shoppingListCommercetoolsService;
    this.storeCommercetoolsService = storeCommercetoolsService;
    this.productListingService = productListingService;
    this.listingConverter = listingConverter;
  }

  @Override
  @NotNull
  public Pageable<ListingAggregation> productListingsByReferences(
      @Nullable @Valid final Collection<Reference> referenceList,
      @NotNull final B2xContext context) {
    if (isEmpty(referenceList)) {
      return emptyPageable();
    }
    try {
      final var futures =
          referenceList.stream()
              .map(reference -> supplyAsync(() -> productListings(reference, context)))
              .toList();
      return initPageableFromList(allCompletableFutureOf(futures).get());
    } catch (final InterruptedException e) {
      LOG.warn("productListingsByReferences :: InterruptedException", e);
      Thread.currentThread().interrupt();
    } catch (final ExecutionException e) {
      LOG.warn("productListingsByReferences :: ExecutionException", e);
    }
    return emptyPageable();
  }

  @Override
  @NotNull
  public Pageable<ListingAggregation> productListingsBySkus(
      @Nullable @Valid final Collection<String> skuList, @NotNull final B2xContext context) {
    if (isEmpty(skuList)) {
      return emptyPageable();
    }
    try {
      final var futures = skuList.stream()
          .map(sku -> supplyAsync(() -> productListingsBySku(sku, context)))
          .toList();
      return initPageableFromList(allCompletableFutureOf(futures).get());
    } catch (final InterruptedException e) {
      LOG.warn("productListingsBySkus :: InterruptedException", e);
      Thread.currentThread().interrupt();
    } catch (final ExecutionException e) {
      LOG.warn("productListingsBySkus :: ExecutionException", e);
    }
    return emptyPageable();
  }

  @Override
  @NotNull
  public ListingAggregation productListingsById(
      @Nullable final String id, @NotNull final B2xContext context) {
    final var listingAggregation = new ListingAggregation().store(context.getStore()).listed(false);
    if (isBlank(id)) {
      return listingAggregation;
    }
    listingAggregation.listings(
        listingConverter.convertList(
            storeCommercetoolsService
                .findByKey(context.getStore())
                .map(store -> productListingService.getProductStoreAssignmentsById(store, id))
                .orElseGet(List::of)).stream().toList());
    return listingAggregation
        .reference(new Reference().id(id).typeId(PRODUCT.getJsonName()))
        .listed(isNotEmpty(listingAggregation.getListings()));
  }

  @Override
  @NotNull
  public ListingAggregation productListingsByKey(
      @Nullable final String key, @NotNull final B2xContext context) {
    final var listingAggregation = new ListingAggregation().store(context.getStore()).listed(false);
    if (isBlank(key)) {
      return listingAggregation;
    }
    listingAggregation.listings(
        listingConverter.convertList(
            storeCommercetoolsService
                .findByKey(context.getStore())
                .map(store -> productListingService.getProductStoreAssignmentsByKey(store, key))
                .orElseGet(List::of)).stream().toList());
    return listingAggregation
        .reference(new Reference().key(key).typeId(PRODUCT.getJsonName()))
        .listed(isNotEmpty(listingAggregation.getListings()));
  }

  @Override
  @NotNull
  public ListingAggregation productListingsBySku(
      @Nullable final String sku, @NotNull final B2xContext context) {
    return productProjectionCommercetoolsService
        .findBySku(sku)
        .map(productProjection -> productListingsById(productProjection.getId(), context))
        .orElseGet(() -> new ListingAggregation().store(context.getStore()).listed(false));
  }

  @Override
  public boolean isListedById(@Nullable final String id, @NotNull final B2xContext context) {
    return isNotBlank(id) && storeCommercetoolsService
        .findByKey(context.getStore())
        .filter(store -> productListingService.isListed(store, id))
        .isPresent();
  }

  @Override
  public boolean isListedByKey(@Nullable final String key, @NotNull final B2xContext context) {
    return isNotBlank(key) && storeCommercetoolsService
        .findByKey(context.getStore())
        .filter(store -> productListingService.isListedByKey(store, key))
        .isPresent();
  }

  @Override
  @NotNull
  public Pageable<ListingAggregation> productListingsByCart(
      @NotNull final String cartId, @NotNull final B2xContext context) {
    return cartCommercetoolsService.findById(cartId)
        .map(cart -> productListingsByReferences(
            cart.getLineItems().stream().map(lineitem ->
                    new Reference()
                        .id(lineitem.getProductId())
                        .key(lineitem.getProductKey())
                        .typeId(PRODUCT.getJsonName()))
                .toList(),
            context))
        .orElse(emptyPageable());
  }

  @Override
  @NotNull
  public Pageable<ListingAggregation> productListingsByCurrentCart(
      @NotNull final B2xContext context) {
    return cartCommercetoolsService
        .findCartByCustomerId(context.getCustomerId())
        .map(
            cart ->
                productListingsByReferences(
                    cart.getLineItems().stream()
                        .map(
                            lineitem ->
                                new Reference()
                                    .id(lineitem.getProductId())
                                    .key(lineitem.getProductKey())
                                    .typeId(PRODUCT.getJsonName()))
                        .toList(),
                    context))
        .orElse(emptyPageable());
  }

  @Override
  @NotNull
  public Pageable<ListingAggregation> productListingsByOrder(
      @NotNull final String orderId, @NotNull final B2xContext context) {
    return orderCommercetoolsService
        .findById(orderId)
        .map(
            cart ->
                productListingsByReferences(
                    cart.getLineItems().stream()
                        .map(
                            lineitem ->
                                new Reference()
                                    .id(lineitem.getProductId())
                                    .key(lineitem.getProductKey())
                                    .typeId(PRODUCT.getJsonName()))
                        .toList(),
                    context))
        .orElse(emptyPageable());
  }

  @Override
  @NotNull
  public Pageable<ListingAggregation> productListingsByShoppinglist(
      @NotNull final String shoppinglistId, @NotNull final B2xContext context) {
    return shoppingListCommercetoolsService
        .findById(shoppinglistId)
        .map(
            shoppingList ->
                productListingsBySkus(
                    shoppingList.getLineItems().stream()
                        .filter(lineitem -> lineitem.getVariant() != null)
                        .map(lineitem -> lineitem.getVariant().getSku())
                        .toList(),
                    context))
        .orElse(emptyPageable());
  }
}
