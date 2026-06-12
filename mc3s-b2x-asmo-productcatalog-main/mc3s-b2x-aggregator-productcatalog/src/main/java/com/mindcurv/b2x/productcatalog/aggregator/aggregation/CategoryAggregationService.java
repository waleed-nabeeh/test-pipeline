package com.mindcurv.b2x.productcatalog.aggregator.aggregation;

import com.mindcurv.b2x.commons.models.B2xContext;
import com.mindcurv.b2x.commons.models.Pageable;
import com.mindcurv.b2x.commons.models.PageableSearchRequest;
import com.mindcurv.b2x.productcatalog.aggregator.models.CategoryAggregation;
import com.mindcurv.b2x.productcatalog.aggregator.models.CategoryNavigationAggregation;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface CategoryAggregationService {

  @NotNull
  Pageable<CategoryAggregation> getItems(
      @NotNull B2xContext b2xContext, @NotNull PageableSearchRequest searchRequest);

  @NotNull
  Optional<CategoryAggregation> findByKey(@Nullable String key, @NotNull B2xContext context);

  @NotNull
  Optional<CategoryAggregation> findById(@Nullable String id, @NotNull B2xContext context);

  @NotNull
  Optional<CategoryAggregation> findBySlug(@Nullable String slug, @NotNull B2xContext context);

  @NotNull
  Collection<CategoryNavigationAggregation> getAncestorsById(
      @NotNull String categoryId, @NotNull B2xContext context);

  @NotNull
  Collection<CategoryNavigationAggregation> getTree(@NotNull B2xContext context);

  @NotNull
  default List<String> getListExpansions() {
    return List.of("custom.type");
  }

  @NotNull
  default Collection<String> getDetailExpansions() {
    final var list = new LinkedList<>(getListExpansions());
    list.add("ancestors[*]");
    return list;
  }
}
