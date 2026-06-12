package com.mindcurv.b2x.productcatalog.aggregator.converter;

import static com.mindcurv.b2x.commons.models.ConverterType.DETAIL;
import static com.mindcurv.b2x.productcatalog.aggregator.helper.CatalogAccessHelper.getRestrictedCustomerIds;
import static com.mindcurv.b2x.productcatalog.aggregator.helper.CatalogHelper.processImageFromAssets;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.mapstruct.ReportingPolicy.IGNORE;

import com.commercetools.api.models.category.Category;
import com.mindcurv.b2x.commons.base.converter.CustomAggregationCustomFieldsConverter;
import com.mindcurv.b2x.commons.base.converter.StringLocalizedStringConverter;
import com.mindcurv.b2x.commons.converter.BaseConverter;
import com.mindcurv.b2x.commons.models.B2xContext;
import com.mindcurv.b2x.commons.models.ConverterType;
import com.mindcurv.b2x.customizing.configuration.impl.CatalogCategoryConfiguration;
import com.mindcurv.b2x.productcatalog.aggregator.models.CategoryNavigationAggregation;
import jakarta.validation.Valid;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring",
    unmappedTargetPolicy = IGNORE,
    implementationName = "Commercetools<CLASS_NAME>",
    implementationPackage = "<PACKAGE_NAME>.impl")
public abstract class CategoryNavigationAggregationCategoryConverter
    implements BaseConverter<Category, CategoryNavigationAggregation> {

  @Autowired
  @NotNull
  private AssetAggregationAssetConverter assetAggregationAssetConverter;
  @Autowired
  @NotNull
  private CustomAggregationCustomFieldsConverter customAggregationCustomFieldsConverter;
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
  @Mapping(ignore = true, target = "custom")
  public abstract CategoryNavigationAggregation convert(@Nullable @Valid Category source);

  @Override
  @Nullable
  public CategoryNavigationAggregation processContextMapping(
      @Nullable @Valid final Category source,
      @Nullable final CategoryNavigationAggregation target,
      @NotNull final B2xContext context,
      @NotNull final ConverterType type) {
    if (source == null || target == null) {
      return target;
    }
    final var restrictedIds = getRestrictedCustomerIds(source.getCustom(),
        catalogCategoryConfiguration.getRestrictedToName().orElse(null));
    target
        .slug(stringLocalizedStringConverter.convert(source.getSlug(), context, type))
        .description(
            stringLocalizedStringConverter.convert(source.getDescription(), context, type))
        .name(stringLocalizedStringConverter.convertAsOptional(source.getName(), context, type)
            .orElse(source.getKey()))
        .restrictedCustomers(restrictedIds.stream().toList())
        .restricted(isNotEmpty(restrictedIds));
    processImageFromAssets(
        assetAggregationAssetConverter.convertList(source.getAssets(), context)).ifPresent(
        target::image);
    if (type == DETAIL) {
      target.ancestors(getAncestors(source, context, type).stream().toList());
    }
    return target.custom(
        customAggregationCustomFieldsConverter.convert(source.getCustom(), context, type));
  }

  @NotNull
  protected Collection<CategoryNavigationAggregation> getAncestors(
      @NotNull @Valid final Category source,
      @NotNull final B2xContext context,
      @NotNull final ConverterType type) {
    final var ancestors = new LinkedList<CategoryNavigationAggregation>();
    source.getAncestors().forEach(item -> Optional.ofNullable(item.getObj()).ifPresent(
        ancestor -> ancestors.add(processCategory(ancestor, context, type))));
    return ancestors;
  }

  @NotNull
  protected CategoryNavigationAggregation processCategory(@NotNull @Valid final Category source,
      @NotNull final B2xContext context, @NotNull final ConverterType type) {
    final var target = convertAsOptional(source)
        .orElse(new CategoryNavigationAggregation().id(source.getId()).key(source.getKey()))
        .name(stringLocalizedStringConverter.convertAsOptional(source.getName(), context, type)
            .orElse(source.getKey()))
        .description(stringLocalizedStringConverter.convert(source.getDescription(), context, type))
        .slug(stringLocalizedStringConverter.convert(source.getSlug(), context, type));
    processImageFromAssets(assetAggregationAssetConverter.convertList(source.getAssets(), context))
        .ifPresent(target::image);
    return target;
  }
}
