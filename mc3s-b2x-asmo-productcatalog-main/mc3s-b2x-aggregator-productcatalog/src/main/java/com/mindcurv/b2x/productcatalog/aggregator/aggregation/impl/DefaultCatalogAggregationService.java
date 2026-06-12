package com.mindcurv.b2x.productcatalog.aggregator.aggregation.impl;

import static com.mindcurv.b2x.commons.commercetools.helper.PredicateHelper.byCustomType;
import static com.mindcurv.b2x.commons.models.Pageable.emptyPageable;
import static com.mindcurv.b2x.commons.models.Pageable.initPageableFromList;
import static com.mindcurv.b2x.customizing.helper.CustomFieldsHelper.getCustomFieldsAccessor;
import static java.util.Collections.singletonList;
import static org.apache.commons.collections4.CollectionUtils.emptyCollection;
import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

import com.commercetools.api.models.store.ProductSelectionSetting;
import com.commercetools.api.models.store.Store;
import com.commercetools.api.models.type.Type;
import com.mindcurv.b2x.commons.commercetools.services.impl.CategoryCommercetoolsService;
import com.mindcurv.b2x.commons.commercetools.services.impl.StoreCommercetoolsService;
import com.mindcurv.b2x.commons.models.B2xContext;
import com.mindcurv.b2x.commons.models.Pageable;
import com.mindcurv.b2x.commons.models.PageableSearchRequest;
import com.mindcurv.b2x.commons.resolver.NullableBaseResolver;
import com.mindcurv.b2x.customizing.configuration.impl.CatalogCategoryConfiguration;
import com.mindcurv.b2x.customizing.configuration.impl.ProductSelectionConfiguration;
import com.mindcurv.b2x.productcatalog.aggregator.aggregation.CatalogAggregationService;
import com.mindcurv.b2x.productcatalog.aggregator.converter.CatalogAggregationCategoryConverter;
import com.mindcurv.b2x.productcatalog.aggregator.models.CatalogAggregation;
import com.mindcurv.b2x.productcatalog.dto.converter.CatalogAggregationCatalogDTOConverter;
import com.mindcurv.b2x.productcatalog.dto.services.CatalogDTOService;
import jakarta.validation.Valid;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DefaultCatalogAggregationService implements CatalogAggregationService {

  private static final Logger LOG = LoggerFactory.getLogger(DefaultCatalogAggregationService.class);

  @NotNull
  private final CategoryCommercetoolsService categoryCommercetoolsService;
  @NotNull
  private final StoreCommercetoolsService storeCommercetoolsService;
  @NotNull
  @Valid
  private final NullableBaseResolver<Type> typeResolver;
  @NotNull
  private final CatalogAggregationCategoryConverter catalogAggregationConverter;
  @NotNull
  private final CatalogAggregationCatalogDTOConverter catalogAggregationCatalogDTOConverter;
  @NotNull
  private final CatalogDTOService catalogDTOService;

  private final CatalogCategoryConfiguration catalogCategoryConfiguration;
  private final ProductSelectionConfiguration productSelectionConfiguration;

  @Autowired
  public DefaultCatalogAggregationService(
      @NotNull final CategoryCommercetoolsService categoryCommercetoolsService,
      @NotNull final StoreCommercetoolsService storeCommercetoolsService,
      @NotNull final NullableBaseResolver<Type> typeResolver,
      @NotNull final CatalogAggregationCategoryConverter catalogAggregationConverter,
      @NotNull final CatalogAggregationCatalogDTOConverter catalogAggregationCatalogDTOConverter,
      @NotNull final CatalogDTOService catalogDTOService,
      final CatalogCategoryConfiguration catalogCategoryConfiguration,
      final ProductSelectionConfiguration productSelectionConfiguration) {
    this.categoryCommercetoolsService = categoryCommercetoolsService;
    this.storeCommercetoolsService = storeCommercetoolsService;
    this.typeResolver = typeResolver;
    this.catalogAggregationConverter = catalogAggregationConverter;
    this.catalogAggregationCatalogDTOConverter = catalogAggregationCatalogDTOConverter;
    this.catalogDTOService = catalogDTOService;
    this.catalogCategoryConfiguration = catalogCategoryConfiguration;
    this.productSelectionConfiguration = productSelectionConfiguration;
  }

  @NotNull
  @Override
  public Pageable<CatalogAggregation> getCatalogs(
      @NotNull final B2xContext context,
      @NotNull @Valid final PageableSearchRequest searchRequest) {
    final var storeOpt = storeCommercetoolsService.findByKey(context.getStore(), getStoreExpands());
    if (storeOpt.isEmpty()) {
      LOG.info("getCatalogs :: no store '{}' available", context.getStore());
      return emptyPageable();
    }
    final var dtoResult = catalogDTOService.getActiveItems(context.getStore());
    LOG.debug("getCatalogs :: active catalogDTOs {}", dtoResult);
    final var aggregation = catalogAggregationCatalogDTOConverter.convertList(dtoResult);
    LOG.debug("getCatalogs :: {} dto items", aggregation.size());

    final var type = typeResolver.resolve(catalogCategoryConfiguration);
    if (type.isPresent()) {
      final var predicate = byCustomType(type.get());
      final var result = categoryCommercetoolsService.getItems(predicate,
          searchRequest.getOffset(),
          searchRequest.getLimit());
      if (result != null) {
        final var catalogs = getActiveCatalogs(storeOpt.get());
        LOG.debug("getCatalogs :: active catalogs for {} - {}", context.getStore(), catalogs);
        final var catalogAggregationList = new LinkedList<CatalogAggregation>();
        emptyIfNull(result.getResults()).stream()
            .filter(item -> catalogs.contains(item.getKey())).toList()
            .forEach(category -> catalogAggregationConverter.convertAsOptional(category, context)
                .ifPresent(catalogAggregationList::add));
        final Pageable<CatalogAggregation> pageable = initPageableFromList(catalogAggregationList);
        return pageable.setResults(catalogAggregationList);
      }
    }
    LOG.info("getCatalogs :: no custom type present for '{}'",
        catalogCategoryConfiguration.getTypeKey());
    return emptyPageable();
  }

  @NotNull
  protected Collection<String> getActiveCatalogs(@NotNull @Valid final Store store) {
    final var catalogAttNameOpt = productSelectionConfiguration.getCatalogKeysName();
    if (catalogAttNameOpt.isEmpty()) {
      return emptyCollection();
    }
    final var catalogAttName = catalogAttNameOpt.get();
    final var catalogs = new LinkedHashSet<String>();
    for (final var setting : getActiveSettings(store)) {
      final var selectionReference = setting.getProductSelection();
      LOG.debug("activeCatalogs :: selectionReference {}", selectionReference);
      Optional.ofNullable(selectionReference.getObj())
          .flatMap(productSelection -> getCustomFieldsAccessor(productSelection.getCustom()))
          .ifPresent(accessor -> catalogs.addAll(emptyIfNull(
              accessor.asSetString(catalogAttName))));
    }
    return catalogs;
  }

  @NotNull
  protected Collection<ProductSelectionSetting> getActiveSettings(
      @NotNull @Valid final Store store) {
    LOG.info("getActiveSettings :: store '{}'", store.getKey());
    return store.getProductSelections().stream()
        .filter(setting -> isTrue(setting.getActive()))
        .toList();
  }

  @NotNull
  protected Collection<String> getStoreExpands() {
    return singletonList("productSelections[*].productSelection");
  }

}
