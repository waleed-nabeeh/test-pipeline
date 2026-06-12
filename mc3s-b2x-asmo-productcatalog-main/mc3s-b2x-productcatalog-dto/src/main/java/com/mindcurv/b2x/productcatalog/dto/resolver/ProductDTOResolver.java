package com.mindcurv.b2x.productcatalog.dto.resolver;

import static com.mindcurv.b2x.commons.helper.ValueHelper.getFirstNonNullArg;
import static com.mindcurv.b2x.commons.helper.ValueHelper.isValidUuid;

import com.mindcurv.b2x.commons.commercetools.condition.impl.ProductScopeCondition;
import com.mindcurv.b2x.commons.resolver.NullableBaseResolver;
import com.mindcurv.b2x.connector.adapter.DTOReadAdapter;
import com.mindcurv.b2x.connector.product.models.ProductDTO;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

@Component
@Conditional({ProductScopeCondition.class})
public class ProductDTOResolver implements NullableBaseResolver<ProductDTO> {

  private static final Logger LOG = LoggerFactory.getLogger(ProductDTOResolver.class);

  private static final String CACHE_KEY = "product_cache";
  @NotNull
  private final DTOReadAdapter<ProductDTO> readDTOAdapter;

  @Autowired
  public ProductDTOResolver(@NotNull final DTOReadAdapter<ProductDTO> readDTOAdapter) {
    this.readDTOAdapter = readDTOAdapter;
  }

  @Override
  @NotNull
  @Cacheable(value = CACHE_KEY,
      key = "T(com.mindcurv.b2x.commons.cache.helper.CacheHelper).getResolverCacheKey('productDTOResolver', #args)")

  public Optional<ProductDTO> resolve(@Nullable final Object... args) {
    final var firstArg = getFirstNonNullArg(args);
    if (firstArg.isEmpty()) {
      return Optional.empty();
    }
    if (firstArg.get() instanceof String value) {
      final var result =
          isValidUuid(value) ? readDTOAdapter.findById(value) : readDTOAdapter.findByKey(value);
      if (result.isPresent()) {
        return result;
      }
      LOG.info("resolve :: not found '{}', try slug and sku ", value);
      // slug
      // sku

    }
    return Optional.empty();
  }
}
