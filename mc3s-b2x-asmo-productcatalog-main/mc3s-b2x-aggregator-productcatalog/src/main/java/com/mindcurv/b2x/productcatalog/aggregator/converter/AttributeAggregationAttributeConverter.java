package com.mindcurv.b2x.productcatalog.aggregator.converter;

import static org.mapstruct.ReportingPolicy.IGNORE;

import com.commercetools.api.models.product.Attribute;
import com.mindcurv.b2x.commons.converter.BaseConverter;
import com.mindcurv.b2x.commons.models.AttributeAggregation;
import org.jetbrains.annotations.Nullable;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring",
    unmappedTargetPolicy = IGNORE,
    implementationName = "Commercetools<CLASS_NAME>",
    implementationPackage = "<PACKAGE_NAME>.impl")
public interface AttributeAggregationAttributeConverter extends
    BaseConverter<Attribute, AttributeAggregation> {

  @Override
  @Nullable
  AttributeAggregation convert(@Nullable Attribute source);

}
