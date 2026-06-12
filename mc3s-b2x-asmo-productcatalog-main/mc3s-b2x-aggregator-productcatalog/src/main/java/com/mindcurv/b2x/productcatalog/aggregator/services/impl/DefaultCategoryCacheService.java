package com.mindcurv.b2x.productcatalog.aggregator.services.impl;

import static com.mindcurv.b2x.commons.base.BaseAggregatorCacheSpringConfig.CATEGORY_CACHE;
import static com.mindcurv.b2x.productcatalog.aggregator.helper.CatalogHelper.CATEGORY_TREE_CACHE_KEY;

import com.commercetools.api.models.category.Category;
import com.mindcurv.b2x.commons.commercetools.services.impl.CategoryCommercetoolsService;
import com.mindcurv.b2x.productcatalog.aggregator.services.CategoryCacheService;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class DefaultCategoryCacheService implements CategoryCacheService {

  private static final Logger LOG = LoggerFactory.getLogger(DefaultCategoryCacheService.class);

  @NotNull
  private final CategoryCommercetoolsService categoryCommercetoolsService;

  @Autowired
  public DefaultCategoryCacheService(
      @NotNull final CategoryCommercetoolsService categoryCommercetoolsService) {
    this.categoryCommercetoolsService = categoryCommercetoolsService;
  }

  @Override
  @NotNull
  @Cacheable(value = CATEGORY_CACHE, key = "T(com.mindcurv.b2x.productcatalog.aggregator.helper.CatalogHelper).CATEGORY_TREE_CACHE_KEY")
  public Collection<Category> findAllCategories(@NotNull final Collection<String> expands) {
    LOG.info("findAllCategories :: load all categories [{}/{}]", CATEGORY_CACHE,
        CATEGORY_TREE_CACHE_KEY);
    return categoryCommercetoolsService.findAll(expands.stream().toList());
  }
}
