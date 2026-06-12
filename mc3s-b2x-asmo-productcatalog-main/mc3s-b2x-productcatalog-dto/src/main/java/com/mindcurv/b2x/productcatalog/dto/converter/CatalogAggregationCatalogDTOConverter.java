package com.mindcurv.b2x.productcatalog.dto.converter;

import static org.mapstruct.ReportingPolicy.IGNORE;

import com.mindcurv.b2x.commons.converter.BaseConverter;
import com.mindcurv.b2x.connector.catalog.models.CatalogDTO;
import com.mindcurv.b2x.connector.converter.StringLocalizedTextDTOConverter;
import com.mindcurv.b2x.productcatalog.aggregator.models.CatalogAggregation;
import jakarta.validation.Valid;
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
public abstract class CatalogAggregationCatalogDTOConverter implements
    BaseConverter<CatalogDTO, CatalogAggregation> {

  @Override
  @Nullable
  @Mapping(source = "seo.slug", target = "slug")
  @Mapping(source = "mixin", target = "custom")
  public abstract CatalogAggregation convert(@Nullable @Valid CatalogDTO source);
}
