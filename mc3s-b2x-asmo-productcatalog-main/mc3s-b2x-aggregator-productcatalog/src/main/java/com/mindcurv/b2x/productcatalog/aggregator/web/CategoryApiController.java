package com.mindcurv.b2x.productcatalog.aggregator.web;

import static com.mindcurv.b2x.commons.DefaultsConstants.DEFAULT_LIMIT_LONG;
import static com.mindcurv.b2x.commons.DefaultsConstants.DEFAULT_OFFSET_LONG;
import static com.mindcurv.b2x.commons.helper.B2xWebUtils.successResponse;
import static com.mindcurv.b2x.commons.models.Pageable.convertPageable;
import static com.mindcurv.b2x.commons.models.Pageable.initPageableFromList;

import com.mindcurv.b2x.commons.helper.B2xWebUtils;
import com.mindcurv.b2x.commons.models.PageableSearchRequest;
import com.mindcurv.b2x.commons.token.services.ContextService;
import com.mindcurv.b2x.commons.token.services.TokenService;
import com.mindcurv.b2x.commons.token.web.B2xAcceleratorController;
import com.mindcurv.b2x.productcatalog.aggregator.aggregation.CategoryAggregationService;
import com.mindcurv.b2x.productcatalog.aggregator.api.CategoryApi;
import com.mindcurv.b2x.productcatalog.aggregator.models.CategoryAggregation;
import com.mindcurv.b2x.productcatalog.openapi.PageableOfCategoryAggregation;
import com.mindcurv.b2x.productcatalog.openapi.PageableOfCategoryNavigationAggregation;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.request.NativeWebRequest;

@Controller
public class CategoryApiController extends B2xAcceleratorController implements CategoryApi {

  @NotNull
  private final CategoryAggregationService categoryAggregationService;

  @Autowired
  public CategoryApiController(
      @NotNull final TokenService tokenService,
      @NotNull final ContextService contextService,
      @NotNull final NativeWebRequest nativeWebRequest,
      @NotNull final CategoryAggregationService categoryAggregationService) {
    super(tokenService, contextService, nativeWebRequest);
    this.categoryAggregationService = categoryAggregationService;
  }

  @Override
  @NotNull
  public ResponseEntity<PageableOfCategoryAggregation> categories(
      @Nullable final Long offset,
      @Nullable final Long limit) {
    return successResponse(
        convertPageable(
            categoryAggregationService.getItems(
                getContextFromHeader(),
                new PageableSearchRequest()
                    .offset(Optional.ofNullable(offset).orElse(DEFAULT_OFFSET_LONG))
                    .limit(Optional.ofNullable(limit).orElse(DEFAULT_LIMIT_LONG))),
            new PageableOfCategoryAggregation()));
  }

  @Override
  @NotNull
  public ResponseEntity<CategoryAggregation> categoryByKey(@NotNull final String key) {
    return categoryAggregationService
        .findByKey(key, getContextFromHeader())
        .map(B2xWebUtils::successResponse)
        .orElseGet(B2xWebUtils::notFound);
  }

  @Override
  @NotNull
  public ResponseEntity<CategoryAggregation> categoryById(@NotNull final String id) {
    return categoryAggregationService
        .findById(id, getContextFromHeader())
        .map(B2xWebUtils::successResponse)
        .orElseGet(B2xWebUtils::notFound);
  }

  @Override
  @NotNull
  public ResponseEntity<CategoryAggregation> categoryBySlug(@NotNull final String slug) {
    return categoryAggregationService
        .findBySlug(slug, getContextFromHeader())
        .map(B2xWebUtils::successResponse)
        .orElseGet(B2xWebUtils::notFound);
  }

  @Override
  @NotNull
  public ResponseEntity<PageableOfCategoryNavigationAggregation> categoryTree() {
    return successResponse(
        convertPageable(
            initPageableFromList(categoryAggregationService.getTree(getContextFromHeader())),
            new PageableOfCategoryNavigationAggregation()));
  }
}
