package com.mindcurv.b2x.productcatalog.aggregator.converter;

import static com.mindcurv.b2x.commons.models.ConverterType.DETAIL;
import static com.mindcurv.b2x.productcatalog.aggregator.helper.CatalogHelper.processImageFromAssets;
import static org.mapstruct.ReportingPolicy.IGNORE;

import com.commercetools.api.models.category.Category;
import com.mindcurv.b2x.commons.base.converter.CustomAggregationCustomFieldsConverter;
import com.mindcurv.b2x.commons.base.converter.StringLocalizedStringConverter;
import com.mindcurv.b2x.commons.converter.BaseConverter;
import com.mindcurv.b2x.commons.models.B2xContext;
import com.mindcurv.b2x.commons.models.ConverterType;
import com.mindcurv.b2x.productcatalog.aggregator.models.CatalogAggregation;
import jakarta.validation.Valid;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring",
    unmappedTargetPolicy = IGNORE,
    implementationName = "Commercetools<CLASS_NAME>",
    implementationPackage = "<PACKAGE_NAME>.impl")
public abstract class CatalogAggregationCategoryConverter implements
    BaseConverter<Category, CatalogAggregation> {

  @Autowired
  @NotNull
  private AssetAggregationAssetConverter assetAggregationAssetConverter;
  @Autowired
  @NotNull
  private CustomAggregationCustomFieldsConverter customAggregationCustomFieldsConverter;
  @Autowired
  @NotNull
  private MetaDataAggregationCategoryConverter metaDataAggregationCategoryConverter;
  @Autowired
  @NotNull
  private StringLocalizedStringConverter stringLocalizedStringConverter;

  @Override
  @Nullable
  @Mapping(ignore = true, target = "name")
  @Mapping(ignore = true, target = "description")
  @Mapping(ignore = true, target = "slug")
  @Mapping(ignore = true, target = "assets")
  @Mapping(ignore = true, target = "custom")
  public abstract CatalogAggregation convert(@Nullable @Valid Category source);

  @Override
  @Nullable
  public CatalogAggregation processContextMapping(@Nullable Category source,
      @Nullable CatalogAggregation target,
      @NotNull B2xContext context,
      @NotNull ConverterType type) {
    if (source == null || target == null) {
      return target;
    }
    target
        .name(stringLocalizedStringConverter.convert(source.getName(), context, type))
        .description(stringLocalizedStringConverter.convert(source.getDescription(), context, type))
        .slug(stringLocalizedStringConverter.convert(source.getSlug(), context, type));
    final var assets = assetAggregationAssetConverter.convertList(source.getAssets(), context,
        type);
    if (type == DETAIL) {
      target.description(
              stringLocalizedStringConverter.convert(source.getDescription(), context, type))
          .metaData(metaDataAggregationCategoryConverter.convert(source, context, type))
          .assets(assets.stream().toList())
          .custom(
              customAggregationCustomFieldsConverter.convert(source.getCustom(), context, type));
    }
    processImageFromAssets(assets).ifPresent(target::image);
    return target;
  }

}
