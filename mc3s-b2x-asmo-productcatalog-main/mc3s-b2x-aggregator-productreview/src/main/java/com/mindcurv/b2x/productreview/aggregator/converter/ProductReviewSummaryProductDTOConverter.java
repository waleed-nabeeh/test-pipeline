package com.mindcurv.b2x.productreview.aggregator.converter;

import static org.mapstruct.ReportingPolicy.IGNORE;

import com.mindcurv.b2x.commons.converter.BaseConverter;
import com.mindcurv.b2x.connector.product.models.ProductDTO;
import com.mindcurv.b2x.productcatalog.review.models.ProductReviewSummary;
import org.jetbrains.annotations.Nullable;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",
    unmappedTargetPolicy = IGNORE,
    implementationName = "Dto<CLASS_NAME>",
    implementationPackage = "<PACKAGE_NAME>.impl")
public interface ProductReviewSummaryProductDTOConverter extends
    BaseConverter<ProductDTO, ProductReviewSummary> {

  @Override
  @Nullable
  @Mapping(source = "id", target = "product.id")
  @Mapping(constant = "product", target = "product.typeId")
  @Mapping(source = "reviewStatistics.average", target = "averageRating")
  @Mapping(source = "reviewStatistics.count", target = "count")
  @Mapping(source = "reviewStatistics.max", target = "maxRating")
  @Mapping(source = "reviewStatistics.min", target = "minRating")
  ProductReviewSummary convert(@Nullable ProductDTO source);
}
