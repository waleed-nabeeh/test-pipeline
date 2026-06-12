package com.mindcurv.b2x.productcatalog.aggregator.aggregation.impl;

import static com.mindcurv.b2x.commons.models.ConverterType.DETAIL;
import static com.mindcurv.b2x.productcatalog.aggregator.helper.CatalogAccessHelper.checkAccess;
import static org.apache.commons.lang3.StringUtils.isBlank;

import com.commercetools.api.models.category.Category;
import com.mindcurv.b2x.commons.commercetools.services.impl.CategoryCommercetoolsService;
import com.mindcurv.b2x.commons.models.B2xContext;
import com.mindcurv.b2x.commons.models.Pageable;
import com.mindcurv.b2x.commons.models.PageableSearchRequest;
import com.mindcurv.b2x.commons.resolver.BaseResolver;
import com.mindcurv.b2x.commons.resolver.NullableBaseResolver;
import com.mindcurv.b2x.productcatalog.aggregator.aggregation.CategoryAggregationService;
import com.mindcurv.b2x.productcatalog.aggregator.converter.CategoryAggregationCategoryConverter;
import com.mindcurv.b2x.productcatalog.aggregator.converter.CategoryNavigationAggregationCategoryReferenceConverter;
import com.mindcurv.b2x.productcatalog.aggregator.models.CategoryAggregation;
import com.mindcurv.b2x.productcatalog.aggregator.models.CategoryNavigationAggregation;
import jakarta.validation.Valid;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DefaultCategoryAggregationService implements CategoryAggregationService {

  private static final Logger LOG = LoggerFactory.getLogger(
      DefaultCategoryAggregationService.class);

  @NotNull
  private final CategoryCommercetoolsService categoryCommercetoolsService;
  @NotNull
  private final CategoryNavigationAggregationCategoryReferenceConverter categoryNavigationConverter;
  @NotNull
  private final CategoryAggregationCategoryConverter categoryAggregationConverter;
  @NotNull
  @Valid
  private final BaseResolver<Collection<CategoryNavigationAggregation>> categoryListResolver;
  @NotNull
  @Valid
  private final NullableBaseResolver<Category> categoryResolver;

  @Autowired
  public DefaultCategoryAggregationService(
      @NotNull final CategoryCommercetoolsService categoryCommercetoolsService,
      @NotNull final CategoryNavigationAggregationCategoryReferenceConverter categoryNavigationConverter,
      @NotNull final CategoryAggregationCategoryConverter categoryAggregationConverter,
      @NotNull final BaseResolver<Collection<CategoryNavigationAggregation>> categoryListResolver,
      @NotNull final NullableBaseResolver<Category> categoryResolver) {
    this.categoryCommercetoolsService = categoryCommercetoolsService;
    this.categoryNavigationConverter = categoryNavigationConverter;
    this.categoryAggregationConverter = categoryAggregationConverter;
    this.categoryListResolver = categoryListResolver;
    this.categoryResolver = categoryResolver;
  }

  @NotNull
  @Override
  public Pageable<CategoryAggregation> getItems(@NotNull final B2xContext context,
      @NotNull @Valid final PageableSearchRequest searchRequest) {
    return categoryAggregationConverter.convertPageableFromResponse(
            categoryCommercetoolsService.getItems(searchRequest), context)
        .setSearchRequest(searchRequest);
  }

  @NotNull
  @Override
  public Optional<CategoryAggregation> findByKey(@Nullable final String key,
      @NotNull final B2xContext context) {
    LOG.debug("findByKey :: {}", key);
    return isBlank(key) ? Optional.empty() : categoryResolver.resolve(key)
                                             .map(category -> categoryAggregationConverter.convert(
                                                 category, context, DETAIL));
  }

  @NotNull
  @Override
  public Optional<CategoryAggregation> findById(@Nullable final String id,
      @NotNull final B2xContext context) {
    LOG.debug("findById :: {}", id);
    return isBlank(id) ? Optional.empty() : categoryResolver.resolve(id)
                                            .map(category -> categoryAggregationConverter.convert(
                                                category, context, DETAIL));
  }

  @Override
  @NotNull
  public Optional<CategoryAggregation> findBySlug(@Nullable final String slug,
      @NotNull final B2xContext context) {
    LOG.debug("findBySlug :: {} [{}]", slug, context.getCurrentLanguage());
    return isBlank(slug) ? Optional.empty()
        : categoryResolver.resolve(slug, context.getCurrentLanguage())
          .map(cat -> categoryAggregationConverter.convert(cat, context, DETAIL));
  }

  @NotNull
  @Override
  public Collection<CategoryNavigationAggregation> getAncestorsById(
      @NotNull final String categoryId,
      @NotNull final B2xContext context) {
    return categoryResolver.resolve(categoryId).map(
        category -> categoryNavigationConverter.convertList(category.getAncestors(), context,
            DETAIL)).orElseGet(List::of);
  }

  @NotNull
  @Override
  public Collection<CategoryNavigationAggregation> getTree(@NotNull final B2xContext context) {
    final var filtered = new LinkedList<CategoryNavigationAggregation>();
    categoryListResolver.resolve(context).forEach(root ->
        checkAccess(root, context).ifPresent(filtered::add));
    return filtered;
  }

}
