package com.mindcurv.b2x.productcatalog.aggregator.services.impl;

import static com.mindcurv.b2x.commons.base.BaseAggregatorCacheSpringConfig.CATEGORY_CACHE;
import static com.mindcurv.b2x.customizing.helper.CustomFieldsHelper.getCustomFieldsAccessor;
import static com.mindcurv.b2x.productcatalog.aggregator.helper.CatalogAccessHelper.checkAccess;
import static com.mindcurv.b2x.productcatalog.aggregator.helper.CatalogHelper.getMainNavigationKey;
import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;

import com.commercetools.api.models.category.Category;
import com.commercetools.api.models.category.CategoryTree;
import com.commercetools.api.models.store.ProductSelectionSetting;
import com.commercetools.api.models.store.Store;
import com.mindcurv.b2x.commons.models.B2xContext;
import com.mindcurv.b2x.commons.resolver.BaseResolver;
import com.mindcurv.b2x.commons.resolver.NullableBaseResolver;
import com.mindcurv.b2x.customizing.configuration.impl.ProductSelectionConfiguration;
import com.mindcurv.b2x.customizing.configuration.impl.StoreConfiguration;
import com.mindcurv.b2x.productcatalog.aggregator.converter.CategoryNavigationAggregationCategoryConverter;
import com.mindcurv.b2x.productcatalog.aggregator.models.CategoryNavigationAggregation;
import com.mindcurv.b2x.productcatalog.aggregator.services.CategoryTreeService;
import jakarta.validation.Valid;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.lang3.Range;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class DefaultCategoryTreeService implements CategoryTreeService {

  private static final Logger LOG = LoggerFactory.getLogger(DefaultCategoryTreeService.class);

  public static final long DEFAULT_LEVELS = 2;
  @NotNull
  @Valid
  private final NullableBaseResolver<Store> storeResolver;
  @NotNull
  @Valid
  private final BaseResolver<CategoryTree> categoryTreeResolver;
  @NotNull
  private final CategoryNavigationAggregationCategoryConverter categoryNavigationAggregationConverter;
  @NotNull
  private final ProductSelectionConfiguration productSelectionConfiguration;
  @NotNull
  private final StoreConfiguration storeConfiguration;

  @Autowired
  public DefaultCategoryTreeService(
      @NotNull final NullableBaseResolver<Store> storeResolver,
      @NotNull final BaseResolver<CategoryTree> categoryTreeResolver,
      @NotNull final CategoryNavigationAggregationCategoryConverter categoryNavigationAggregationConverter,
      @NotNull final ProductSelectionConfiguration productSelectionConfiguration,
      @NotNull final StoreConfiguration storeConfiguration) {
    this.storeResolver = storeResolver;
    this.categoryTreeResolver = categoryTreeResolver;
    this.categoryNavigationAggregationConverter = categoryNavigationAggregationConverter;
    this.productSelectionConfiguration = productSelectionConfiguration;
    this.storeConfiguration = storeConfiguration;
  }

  @Override
  @NotNull
  @Cacheable(
      value = CATEGORY_CACHE,
      key = "T(com.mindcurv.b2x.productcatalog.aggregator.helper.CatalogHelper).getMainNavigationKey(#context)")
  public Collection<CategoryNavigationAggregation> generateTree(@NotNull final B2xContext context) {
    if (LOG.isDebugEnabled()) {
      LOG.debug("generateTree :: cacheKey '{}'", getMainNavigationKey(context));
    }
    final var tree = getStoreCategoryTrees(context);
    final var navigation = new LinkedList<CategoryNavigationAggregation>();
    final var range = Range.of(0, getMaxLevels(context).intValue());
    for (final var catalog : tree.getRoots()) {
      LOG.info("generateTree :: catalog '{}'", catalog.getKey());
      categoryNavigationAggregationConverter.convertAsOptional(catalog, context)
          .flatMap(navigationAggregation -> checkAccess(navigationAggregation, context))
          .ifPresent(categoryNavigationAggregation -> navigation.add(
              categoryNavigationAggregation.children(
                  processChildren(catalog, tree.getSubtree(List.of(catalog)), 0, range,
                      context).stream().toList())));
    }
    if (LOG.isDebugEnabled()) {
      LOG.debug("generateTree :: navigation {}", navigation.size());
    }
    return navigation;
  }

  @NotNull
  protected Collection<CategoryNavigationAggregation> processChildren(
      @NotNull @Valid final Category category,
      @NotNull final CategoryTree catalogTree,
      final int currentLevel,
      final Range<Integer> levelRange,
      @NotNull final B2xContext context) {
    LOG.debug("processChildren :: category '{}'", category.getKey());
    final var navigation = new LinkedList<CategoryNavigationAggregation>();
    if (currentLevel < levelRange.getMaximum()) {
      for (final var child : catalogTree.findChildren(category)) {
        LOG.debug("processChildren :: child '{}'", child.getKey());
        categoryNavigationAggregationConverter.convertAsOptional(child,
                context).flatMap(navigationAggregation -> checkAccess(navigationAggregation, context))
            .ifPresent(categoryNavigationAggregation -> navigation.add(
                categoryNavigationAggregation.children(
                    processChildren(child, catalogTree.getSubtree(List.of(child)), currentLevel,
                        levelRange, context).stream().toList())));
      }
    }
    LOG.debug("processChildren :: category '{}' - {}", category.getKey(), navigation.size());
    return navigation;
  }

  @NotNull
  protected CategoryTree getStoreCategoryTrees(@NotNull final B2xContext context) {
    final var catalogKeys = getStoreCatalogs(context);
    final var tree = categoryTreeResolver.resolve();
    if (isEmpty(catalogKeys)) {
      return tree;
    }
    final var matches = new LinkedHashSet<Category>();
    catalogKeys.forEach(catalog -> tree.findByKey(catalog).ifPresent(matches::add));
    return Optional.ofNullable(tree.getSubtree(matches)).orElse(tree);
  }

  @NotNull
  protected Collection<String> getStoreCatalogs(@NotNull final B2xContext context) {
    final var catalogKeysAttName = productSelectionConfiguration.getCatalogKeysName();
    if (catalogKeysAttName.isEmpty()) {
      LOG.debug("getStoreCatalogs :: no catalog key attribute present");
      return Set.of();
    }
    final var selectionCategories = new LinkedHashSet<String>();
    storeResolver.resolve(context.getStore())
        .ifPresent(store1 -> store1.getProductSelections().stream()
            .filter(ProductSelectionSetting::getActive).toList()
            .forEach(selection -> Optional.ofNullable(selection.getProductSelection().getObj())
                .flatMap(value -> getCustomFieldsAccessor(value.getCustom()))
                .ifPresent(customFieldsAccessor -> selectionCategories.addAll(
                    emptyIfNull(customFieldsAccessor.asSetString(catalogKeysAttName.get()))))));
    LOG.debug("getStoreCatalogs :: {} selectionCategories ", selectionCategories.size());
    return selectionCategories;
  }

  @NotNull
  protected Long getMaxLevels(@NotNull final B2xContext context) {
    return storeConfiguration.getNavigationLevelsName()
        .map(attName -> storeResolver.resolve(context.getStore())
            .map(store -> getCustomFieldsAccessor(store.getCustom()).map(
                customFieldsAccessor -> Optional.ofNullable(customFieldsAccessor.asLong(attName))
                    .orElse(DEFAULT_LEVELS)).orElse(DEFAULT_LEVELS)).orElse(DEFAULT_LEVELS))
        .orElse(DEFAULT_LEVELS);
  }
}
