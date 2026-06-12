package com.mindcurv.b2x.productcatalog.aggregator.resolver.impl;

import static com.mindcurv.b2x.commons.base.BaseAggregatorCacheSpringConfig.PRODUCT_CACHE;
import static com.mindcurv.b2x.commons.helper.ValueHelper.getFirstNonNullArg;
import static com.mindcurv.b2x.commons.helper.ValueHelper.isValidUuid;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import com.commercetools.api.models.product.ProductProjection;
import com.mindcurv.b2x.commons.commercetools.services.impl.ProductProjectionCommercetoolsService;
import com.mindcurv.b2x.commons.resolver.NullableBaseResolver;
import com.mindcurv.b2x.connector.models.Predicate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
public class ProductProjectionResolver implements NullableBaseResolver<ProductProjection> {

  public static final String SKU_PREFIX = "sku=";
  @NotNull
  private final ProductProjectionCommercetoolsService productProjectionCommercetoolsService;

  @Autowired
  public ProductProjectionResolver(
      @NotNull final ProductProjectionCommercetoolsService productProjectionCommercetoolsService) {
    this.productProjectionCommercetoolsService = productProjectionCommercetoolsService;
  }

  @Override
  @NotNull
  @Cacheable(
      value = {PRODUCT_CACHE},
      key = "T(com.mindcurv.b2x.commons.cache.helper.CacheHelper).getResolverCacheKey('productProjectionResolver', #args)"
  )
  public Optional<ProductProjection> resolve(@Nullable final Object... args) {
    final var identifier = getFirstNonNullArg(args);
    if (identifier.isPresent() && identifier.get() instanceof String value) {

      if (args != null && args.length > 1 && args[1] instanceof String language) {
        return productProjectionCommercetoolsService.findBySlug(value, language,
            getExpansions().toArray(new String[0]));
      }
      if (value.startsWith(SKU_PREFIX)) {
        return productProjectionCommercetoolsService.findBySku(value.replace(SKU_PREFIX, EMPTY),
            getExpansions());
      }
      return isValidUuid(value) ? productProjectionCommercetoolsService.findById(value,
          getExpansions()) :
          productProjectionCommercetoolsService.findByKey(value, getExpansions());
    }
    return Optional.empty();
  }

  @Override
  @NotNull
  public Collection<ProductProjection> getAllItems() {
    return productProjectionCommercetoolsService.getAllItems(
        Predicate.builder().expansions(getExpansions()).build());
  }

  @Override
  @NotNull
  public List<String> getExpansions() {
    return List.of("productType", "taxCategory", "categories[*].custom.type",
        "categories[*].ancestors[*]");
  }
}
