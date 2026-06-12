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
import com.mindcurv.b2x.productcatalog.aggregator.aggregation.AttributeGroupAggregationService;
import com.mindcurv.b2x.productcatalog.aggregator.api.AttributeGroupApi;
import com.mindcurv.b2x.productcatalog.aggregator.models.AttributeGroupAggregation;
import com.mindcurv.b2x.productcatalog.openapi.MapOfAttributeGroupAggregation;
import com.mindcurv.b2x.productcatalog.openapi.PageableOfAttributeGroupAggregation;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.request.NativeWebRequest;

@Controller
public class AttributeGroupApiController extends B2xAcceleratorController
    implements AttributeGroupApi {

  @NotNull
  private final AttributeGroupAggregationService attributeGroupAggregationService;

  @Autowired
  public AttributeGroupApiController(
      @NotNull final TokenService tokenService,
      @NotNull final ContextService contextService,
      @NotNull final NativeWebRequest nativeWebRequest,
      @NotNull final AttributeGroupAggregationService attributeGroupAggregationService) {
    super(tokenService, contextService, nativeWebRequest);
    this.attributeGroupAggregationService = attributeGroupAggregationService;
  }

  @Override
  @NotNull
  public ResponseEntity<MapOfAttributeGroupAggregation> attributeGroupsForType(
      @NotNull final String key) {
    final var response = new MapOfAttributeGroupAggregation();
    response.putAll(
        attributeGroupAggregationService.getGroupsForType(
            key, getContextFromHeader()));
    return successResponse(response);
  }

  @Override
  @NotNull
  public ResponseEntity<PageableOfAttributeGroupAggregation> attributeGroups(
      @Nullable final Long offset,
      @Nullable final Long limit) {
    return successResponse(
        convertPageable(
            attributeGroupAggregationService.getItems(
                new PageableSearchRequest()
                    .offset(Optional.ofNullable(offset).orElse(DEFAULT_OFFSET_LONG))
                    .limit(Optional.ofNullable(limit).orElse(DEFAULT_LIMIT_LONG)),
                getContextFromHeader()
            ),
            new PageableOfAttributeGroupAggregation()));
  }

  @Override
  @NotNull
  public ResponseEntity<AttributeGroupAggregation> attributeGroupByKey(@NotNull final String key) {
    return attributeGroupAggregationService
        .findByKey(key, getContextFromHeader())
        .map(B2xWebUtils::successResponse)
        .orElseGet(B2xWebUtils::notFound);
  }

  @Override
  @NotNull
  public ResponseEntity<AttributeGroupAggregation> attributeGroupById(@NotNull final String id) {
    return attributeGroupAggregationService
        .findById(id, getContextFromHeader())
        .map(B2xWebUtils::successResponse)
        .orElseGet(B2xWebUtils::notFound);
  }
}
