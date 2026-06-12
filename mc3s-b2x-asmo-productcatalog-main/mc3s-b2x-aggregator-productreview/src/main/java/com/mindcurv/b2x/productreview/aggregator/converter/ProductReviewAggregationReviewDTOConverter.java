package com.mindcurv.b2x.productreview.aggregator.converter;

import static org.mapstruct.ReportingPolicy.IGNORE;

import com.mindcurv.b2x.commons.converter.BaseConverter;
import com.mindcurv.b2x.commons.models.B2xContext;
import com.mindcurv.b2x.commons.models.ConverterType;
import com.mindcurv.b2x.commons.models.StateAggregation;
import com.mindcurv.b2x.connector.resolver.DTOBaseResolver;
import com.mindcurv.b2x.connector.review.models.ReviewDTO;
import com.mindcurv.b2x.connector.state.models.StateDTO;
import com.mindcurv.b2x.productcatalog.dto.converter.CustomAggregationMixinConverter;
import com.mindcurv.b2x.productcatalog.review.models.ProductReviewAggregation;
import jakarta.validation.Valid;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring",
    unmappedTargetPolicy = IGNORE,
    implementationName = "Dto<CLASS_NAME>",
    implementationPackage = "<PACKAGE_NAME>.impl",
    uses = {
        CustomAggregationMixinConverter.class
    })
public abstract class ProductReviewAggregationReviewDTOConverter implements
    BaseConverter<ReviewDTO, ProductReviewAggregation> {

  private static final Logger LOG = LoggerFactory.getLogger(
      ProductReviewAggregationReviewDTOConverter.class);

  @Autowired
  @NotNull
  private DTOBaseResolver<StateDTO> stateResolver;

  @Override
  @Nullable
  @Mapping(source = "text", target = "review")
  @Mapping(source = "user", target = "author.customer.key")
  @Mapping(source = "author", target = "author.name")
  @Mapping(source = "target.id", target = "product.id")
  @Mapping(source = "target.key", target = "product.key")
  @Mapping(constant = "product", target = "product.typeId")
  public abstract ProductReviewAggregation convert(@Nullable ReviewDTO source);

  @Override
  @Nullable
  public ProductReviewAggregation processContextMapping(@Nullable final ReviewDTO source,
      @Nullable final ProductReviewAggregation target,
      @NotNull final B2xContext context,
      @NotNull final ConverterType converterType) {
    if (source != null && target != null) {
      LOG.debug("processContextMapping ::");
    }
    return target;
  }

  @Nullable
  protected StateAggregation map(@Nullable @Valid final String state) {
    return stateResolver.resolve(state).map(item ->
        new StateAggregation().id(item.getId()).key(item.getKey())).orElse(null);
  }

}
