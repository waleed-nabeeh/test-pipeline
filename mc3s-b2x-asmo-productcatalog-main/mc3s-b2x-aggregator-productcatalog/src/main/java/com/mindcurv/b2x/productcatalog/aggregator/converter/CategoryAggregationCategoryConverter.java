package com.mindcurv.b2x.productcatalog.aggregator.converter;

import static com.mindcurv.b2x.commons.models.ConverterType.DETAIL;
import static com.mindcurv.b2x.productcatalog.aggregator.helper.CatalogAccessHelper.accessAllowed;
import static com.mindcurv.b2x.productcatalog.aggregator.helper.CatalogAccessHelper.getRestrictedCustomerIds;
import static com.mindcurv.b2x.productcatalog.aggregator.helper.CatalogHelper.processImageFromAssets;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.BooleanUtils.isNotTrue;
import static org.mapstruct.ReportingPolicy.IGNORE;

import com.commercetools.api.models.category.Category;
import com.commercetools.api.models.category.CategoryTree;
import com.mindcurv.b2x.commons.base.converter.CustomAggregationCustomFieldsConverter;
import com.mindcurv.b2x.commons.base.converter.StringLocalizedStringConverter;
import com.mindcurv.b2x.commons.converter.BaseConverter;
import com.mindcurv.b2x.commons.models.B2xContext;
import com.mindcurv.b2x.commons.models.ConverterType;
import com.mindcurv.b2x.commons.resolver.BaseResolver;
import com.mindcurv.b2x.customizing.configuration.impl.CatalogCategoryConfiguration;
import com.mindcurv.b2x.productcatalog.aggregator.models.CategoryAggregation;
import jakarta.validation.Valid;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring",
    unmappedTargetPolicy = IGNORE,
    implementationName = "Commercetools<CLASS_NAME>",
    implementationPackage = "<PACKAGE_NAME>.impl")
public abstract class CategoryAggregationCategoryConverter implements
    BaseConverter<Category, CategoryAggregation> {

  private static final Logger LOG = LoggerFactory.getLogger(
      CategoryAggregationCategoryConverter.class);

  @Autowired
  @NotNull
  private BaseResolver<CategoryTree> categoryTreeResolver;
  @Autowired
  @NotNull
  private CategoryNavigationAggregationCategoryReferenceConverter categoryNavigationAggregationCategoryReferenceConverter;
  @Autowired
  @NotNull
  private CategoryNavigationAggregationCategoryConverter categoryNavigationAggregationCategoryConverter;
  @Autowired
  @NotNull
  private CustomAggregationCustomFieldsConverter customAggregationCustomFieldsConverter;
  @Autowired
  @NotNull
  private AssetAggregationAssetConverter assetAggregationAssetConverter;
  @Autowired
  @NotNull
  private MetaDataAggregationCategoryConverter metaDataAggregationCategoryConverter;
  @Autowired
  @NotNull
  private StringLocalizedStringConverter stringLocalizedStringConverter;
  @Autowired
  @NotNull
  private CatalogCategoryConfiguration catalogCategoryConfiguration;

  @Override
  @Nullable
  @Mapping(ignore = true, target = "name")
  @Mapping(ignore = true, target = "description")
  @Mapping(ignore = true, target = "slug")
  @Mapping(ignore = true, target = "assets")
  @Mapping(ignore = true, target = "custom")
  public abstract CategoryAggregation convert(@Nullable @Valid Category source);

  @Override
  public @Nullable CategoryAggregation processContextMapping(@Nullable Category source,
      @Nullable CategoryAggregation target,
      @NotNull B2xContext context,
      @NotNull ConverterType type) {
    if (source == null || target == null) {
      return target;
    }
    final var restrictedIds = getRestrictedCustomerIds(source.getCustom(),
        catalogCategoryConfiguration.getRestrictedToName().orElse(null));
    if (isNotTrue(accessAllowed(restrictedIds, context))) {
      LOG.info("convert :: access to Category {} not allowed {}", source.getId(), restrictedIds);
      return null;
    }
    target
        .slug(stringLocalizedStringConverter.convert(source.getSlug(), context, type))
        .restrictedCustomers(restrictedIds.stream().toList())
        .restricted(isNotEmpty(restrictedIds))
        .description(stringLocalizedStringConverter.convert(source.getDescription(), context, type))
        .name(stringLocalizedStringConverter.convertAsOptional(source.getName(), context, type)
            .orElse(source.getKey()));
    final var assets = assetAggregationAssetConverter.convertList(source.getAssets(), context,
        type);
    if (type == DETAIL) {
      target
          .metaData(metaDataAggregationCategoryConverter.convert(source, context, type))
          .assets(assets.stream().toList())
          .custom(customAggregationCustomFieldsConverter.convert(source.getCustom(), context, type))
          .ancestors(categoryNavigationAggregationCategoryReferenceConverter.convertList(
              source.getAncestors(), context, type).stream().toList())
          .children(categoryNavigationAggregationCategoryConverter.convertList(
                  categoryTreeResolver.resolve().findChildren(source), context, type).stream()
              .toList());
    }
    processImageFromAssets(assets).ifPresent(target::image);
    return target;
  }
}
