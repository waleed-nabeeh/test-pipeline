package com.mindcurv.b2x.productcatalog.aggregator.converter;

import static org.mapstruct.ReportingPolicy.IGNORE;

import com.commercetools.api.models.common.Asset;
import com.mindcurv.b2x.commons.base.converter.CustomAggregationCustomFieldsConverter;
import com.mindcurv.b2x.commons.base.converter.StringLocalizedStringConverter;
import com.mindcurv.b2x.commons.converter.BaseConverter;
import com.mindcurv.b2x.commons.models.B2xContext;
import com.mindcurv.b2x.commons.models.ConverterType;
import com.mindcurv.b2x.productcatalog.aggregator.models.AssetAggregation;
import jakarta.validation.Valid;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring",
    unmappedTargetPolicy = IGNORE,
    implementationName = "Commercetools<CLASS_NAME>",
    implementationPackage = "<PACKAGE_NAME>.impl",
    uses = {
        AssetSourceAggregationAssetSourceConverter.class
    })
public abstract class AssetAggregationAssetConverter implements
    BaseConverter<Asset, AssetAggregation> {

  @Autowired
  @NotNull
  private StringLocalizedStringConverter stringLocalizedStringConverter;
  @Autowired
  @NotNull
  private CustomAggregationCustomFieldsConverter customAggregationCustomFieldsConverter;

  @Override
  @Nullable
  @Mapping(ignore = true, target = "name")
  @Mapping(ignore = true, target = "description")
  @Mapping(ignore = true, target = "custom")
  public abstract AssetAggregation convert(@Nullable @Valid Asset source);

  @Override
  @Nullable
  public AssetAggregation processContextMapping(
      @Nullable @Valid final Asset source,
      @Nullable final AssetAggregation target,
      @NotNull final B2xContext context,
      @NotNull final ConverterType type) {
    if (source == null || target == null) {
      return target;
    }
    return target
        .name(stringLocalizedStringConverter.convert(source.getName(), context, type))
        .description(stringLocalizedStringConverter.convert(source.getDescription(), context, type))
        .custom(customAggregationCustomFieldsConverter.convert(source.getCustom(), context, type));
  }
}
