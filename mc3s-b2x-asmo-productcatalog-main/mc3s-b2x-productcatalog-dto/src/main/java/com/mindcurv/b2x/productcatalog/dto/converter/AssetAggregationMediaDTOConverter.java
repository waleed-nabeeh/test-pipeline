package com.mindcurv.b2x.productcatalog.dto.converter;

import static org.mapstruct.ReportingPolicy.IGNORE;

import com.mindcurv.b2x.commons.converter.BaseConverter;
import com.mindcurv.b2x.connector.converter.StringLocalizedTextDTOConverter;
import com.mindcurv.b2x.connector.media.models.MediaDTO;
import com.mindcurv.b2x.productcatalog.aggregator.models.AssetAggregation;
import org.jetbrains.annotations.Nullable;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",
    unmappedTargetPolicy = IGNORE,
    implementationName = "Default<CLASS_NAME>",
    implementationPackage = "<PACKAGE_NAME>.impl",
    uses = {
        CustomAggregationMixinConverter.class,
        StringLocalizedTextDTOConverter.class
    })
public abstract class AssetAggregationMediaDTOConverter implements
    BaseConverter<MediaDTO, AssetAggregation> {

  @Override
  @Nullable
  @Mapping(source = "mixin", target = "custom")
  public abstract AssetAggregation convert(@Nullable MediaDTO source);
}
