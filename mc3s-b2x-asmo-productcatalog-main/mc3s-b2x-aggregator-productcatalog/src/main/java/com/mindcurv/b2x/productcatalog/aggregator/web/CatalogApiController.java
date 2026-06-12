package com.mindcurv.b2x.productcatalog.aggregator.web;

import static com.mindcurv.b2x.commons.DefaultsConstants.DEFAULT_LIMIT_LONG;
import static com.mindcurv.b2x.commons.DefaultsConstants.DEFAULT_OFFSET_LONG;
import static com.mindcurv.b2x.commons.helper.B2xWebUtils.successResponse;
import static com.mindcurv.b2x.commons.models.Pageable.convertPageable;

import com.mindcurv.b2x.commons.models.PageableSearchRequest;
import com.mindcurv.b2x.commons.token.services.ContextService;
import com.mindcurv.b2x.commons.token.services.TokenService;
import com.mindcurv.b2x.commons.token.web.B2xAcceleratorController;
import com.mindcurv.b2x.productcatalog.aggregator.aggregation.CatalogAggregationService;
import com.mindcurv.b2x.productcatalog.aggregator.api.CatalogApi;
import com.mindcurv.b2x.productcatalog.openapi.PageableOfCatalogAggregation;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.request.NativeWebRequest;

@Controller
public class CatalogApiController extends B2xAcceleratorController implements CatalogApi {

  @NotNull
  private final CatalogAggregationService catalogAggregationService;

  @Autowired
  public CatalogApiController(
      @NotNull final TokenService tokenService,
      @NotNull final ContextService contextService,
      @NotNull final NativeWebRequest nativeWebRequest,
      @NotNull final CatalogAggregationService catalogAggregationService) {
    super(tokenService, contextService, nativeWebRequest);
    this.catalogAggregationService = catalogAggregationService;
  }

  @Override
  @NotNull
  public ResponseEntity<PageableOfCatalogAggregation> catalogs(
      @Nullable final Long offset,
      @Nullable final Long limit) {
    return successResponse(
        convertPageable(
            catalogAggregationService.getCatalogs(
                getContextFromHeader(),
                new PageableSearchRequest()
                    .offset(Optional.ofNullable(offset).orElse(DEFAULT_OFFSET_LONG))
                    .limit(Optional.ofNullable(limit).orElse(DEFAULT_LIMIT_LONG))),
            new PageableOfCatalogAggregation()));
  }

}
