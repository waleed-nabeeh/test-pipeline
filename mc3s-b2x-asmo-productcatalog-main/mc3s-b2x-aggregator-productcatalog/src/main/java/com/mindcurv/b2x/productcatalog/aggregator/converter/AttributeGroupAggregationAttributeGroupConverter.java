package com.mindcurv.b2x.productcatalog.aggregator.converter;

import static org.mapstruct.ReportingPolicy.IGNORE;

import com.commercetools.api.models.attribute_group.AttributeGroup;
import com.commercetools.api.models.attribute_group.AttributeReference;
import com.mindcurv.b2x.commons.base.converter.StringLocalizedStringConverter;
import com.mindcurv.b2x.commons.converter.BaseConverter;
import com.mindcurv.b2x.commons.models.B2xContext;
import com.mindcurv.b2x.commons.models.ConverterType;
import com.mindcurv.b2x.productcatalog.aggregator.models.AttributeGroupAggregation;
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
public abstract class AttributeGroupAggregationAttributeGroupConverter
    implements BaseConverter<AttributeGroup, AttributeGroupAggregation> {

  @Autowired
  @NotNull
  private StringLocalizedStringConverter stringLocalizedStringConverter;

  @Nullable
  @Override
  @Mapping(ignore = true, target = "name")
  @Mapping(ignore = true, target = "description")
  @Mapping(ignore = true, target = "attributes")
  public abstract AttributeGroupAggregation convert(@Nullable @Valid AttributeGroup source);

  @Override
  public @Nullable AttributeGroupAggregation processContextMapping(
      @Nullable @Valid final AttributeGroup source,
      @Nullable final AttributeGroupAggregation target,
      @NotNull final B2xContext context,
      @NotNull final ConverterType converterType) {
    if (source == null || target == null) {
      return target;
    }
    return target
        .name(stringLocalizedStringConverter.convert(source.getName(), context, converterType))
        .description(
            stringLocalizedStringConverter.convert(source.getDescription(), context, converterType))
        .attributes(source.getAttributes().stream().map(AttributeReference::getKey).toList());
  }
}
