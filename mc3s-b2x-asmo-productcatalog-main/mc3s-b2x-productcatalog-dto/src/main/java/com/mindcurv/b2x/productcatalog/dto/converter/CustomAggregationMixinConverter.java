package com.mindcurv.b2x.productcatalog.dto.converter;

import static java.util.stream.Collectors.toMap;
import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;
import static org.mapstruct.ReportingPolicy.IGNORE;

import com.mindcurv.b2x.commons.converter.BaseConverter;
import com.mindcurv.b2x.commons.models.AttributeAggregation;
import com.mindcurv.b2x.commons.models.CustomAggregation;
import com.mindcurv.b2x.connector.models.AttributeDTO;
import com.mindcurv.b2x.connector.models.Mixin;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",
    unmappedTargetPolicy = IGNORE,
    implementationName = "Default<CLASS_NAME>",
    implementationPackage = "<PACKAGE_NAME>.impl")
public interface CustomAggregationMixinConverter extends BaseConverter<Mixin, CustomAggregation> {

  @Override
  @Nullable
  @Mapping(source = "type", target = "type.key")
  @Mapping(constant = "type", target = "type.typeId")
  CustomAggregation convert(@Nullable Mixin source);

  @NotNull
  default Map<String, AttributeAggregation> processAttributes(
      @Nullable final Collection<AttributeDTO> attributes) {
    return emptyIfNull(attributes).stream()
        .collect(toMap(AttributeDTO::getKey, attribute -> new AttributeAggregation()
            .name(attribute.getKey())
            .value(attribute.getValue()), (a, b) -> b, LinkedHashMap::new));
  }
}
