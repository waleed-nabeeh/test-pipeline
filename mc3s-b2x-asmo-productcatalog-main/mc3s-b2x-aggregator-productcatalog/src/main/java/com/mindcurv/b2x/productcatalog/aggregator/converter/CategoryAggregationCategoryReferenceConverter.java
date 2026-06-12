package com.mindcurv.b2x.productcatalog.aggregator.converter;

import static org.mapstruct.ReportingPolicy.IGNORE;

import com.commercetools.api.models.category.CategoryReference;
import com.mindcurv.b2x.commons.converter.BaseConverter;
import com.mindcurv.b2x.productcatalog.aggregator.models.CategoryAggregation;
import jakarta.validation.Valid;
import org.jetbrains.annotations.Nullable;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring",
    unmappedTargetPolicy = IGNORE,
    implementationName = "Commercetools<CLASS_NAME>",
    implementationPackage = "<PACKAGE_NAME>.impl")
public interface CategoryAggregationCategoryReferenceConverter
    extends BaseConverter<CategoryReference, CategoryAggregation> {

  @Nullable
  @Override
  CategoryAggregation convert(@Nullable @Valid CategoryReference source);
}
