package com.mindcurv.b2x.productcatalog.aggregator.converter;

import static com.mindcurv.b2x.productcatalog.aggregator.helper.CatalogHelper.determineAssetsPrefix;
import static com.mindcurv.b2x.productcatalog.aggregator.helper.CatalogHelper.processUri;
import static org.mapstruct.ReportingPolicy.IGNORE;

import com.commercetools.api.models.common.AssetSource;
import com.mindcurv.b2x.commons.converter.BaseConverter;
import com.mindcurv.b2x.commons.models.URLMetaData;
import com.mindcurv.b2x.productcatalog.aggregator.models.AssetSourceAggregation;
import jakarta.validation.Valid;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring",
    unmappedTargetPolicy = IGNORE,
    implementationName = "Commercetools<CLASS_NAME>",
    implementationPackage = "<PACKAGE_NAME>.impl")
public abstract class AssetSourceAggregationAssetSourceConverter implements
    BaseConverter<AssetSource, AssetSourceAggregation> {

  @Autowired
  @NotNull
  private URLMetaData urlMetaData;

  @Override
  @Nullable
  @Mapping(source = "dimensions.w", target = "width")
  @Mapping(source = "dimensions.h", target = "height")
  public abstract AssetSourceAggregation convert(@Nullable AssetSource source);

  @NotNull
  @AfterMapping
  protected AssetSourceAggregation afterMapping(@NotNull @Valid final AssetSource source,
      @MappingTarget AssetSourceAggregation target) {
    final var prefix = determineAssetsPrefix(urlMetaData);
    return target.uri(processUri(source.getUri(), prefix));
  }
}
