package com.mindcurv.b2x.productcatalog.aggregator.web;

import static com.mindcurv.b2x.commons.DefaultsConstants.DEFAULT_LIMIT_LONG;
import static com.mindcurv.b2x.commons.DefaultsConstants.DEFAULT_OFFSET_LONG;
import static com.mindcurv.b2x.commons.helper.B2xWebUtils.successResponse;
import static com.mindcurv.b2x.commons.models.Pageable.convertPageable;

import com.mindcurv.b2x.commons.helper.B2xWebUtils;
import com.mindcurv.b2x.commons.models.PageableSearchRequest;
import com.mindcurv.b2x.commons.token.services.ContextService;
import com.mindcurv.b2x.commons.token.services.TokenService;
import com.mindcurv.b2x.commons.token.web.B2xAcceleratorController;
import com.mindcurv.b2x.productcatalog.aggregator.aggregation.ProductAggregationService;
import com.mindcurv.b2x.productcatalog.aggregator.api.ProductApi;
import com.mindcurv.b2x.productcatalog.aggregator.models.ProductAggregation;
import com.mindcurv.b2x.productcatalog.openapi.PageableOfProductAggregation;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.request.NativeWebRequest;

@Controller
public class ProductApiController extends B2xAcceleratorController implements ProductApi {

  @NotNull
  private final ProductAggregationService productAggregationService;

  @Autowired
  public ProductApiController(
      @NotNull final TokenService tokenService,
      @NotNull final ContextService contextService,
      @NotNull final NativeWebRequest nativeWebRequest,
      @NotNull final ProductAggregationService productAggregationService) {
    super(tokenService, contextService, nativeWebRequest);
    this.productAggregationService = productAggregationService;
  }

  @Override
  @NotNull
  public ResponseEntity<PageableOfProductAggregation> products(
      @Nullable final Long offset,
      @Nullable final Long limit) {
    return successResponse(
        convertPageable(
            productAggregationService.getItems(
                getContextFromHeader(),
                new PageableSearchRequest()
                    .offset(Optional.ofNullable(offset).orElse(DEFAULT_OFFSET_LONG))
                    .limit(Optional.ofNullable(limit).orElse(DEFAULT_LIMIT_LONG))),
            new PageableOfProductAggregation()));
  }

  @Override
  @NotNull
  public ResponseEntity<ProductAggregation> productByKey(@NotNull final String key) {
    return productAggregationService
        .findByKey(key, getContextFromHeader())
        .map(B2xWebUtils::successResponse)
        .orElseGet(B2xWebUtils::notFound);
  }

  @Override
  @NotNull
  public ResponseEntity<ProductAggregation> productById(@NotNull final String id) {
    return productAggregationService
        .findById(id, getContextFromHeader())
        .map(B2xWebUtils::successResponse)
        .orElseGet(B2xWebUtils::notFound);
  }

  @Override
  @NotNull
  public ResponseEntity<ProductAggregation> productBySku(@NotNull final String sku) {
    return productAggregationService
        .findBySku(sku, getContextFromHeader())
        .map(B2xWebUtils::successResponse)
        .orElseGet(B2xWebUtils::notFound);
  }

  @Override
  @NotNull
  public ResponseEntity<ProductAggregation> productBySlug(@NotNull final String slug) {
    return productAggregationService
        .findBySlug(slug, getContextFromHeader())
        .map(B2xWebUtils::successResponse)
        .orElseGet(B2xWebUtils::notFound);
  }
}
