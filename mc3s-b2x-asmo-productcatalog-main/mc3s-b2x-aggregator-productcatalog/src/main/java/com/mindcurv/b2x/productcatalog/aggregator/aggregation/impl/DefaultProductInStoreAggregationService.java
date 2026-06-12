package com.mindcurv.b2x.productcatalog.aggregator.aggregation.impl;

import static com.mindcurv.b2x.commons.models.ConverterType.DETAIL;

import com.mindcurv.b2x.commons.commercetools.instore.services.impl.ProductProjectionInStoreService;
import com.mindcurv.b2x.commons.commercetools.services.impl.ProductProjectionCommercetoolsService;
import com.mindcurv.b2x.commons.models.B2xContext;
import com.mindcurv.b2x.productcatalog.aggregator.aggregation.ProductInStoreAggregationService;
import com.mindcurv.b2x.productcatalog.aggregator.converter.ProductAggregationProductProjectionConverter;
import com.mindcurv.b2x.productcatalog.aggregator.models.ProductAggregation;
import jakarta.validation.Valid;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DefaultProductInStoreAggregationService implements ProductInStoreAggregationService {

  @NotNull
  private final ProductProjectionInStoreService productProjectionInStoreService;

  @NotNull
  private final ProductProjectionCommercetoolsService productProjectionCommercetoolsService;

  @NotNull
  @Valid
  private final ProductAggregationProductProjectionConverter productAggregationProductProjectionConverter;

  @Autowired
  public DefaultProductInStoreAggregationService(
      @NotNull final ProductProjectionInStoreService productProjectionInStoreService,
      @NotNull final ProductProjectionCommercetoolsService productProjectionCommercetoolsService,
      @NotNull final ProductAggregationProductProjectionConverter productAggregationProductProjectionConverter) {
    this.productProjectionInStoreService = productProjectionInStoreService;
    this.productProjectionCommercetoolsService = productProjectionCommercetoolsService;
    this.productAggregationProductProjectionConverter = productAggregationProductProjectionConverter;
  }

  @NotNull
  @Override
  public Optional<ProductAggregation> findByKey(
      @Nullable final String key, @NotNull final B2xContext context) {
    return productProjectionInStoreService.findByKeyInStore(key, context.getStore(),
            getDetailExpansions())
        .flatMap(product -> productAggregationProductProjectionConverter.convertAsOptional(product,
            context, DETAIL));
  }

  @NotNull
  @Override
  public Optional<ProductAggregation> findById(
      @Nullable final String id, @NotNull final B2xContext context) {
    return productProjectionInStoreService.findByIdInStore(id, context.getStore(),
            getDetailExpansions())
        .flatMap(product -> productAggregationProductProjectionConverter.convertAsOptional(product,
            context, DETAIL));
  }

  @NotNull
  @Override
  public Optional<ProductAggregation> findBySku(
      @Nullable final String sku, @NotNull final B2xContext context) {
    return productProjectionCommercetoolsService.findBySku(sku)
        .flatMap(product -> findById(product.getId(), context));
  }

  @NotNull
  @Override
  public Optional<ProductAggregation> findBySlug(
      @Nullable final String slug, @NotNull final B2xContext context) {
    return productProjectionCommercetoolsService.findBySlug(slug, context.getCurrentLanguage())
        .flatMap(product -> findById(product.getId(), context));
  }
}
