package com.mindcurv.b2x.productcatalog.aggregator.resolver.impl;

import static com.mindcurv.b2x.commons.base.BaseAggregatorCacheSpringConfig.CATEGORY_CACHE;
import static com.mindcurv.b2x.commons.helper.ValueHelper.isValidUuid;
import static com.mindcurv.b2x.productcatalog.aggregator.helper.CatalogHelper.CATEGORY_TREE_CACHE_KEY;

import com.commercetools.api.models.category.CategoryTree;
import com.mindcurv.b2x.commons.commercetools.services.impl.CategoryCommercetoolsService;
import com.mindcurv.b2x.commons.resolver.BaseResolver;
import com.mindcurv.b2x.productcatalog.aggregator.services.CategoryCacheService;
import jakarta.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CategoryTreeResolver implements BaseResolver<CategoryTree> {

  private static final Logger LOG = LoggerFactory.getLogger(CategoryTreeResolver.class);

  @NotNull
  private final CategoryCommercetoolsService categoryCommercetoolsService;

  @NotNull
  private final CacheManager cacheManager;

  @NotNull
  private final CategoryCacheService categoryCacheService;

  @Autowired
  public CategoryTreeResolver(
      @NotNull final CategoryCommercetoolsService categoryCommercetoolsService,
      @NotNull final CacheManager cacheManager,
      @NotNull final CategoryCacheService categoryCacheService) {
    this.categoryCommercetoolsService = categoryCommercetoolsService;
    this.cacheManager = cacheManager;
    this.categoryCacheService = categoryCacheService;
  }

  @PostConstruct
  private void postConstruct() {
    final var categories = categoryCommercetoolsService.findAll(getExpansions());
    Optional.ofNullable(cacheManager.getCache(CATEGORY_CACHE)).ifPresentOrElse(
        cache -> {
          cache.clear();
          cache.put(CATEGORY_TREE_CACHE_KEY, categories);
        },
        () -> LOG.warn("postConstruct :: categoryCache is missing"));
    LOG.info("postConstruct :: categoryTree {} items is saved in {} cache, key:{}",
        categories.size(), CATEGORY_CACHE, CATEGORY_TREE_CACHE_KEY);
  }

  @Override
  @NotNull
  public CategoryTree resolve(@Nullable final Object... args) {
    final var categoryCacheList = categoryCacheService.findAllCategories(getExpansions());
    final var resolverCache = CategoryTree.of(categoryCacheList.parallelStream().toList());
    final var item = args == null ? null : Arrays.stream(args).findFirst().orElse(null);
    if (item instanceof String identifier) {
      final var category = isValidUuid(identifier) ?
          resolverCache.findById(identifier) : resolverCache.findByKey(identifier);
      return category.map(value -> CategoryTree.of(resolverCache.findChildren(value)))
          .orElse(resolverCache);
    }
    return resolverCache;
  }

  @Override
  @NotNull
  public Collection<CategoryTree> getAllItems() {
    return List.of(
        CategoryTree.of(categoryCacheService.findAllCategories(getExpansions()).parallelStream()
            .toList()));
  }

  @Override
  @Scheduled(cron = "${cron.expression.CategoryTreeResolver.reload:0 0 0/1 * * *}")
  public void reload() {
    LOG.info("reload :: scheduler triggered");
    postConstruct();
  }

  @Override
  @NotNull
  public List<String> getExpansions() {
    return List.of("ancestors[*]");
  }
}
