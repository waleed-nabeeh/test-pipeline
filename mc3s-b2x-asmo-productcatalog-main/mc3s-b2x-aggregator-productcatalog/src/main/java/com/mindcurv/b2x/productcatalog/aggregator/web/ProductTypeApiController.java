package com.mindcurv.b2x.productcatalog.aggregator.web;

import static com.mindcurv.b2x.commons.helper.B2xWebUtils.successResponse;
import static com.mindcurv.b2x.commons.models.Pageable.convertPageable;
import static com.mindcurv.b2x.commons.models.Pageable.initPageableFromList;

import com.mindcurv.b2x.commons.helper.B2xWebUtils;
import com.mindcurv.b2x.commons.models.ProductTypeAggregation;
import com.mindcurv.b2x.commons.token.services.ContextService;
import com.mindcurv.b2x.commons.token.services.TokenService;
import com.mindcurv.b2x.commons.token.web.B2xAcceleratorController;
import com.mindcurv.b2x.productcatalog.aggregator.aggregation.ProductTypeAggregationService;
import com.mindcurv.b2x.productcatalog.aggregator.api.ProductTypesApi;
import com.mindcurv.b2x.productcatalog.openapi.PageableOfAttributeMetaData;
import com.mindcurv.b2x.productcatalog.openapi.PageableOfProductTypeAggregation;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.request.NativeWebRequest;

@Controller
public class ProductTypeApiController extends B2xAcceleratorController implements ProductTypesApi {

  @NotNull
  private final ProductTypeAggregationService productTypeAggregationService;

  @Autowired
  public ProductTypeApiController(
      @NotNull final TokenService tokenService,
      @NotNull final ContextService contextService,
      @NotNull final NativeWebRequest nativeWebRequest,
      @NotNull final ProductTypeAggregationService productTypeAggregationService) {
    super(tokenService, contextService, nativeWebRequest);
    this.productTypeAggregationService = productTypeAggregationService;
  }

  @Override
  @NotNull
  public ResponseEntity<PageableOfProductTypeAggregation> productTypes() {
    return successResponse(
        convertPageable(
            productTypeAggregationService.getProductTypes(getContextFromHeader()),
            new PageableOfProductTypeAggregation()));
  }

  @Override
  @NotNull
  public ResponseEntity<ProductTypeAggregation> productTypeById(@NotNull final String id) {
    return productTypeAggregationService
        .getProductTypeById(id, getContextFromHeader())
        .map(B2xWebUtils::successResponse)
        .orElseGet(B2xWebUtils::notFound);
  }

  @Override
  @NotNull
  public ResponseEntity<ProductTypeAggregation> productTypeByKey(@NotNull final String key) {
    return productTypeAggregationService
        .getProductTypeByKey(key, getContextFromHeader())
        .map(B2xWebUtils::successResponse)
        .orElseGet(B2xWebUtils::notFound);
  }

  @Override
  @NotNull
  public ResponseEntity<PageableOfAttributeMetaData> productAttributeMetaDataByKey(
      @NotNull final String key) {
    return successResponse(
        convertPageable(
            initPageableFromList(
                productTypeAggregationService.getProductAttributeMetaDataByKey(key)),
            new PageableOfAttributeMetaData()));
  }

  @Override
  @NotNull
  public ResponseEntity<PageableOfAttributeMetaData> productAttributeMetaDataById(
      @NotNull final String id) {
    return successResponse(
        convertPageable(
            initPageableFromList(productTypeAggregationService.getProductAttributeMetaDataById(id)),
            new PageableOfAttributeMetaData()));
  }
}
