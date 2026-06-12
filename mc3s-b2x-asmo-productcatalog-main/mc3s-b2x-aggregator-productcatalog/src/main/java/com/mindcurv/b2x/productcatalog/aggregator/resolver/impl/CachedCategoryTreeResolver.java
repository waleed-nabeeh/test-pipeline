package com.mindcurv.b2x.productcatalog.aggregator.resolver.impl;

import com.mindcurv.b2x.commons.models.B2xContext;
import com.mindcurv.b2x.commons.resolver.BaseResolver;
import com.mindcurv.b2x.productcatalog.aggregator.models.CategoryNavigationAggregation;
import com.mindcurv.b2x.productcatalog.aggregator.services.CategoryTreeService;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CachedCategoryTreeResolver implements
    BaseResolver<Collection<CategoryNavigationAggregation>> {

  @NotNull
  private final CategoryTreeService categoryTreeService;

  @Autowired
  public CachedCategoryTreeResolver(@NotNull final CategoryTreeService categoryTreeService) {
    this.categoryTreeService = categoryTreeService;
  }

  @NotNull
  @Override
  public Collection<CategoryNavigationAggregation> resolve(@Nullable final Object... args) {
    final var item = args == null ? null : Arrays.stream(args).findFirst().orElse(null);
    return item instanceof B2xContext context ? getTree(context) : List.of();
  }

  @NotNull
  protected Collection<CategoryNavigationAggregation> getTree(@NotNull final B2xContext context) {
    return categoryTreeService.generateTree(context);
  }

}
