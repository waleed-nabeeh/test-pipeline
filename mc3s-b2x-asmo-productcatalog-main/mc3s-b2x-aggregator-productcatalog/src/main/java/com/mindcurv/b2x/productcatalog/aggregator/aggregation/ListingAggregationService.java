package com.mindcurv.b2x.productcatalog.aggregator.aggregation;

import static com.mindcurv.b2x.commons.helper.ValueHelper.isValidUuid;

import com.mindcurv.b2x.commons.models.B2xContext;
import com.mindcurv.b2x.commons.models.Pageable;
import com.mindcurv.b2x.commons.models.Reference;
import com.mindcurv.b2x.productcatalog.aggregator.models.ListingAggregation;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ListingAggregationService {

  @NotNull
  default ListingAggregation productListings(
      @Nullable final String identifier, @NotNull final B2xContext context) {
    return isValidUuid(identifier)
        ? productListingsById(identifier, context)
        : productListingsByKey(identifier, context);
  }

  @NotNull
  default ListingAggregation productListings(
      @NotNull final Reference reference, @NotNull final B2xContext context) {
    final var identifier = reference.getId() == null ? reference.getKey() : reference.getId();
    return productListings(identifier, context);
  }

  @NotNull
  Pageable<ListingAggregation> productListingsByReferences(
      @Nullable Collection<Reference> referenceList, @NotNull B2xContext context);

  @NotNull
  Pageable<ListingAggregation> productListingsBySkus(
      @Nullable Collection<String> skuList, @NotNull B2xContext context);

  @NotNull
  ListingAggregation productListingsById(@Nullable String id, @NotNull B2xContext context);

  boolean isListedById(@Nullable String id, @NotNull B2xContext context);

  @NotNull
  ListingAggregation productListingsByKey(@Nullable String key, @NotNull B2xContext context);

  @NotNull
  ListingAggregation productListingsBySku(@Nullable String key, @NotNull B2xContext context);

  @NotNull
  Pageable<ListingAggregation> productListingsByCart(
      @NotNull String cartId, @NotNull B2xContext context);

  @NotNull
  Pageable<ListingAggregation> productListingsByCurrentCart(@NotNull B2xContext context);

  @NotNull
  Pageable<ListingAggregation> productListingsByOrder(
      @NotNull String orderId, @NotNull B2xContext context);

  @NotNull
  Pageable<ListingAggregation> productListingsByShoppinglist(
      @NotNull String shoppinglistId, @NotNull B2xContext context);

  boolean isListedByKey(@Nullable String key, @NotNull B2xContext context);
}
