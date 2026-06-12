package com.mindcurv.b2x.productreview.aggregator.aggregation;

import com.mindcurv.b2x.commons.models.B2xContext;
import com.mindcurv.b2x.commons.models.Pageable;
import com.mindcurv.b2x.productcatalog.review.models.ProductReviewAggregation;
import com.mindcurv.b2x.productcatalog.review.models.ProductReviewCreateRequest;
import com.mindcurv.b2x.productcatalog.review.models.ProductReviewSummary;
import jakarta.validation.Valid;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;

public interface ProductReviewAggregationService {

  @NotNull
  Optional<ProductReviewAggregation> createProductReview(
      @NotNull @Valid ProductReviewCreateRequest request, @NotNull B2xContext context);

  @NotNull
  Optional<ProductReviewAggregation> findReviewById(@NotNull String id,
      @NotNull B2xContext context);

  @NotNull
  Optional<ProductReviewAggregation> findReviewByKey(@NotNull String key,
      @NotNull B2xContext context);

  @NotNull
  Optional<ProductReviewSummary> getProductReviewSummaryByProductKey(@NotNull String key,
      @NotNull Long offset, @NotNull Long limit,
      @NotNull B2xContext context);

  @NotNull
  Optional<ProductReviewSummary> getProductReviewSummaryByProductId(@NotNull String id,
      @NotNull Long offset, @NotNull Long limit,
      @NotNull B2xContext context);

  @NotNull
  Pageable<ProductReviewAggregation> getMyProductReviews(
      @NotNull B2xContext context);

}
