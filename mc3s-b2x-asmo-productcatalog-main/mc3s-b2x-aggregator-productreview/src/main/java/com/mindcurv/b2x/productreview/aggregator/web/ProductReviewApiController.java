package com.mindcurv.b2x.productreview.aggregator.web;

import static com.mindcurv.b2x.commons.DefaultsConstants.DEFAULT_LIMIT_LONG;
import static com.mindcurv.b2x.commons.DefaultsConstants.DEFAULT_OFFSET_LONG;

import com.mindcurv.b2x.commons.helper.B2xWebUtils;
import com.mindcurv.b2x.commons.token.services.ContextService;
import com.mindcurv.b2x.commons.token.services.TokenService;
import com.mindcurv.b2x.commons.token.web.B2xAcceleratorController;
import com.mindcurv.b2x.productcatalog.review.api.ProductReviewApi;
import com.mindcurv.b2x.productcatalog.review.models.ProductReviewAggregation;
import com.mindcurv.b2x.productcatalog.review.models.ProductReviewCreateRequest;
import com.mindcurv.b2x.productcatalog.review.models.ProductReviewSummary;
import com.mindcurv.b2x.productreview.aggregator.aggregation.ProductReviewAggregationService;
import jakarta.validation.Valid;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.request.NativeWebRequest;

@Controller
public class ProductReviewApiController extends B2xAcceleratorController
    implements ProductReviewApi {

  @NotNull
  private final ProductReviewAggregationService productReviewAggregationService;

  @Autowired
  public ProductReviewApiController(
      @NotNull final TokenService tokenService,
      @NotNull final ContextService contextService,
      @NotNull final NativeWebRequest nativeWebRequest,
      @NotNull final ProductReviewAggregationService productReviewAggregationService) {
    super(tokenService, contextService, nativeWebRequest);
    this.productReviewAggregationService = productReviewAggregationService;
  }

  @Override
  @NotNull
  public ResponseEntity<ProductReviewAggregation> productReview(@NotNull @Valid final
  ProductReviewCreateRequest request) {
    return productReviewAggregationService.createProductReview(request,
            getContextFromHeader()).map(B2xWebUtils::successResponse)
        .orElseGet(B2xWebUtils::badRequest);
  }

  @Override
  @NotNull
  public ResponseEntity<ProductReviewAggregation> productReviewById(@NotNull final String id) {
    return productReviewAggregationService.findReviewById(id, getContextFromHeader())
        .map(B2xWebUtils::successResponse).orElseGet(B2xWebUtils::notFound);
  }

  @Override
  @NotNull
  public ResponseEntity<ProductReviewAggregation> productReviewByKey(@NotNull final String key) {
    return productReviewAggregationService.findReviewByKey(key, getContextFromHeader())
        .map(B2xWebUtils::successResponse).orElseGet(B2xWebUtils::notFound);
  }

  @Override
  @NotNull
  public ResponseEntity<ProductReviewSummary> productReviewsByKey(@NotNull final String productKey,
      @Nullable final Long offset,
      @Nullable final Long limit) {
    return productReviewAggregationService.getProductReviewSummaryByProductKey(
            productKey, Optional.ofNullable(offset).orElse(DEFAULT_OFFSET_LONG),
            Optional.ofNullable(limit).orElse(DEFAULT_LIMIT_LONG),
            getContextFromHeader()).map(B2xWebUtils::successResponse)
        .orElseGet(B2xWebUtils::notFound);
  }

  @Override
  @NotNull
  public ResponseEntity<ProductReviewSummary> productReviewsByProductId(
      @NotNull final String productId,
      @Nullable final Long offset,
      @Nullable final Long limit) {
    return productReviewAggregationService.getProductReviewSummaryByProductId(
            productId,
            Optional.ofNullable(offset).orElse(DEFAULT_OFFSET_LONG),
            Optional.ofNullable(limit).orElse(DEFAULT_LIMIT_LONG),
            getContextFromHeader()).map(B2xWebUtils::successResponse)
        .orElseGet(B2xWebUtils::notFound);
  }
}
