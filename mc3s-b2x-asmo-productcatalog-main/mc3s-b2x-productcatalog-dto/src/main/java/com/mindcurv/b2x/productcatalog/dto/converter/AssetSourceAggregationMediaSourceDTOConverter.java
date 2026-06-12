package com.mindcurv.b2x.productcatalog.dto.converter;

import static org.mapstruct.ReportingPolicy.IGNORE;

import com.mindcurv.b2x.commons.converter.BaseConverter;
import com.mindcurv.b2x.connector.converter.StringLocalizedTextDTOConverter;
import com.mindcurv.b2x.connector.media.models.MediaSourceDTO;
import com.mindcurv.b2x.productcatalog.aggregator.models.AssetSourceAggregation;
import org.jetbrains.annotations.Nullable;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring",
    unmappedTargetPolicy = IGNORE,
    implementationName = "Default<CLASS_NAME>",
    implementationPackage = "<PACKAGE_NAME>.impl",
    uses = {
        CustomAggregationMixinConverter.class,
        StringLocalizedTextDTOConverter.class
    })
public interface AssetSourceAggregationMediaSourceDTOConverter extends
    BaseConverter<MediaSourceDTO, AssetSourceAggregation> {

  @Override
  @Nullable
  AssetSourceAggregation convert(@Nullable MediaSourceDTO source);

}
