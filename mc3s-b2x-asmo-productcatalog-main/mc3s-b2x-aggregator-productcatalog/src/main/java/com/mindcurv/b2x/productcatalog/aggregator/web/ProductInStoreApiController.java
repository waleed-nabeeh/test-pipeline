package com.mindcurv.b2x.productcatalog.aggregator.web;

import com.mindcurv.b2x.commons.helper.B2xWebUtils;
import com.mindcurv.b2x.commons.token.services.ContextService;
import com.mindcurv.b2x.commons.token.services.TokenService;
import com.mindcurv.b2x.commons.token.web.B2xAcceleratorController;
import com.mindcurv.b2x.productcatalog.aggregator.aggregation.ProductInStoreAggregationService;
import com.mindcurv.b2x.productcatalog.aggregator.api.ProductInStoreApi;
import com.mindcurv.b2x.productcatalog.aggregator.models.ProductAggregation;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.request.NativeWebRequest;

@Controller
public class ProductInStoreApiController extends B2xAcceleratorController
    implements ProductInStoreApi {

  @NotNull
  private final ProductInStoreAggregationService service;

  @Autowired
  public ProductInStoreApiController(
      @NotNull final TokenService tokenService,
      @NotNull final ContextService contextService,
      @NotNull final NativeWebRequest nativeWebRequest,
      @NotNull final ProductInStoreAggregationService service) {
    super(tokenService, contextService, nativeWebRequest);
    this.service = service;
  }

  @Override
  @NotNull
  public ResponseEntity<ProductAggregation> productInStoreByKey(@NotNull final String key) {
    return service
        .findByKey(key, getContextFromHeader())
        .map(B2xWebUtils::successResponse)
        .orElseGet(B2xWebUtils::notFound);
  }

  @Override
  @NotNull
  public ResponseEntity<ProductAggregation> productInStoreBySlug(@NotNull final String slug) {
    return service
        .findBySlug(slug, getContextFromHeader())
        .map(B2xWebUtils::successResponse)
        .orElseGet(B2xWebUtils::notFound);
  }

  @Override
  @NotNull
  public ResponseEntity<ProductAggregation> productInStoreById(@NotNull final String id) {
    return service
        .findById(id, getContextFromHeader())
        .map(B2xWebUtils::successResponse)
        .orElseGet(B2xWebUtils::notFound);
  }

  @Override
  @NotNull
  public ResponseEntity<ProductAggregation> productInStoreBySku(@NotNull final String sku) {
    return service
        .findBySku(sku, getContextFromHeader())
        .map(B2xWebUtils::successResponse)
        .orElseGet(B2xWebUtils::notFound);
  }
}
