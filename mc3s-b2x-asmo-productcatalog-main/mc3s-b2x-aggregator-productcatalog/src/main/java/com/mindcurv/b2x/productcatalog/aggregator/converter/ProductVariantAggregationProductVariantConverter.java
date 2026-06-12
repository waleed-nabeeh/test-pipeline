package com.mindcurv.b2x.productcatalog.aggregator.converter;

import static org.mapstruct.ReportingPolicy.IGNORE;

import com.commercetools.api.models.product.ProductVariant;
import com.mindcurv.b2x.commons.base.converter.ImageAggregationImageConverter;
import com.mindcurv.b2x.commons.base.converter.LogisticInformationProductVariantConverter;
import com.mindcurv.b2x.commons.converter.BaseConverter;
import com.mindcurv.b2x.commons.models.B2xContext;
import com.mindcurv.b2x.commons.models.ConverterType;
import com.mindcurv.b2x.productcatalog.aggregator.models.ProductVariantAggregation;
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
        AttributeAggregationAttributeConverter.class,
        ImageAggregationImageConverter.class,
        LogisticInformationProductVariantConverter.class
    })
public abstract class ProductVariantAggregationProductVariantConverter
    implements BaseConverter<ProductVariant, ProductVariantAggregation> {

  @Autowired
  @NotNull
  @Valid
  private AssetAggregationAssetConverter assetAggregationAssetConverter;

  @Override
  @Mapping(source = "source", target = "logisticInformation")
  @Mapping(ignore = true, target = "assets")
  public abstract ProductVariantAggregation convert(@Nullable @Valid ProductVariant source);

  @Override
  @Nullable
  public ProductVariantAggregation processContextMapping(
      @Nullable @Valid final ProductVariant source,
      @Nullable final ProductVariantAggregation target,
      @NotNull final B2xContext context,
      @NotNull final ConverterType converterType) {
    if (source == null || target == null) {
      return target;
    }
    return target.assets(assetAggregationAssetConverter.convertList(source.getAssets(), context)
        .stream().toList());
  }
}
