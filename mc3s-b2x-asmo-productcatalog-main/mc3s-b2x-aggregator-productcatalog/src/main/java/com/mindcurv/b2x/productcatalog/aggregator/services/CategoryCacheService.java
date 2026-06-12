package com.mindcurv.b2x.productcatalog.aggregator.services;

import com.commercetools.api.models.category.Category;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;

public interface CategoryCacheService {

  @NotNull
  Collection<Category> findAllCategories(@NotNull Collection<String> expands);

}
