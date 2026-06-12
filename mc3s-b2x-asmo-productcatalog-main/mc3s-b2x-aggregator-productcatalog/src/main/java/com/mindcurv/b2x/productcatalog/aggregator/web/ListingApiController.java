package com.mindcurv.b2x.productcatalog.aggregator.web;

import static com.mindcurv.b2x.commons.helper.B2xWebUtils.successResponse;
import static com.mindcurv.b2x.commons.models.Pageable.convertPageable;

import com.mindcurv.b2x.commons.models.Pageable;
import com.mindcurv.b2x.commons.models.Reference;
import com.mindcurv.b2x.commons.token.services.ContextService;
import com.mindcurv.b2x.commons.token.services.TokenService;
import com.mindcurv.b2x.commons.token.web.B2xAcceleratorController;
import com.mindcurv.b2x.productcatalog.aggregator.aggregation.ListingAggregationService;
import com.mindcurv.b2x.productcatalog.aggregator.api.ListingApi;
import com.mindcurv.b2x.productcatalog.aggregator.models.ListingAggregation;
import com.mindcurv.b2x.productcatalog.openapi.PageableOfListingAggregation;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.request.NativeWebRequest;

@Controller
public class ListingApiController extends B2xAcceleratorController implements ListingApi {

  @NotNull
  private final ListingAggregationService listingAggregationService;

  @Autowired
  public ListingApiController(
      @NotNull final TokenService tokenService,
      @NotNull final ContextService contextService,
      @NotNull final NativeWebRequest nativeWebRequest,
      @NotNull final ListingAggregationService listingAggregationService) {
    super(tokenService, contextService, nativeWebRequest);
    this.listingAggregationService = listingAggregationService;
  }

  @Override
  @NotNull
  public ResponseEntity<PageableOfListingAggregation> listingsByReference(
      @NotNull final List<Reference> reference) {
    return successResponse(
        convertPageable(
            listingAggregationService.productListingsByReferences(
                reference, getContextFromHeader()),
            new PageableOfListingAggregation()));
  }

  @Override
  @NotNull
  public ResponseEntity<ListingAggregation> listingById(@NotNull final String id) {
    return successResponse(
        listingAggregationService.productListingsById(id, getContextFromHeader()));
  }

  @Override
  @NotNull
  public ResponseEntity<ListingAggregation> listingByKey(@NotNull final String key) {
    return successResponse(
        listingAggregationService.productListingsByKey(key, getContextFromHeader()));
  }

  @Override
  @NotNull
  public ResponseEntity<ListingAggregation> listingBySku(@NotNull final String sku) {
    return successResponse(
        listingAggregationService.productListingsBySku(sku, getContextFromHeader()));
  }

  @Override
  @NotNull
  public ResponseEntity<PageableOfListingAggregation> listingByCart(
      @Nullable final String id) {
    final var context = getContextFromHeader();
    final Pageable<ListingAggregation> pageable =
        Optional.ofNullable(id)
            .map(cartId -> listingAggregationService.productListingsByCart(cartId, context))
            .orElseGet(() -> listingAggregationService.productListingsByCurrentCart(context));
    return successResponse(convertPageable(pageable, new PageableOfListingAggregation()));
  }

  @Override
  @NotNull
  public ResponseEntity<PageableOfListingAggregation> listingByOrder(@NotNull final String id) {
    return successResponse(
        convertPageable(
            listingAggregationService.productListingsByOrder(id, getContextFromHeader()),
            new PageableOfListingAggregation()));
  }

  @Override
  @NotNull
  public ResponseEntity<PageableOfListingAggregation> listingByShoppinglist(
      @NotNull final String id) {
    return successResponse(
        convertPageable(
            listingAggregationService.productListingsByShoppinglist(id, getContextFromHeader()),
            new PageableOfListingAggregation()));
  }
}
