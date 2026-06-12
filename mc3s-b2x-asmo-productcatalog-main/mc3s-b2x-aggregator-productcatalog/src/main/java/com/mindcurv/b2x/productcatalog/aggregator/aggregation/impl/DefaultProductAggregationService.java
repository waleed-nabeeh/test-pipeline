package com.mindcurv.b2x.productcatalog.aggregator.aggregation.impl;

import static com.mindcurv.b2x.commons.models.ConverterType.DETAIL;
import static com.mindcurv.b2x.productcatalog.aggregator.helper.CatalogAccessHelper.getFilteredContextCategories;
import static com.mindcurv.b2x.productcatalog.aggregator.resolver.impl.ProductProjectionResolver.SKU_PREFIX;
import static java.util.stream.Collectors.toSet;

import com.commercetools.api.models.product.ProductProjection;
import com.mindcurv.b2x.commons.commercetools.services.impl.ProductProjectionCommercetoolsService;
import com.mindcurv.b2x.commons.models.B2xContext;
import com.mindcurv.b2x.commons.models.Pageable;
import com.mindcurv.b2x.commons.models.PageableSearchRequest;
import com.mindcurv.b2x.commons.resolver.NullableBaseResolver;
import com.mindcurv.b2x.productcatalog.aggregator.aggregation.ProductAggregationService;
import com.mindcurv.b2x.productcatalog.aggregator.converter.ProductAggregationProductProjectionConverter;
import com.mindcurv.b2x.productcatalog.aggregator.models.CategoryNavigationAggregation;
import com.mindcurv.b2x.productcatalog.aggregator.models.ProductAggregation;
import com.mindcurv.b2x.productcatalog.aggregator.services.CategoryTreeService;
import jakarta.validation.Valid;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DefaultProductAggregationService implements ProductAggregationService {

  private static final Logger LOG = LoggerFactory.getLogger(DefaultProductAggregationService.class);

  @NotNull
  private final ProductProjectionCommercetoolsService productProjectionCommercetoolsService;
  @NotNull
  private final CategoryTreeService categoryTreeService;
  @NotNull
  private final NullableBaseResolver<ProductProjection> productProjectionResolver;
  @NotNull
  private final ProductAggregationProductProjectionConverter productAggregationProductProjectionConverter;

  @Autowired
  public DefaultProductAggregationService(
      @NotNull final ProductProjectionCommercetoolsService productProjectionCommercetoolsService,
      @NotNull final CategoryTreeService categoryTreeService,
      @NotNull final NullableBaseResolver<ProductProjection> productProjectionResolver,
      @NotNull final ProductAggregationProductProjectionConverter productAggregationProductProjectionConverter) {
    this.productProjectionCommercetoolsService = productProjectionCommercetoolsService;
    this.categoryTreeService = categoryTreeService;
    this.productProjectionResolver = productProjectionResolver;
    this.productAggregationProductProjectionConverter = productAggregationProductProjectionConverter;
  }

  @NotNull
  @Override
  public Pageable<ProductAggregation> getItems(
      @NotNull final B2xContext context,
      @NotNull @Valid final PageableSearchRequest searchRequest) {
    return productAggregationProductProjectionConverter.convertPageableFromResponse(
            productProjectionCommercetoolsService.getItems(searchRequest), context, DETAIL)
        .setSearchRequest(searchRequest);
  }

  @NotNull
  @Override
  public Optional<ProductAggregation> findByKey(
      @Nullable final String key, @NotNull final B2xContext context) {
    return productProjectionResolver.resolve(key)
        .flatMap(product -> getProductAggregation(product, context));
  }

  @NotNull
  @Override
  public Optional<ProductAggregation> findById(
      @Nullable final String id, @NotNull final B2xContext context) {
    return productProjectionResolver.resolve(id)
        .flatMap(product -> getProductAggregation(product, context));
  }

  @NotNull
  @Override
  public Optional<ProductAggregation> findBySku(
      @Nullable final String sku, @NotNull final B2xContext context) {
    return productProjectionResolver.resolve(SKU_PREFIX + sku)
        .flatMap(product -> getProductAggregation(product, context));
  }

  @NotNull
  @Override
  public Optional<ProductAggregation> findBySlug(
      @Nullable final String slug, @NotNull final B2xContext context) {
    return productProjectionResolver.resolve(slug, context.getCurrentLanguage())
        .flatMap(product -> getProductAggregation(product, context));
  }

  @NotNull
  protected Optional<ProductAggregation> getProductAggregation(
      @NotNull @Valid final ProductProjection productProjection,
      @NotNull final B2xContext context) {
    final var aggregation = productAggregationProductProjectionConverter.convertAsOptional(
        productProjection, context, DETAIL);
    if (aggregation.isPresent()) {
      final var product = aggregation.get();
      product.categories(getFilteredContextCategories(aggregation.get().getCategories(),
          getRootCategoryIds(context)).stream().toList());
      return Optional.of(product);
    }
    return Optional.empty();
  }

  @NotNull
  protected Collection<String> getRootCategoryIds(@NotNull final B2xContext context) {
    final var idList = categoryTreeService.generateTree(context).stream()
        .map(CategoryNavigationAggregation::getId)
        .filter(Objects::nonNull)
        .collect(toSet());
    LOG.debug("getRootCategoryIds :: {}", idList);
    return idList;
  }
}
