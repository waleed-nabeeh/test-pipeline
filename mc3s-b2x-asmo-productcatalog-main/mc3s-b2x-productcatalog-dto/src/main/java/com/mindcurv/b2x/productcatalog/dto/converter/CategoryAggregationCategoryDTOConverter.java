package com.mindcurv.b2x.productcatalog.dto.converter;

import static org.mapstruct.ReportingPolicy.IGNORE;

import com.mindcurv.b2x.commons.converter.BaseConverter;
import com.mindcurv.b2x.connector.category.models.CategoryDTO;
import com.mindcurv.b2x.connector.converter.StringLocalizedTextDTOConverter;
import com.mindcurv.b2x.connector.resolver.DTOBaseResolver;
import com.mindcurv.b2x.productcatalog.aggregator.models.CategoryAggregation;
import jakarta.validation.Valid;
import org.jetbrains.annotations.Nullable;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring",
    unmappedTargetPolicy = IGNORE,
    implementationName = "Default<CLASS_NAME>",
    implementationPackage = "<PACKAGE_NAME>.impl",
    uses = {
        CustomAggregationMixinConverter.class,
        StringLocalizedTextDTOConverter.class
    })
public abstract class CategoryAggregationCategoryDTOConverter implements
    BaseConverter<CategoryDTO, CategoryAggregation> {

  @Autowired
  private DTOBaseResolver<CategoryDTO> categoryDTOResolver;

  @Override
  @Nullable
  @Mapping(source = "seo.slug", target = "slug")
  @Mapping(source = "mixin", target = "custom")
  public abstract CategoryAggregation convert(@Nullable @Valid CategoryDTO source);

  @Nullable
  protected CategoryAggregation getParent(@Nullable final String identifier) {
    return categoryDTOResolver.resolve(identifier).flatMap(this::convertAsOptional).orElse(null);
  }
}
