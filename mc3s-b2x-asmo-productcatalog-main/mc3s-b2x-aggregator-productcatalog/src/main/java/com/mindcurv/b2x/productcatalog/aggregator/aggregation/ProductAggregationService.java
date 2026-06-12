package com.mindcurv.b2x.productcatalog.aggregator.aggregation;

import com.mindcurv.b2x.commons.models.B2xContext;
import com.mindcurv.b2x.commons.models.Pageable;
import com.mindcurv.b2x.commons.models.PageableSearchRequest;
import com.mindcurv.b2x.productcatalog.aggregator.models.ProductAggregation;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ProductAggregationService {

  @NotNull
  Pageable<ProductAggregation> getItems(
      @NotNull B2xContext context, @NotNull PageableSearchRequest searchRequest);

  @NotNull
  Optional<ProductAggregation> findByKey(@Nullable String key, @NotNull B2xContext context);

  @NotNull
  Optional<ProductAggregation> findById(@Nullable String id, @NotNull B2xContext context);

  @NotNull
  Optional<ProductAggregation> findBySku(@Nullable String sku, @NotNull B2xContext context);

  @NotNull
  Optional<ProductAggregation> findBySlug(@Nullable String slug, @NotNull B2xContext context);

  @NotNull
  default Collection<String> getListExpansions() {
    return List.of("productType", "taxCategory");
  }

  @NotNull
  default Collection<String> getDetailExpansions() {
    final var expansions = new LinkedList<>(getListExpansions());
    expansions.addAll(List.of("categories[*].custom.type", "categories[*].ancestors[*]"));
    return expansions;
  }
}
