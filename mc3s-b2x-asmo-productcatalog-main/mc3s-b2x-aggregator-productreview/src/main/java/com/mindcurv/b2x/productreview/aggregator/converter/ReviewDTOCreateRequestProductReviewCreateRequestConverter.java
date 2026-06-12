package com.mindcurv.b2x.productreview.aggregator.converter;

import static com.mindcurv.b2x.commons.helper.SecureRandomHelper.generateRandomString;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.mapstruct.ReportingPolicy.IGNORE;

import com.mindcurv.b2x.commons.converter.BaseConverter;
import com.mindcurv.b2x.commons.models.B2xContext;
import com.mindcurv.b2x.commons.models.ConverterType;
import com.mindcurv.b2x.commons.resolver.NullableBaseResolver;
import com.mindcurv.b2x.connector.customer.models.CustomerDTO;
import com.mindcurv.b2x.connector.review.models.ReviewDTOCreateRequest;
import com.mindcurv.b2x.customizing.configuration.impl.ReviewConfiguration;
import com.mindcurv.b2x.productcatalog.dto.converter.CustomAggregationMixinConverter;
import com.mindcurv.b2x.productcatalog.review.models.ProductReviewCreateRequest;
import jakarta.validation.Valid;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring",
    unmappedTargetPolicy = IGNORE,
    implementationName = "Dto<CLASS_NAME>",
    implementationPackage = "<PACKAGE_NAME>.impl",
    uses = {
        CustomAggregationMixinConverter.class
    })
public abstract class ReviewDTOCreateRequestProductReviewCreateRequestConverter implements
    BaseConverter<ProductReviewCreateRequest, ReviewDTOCreateRequest> {

  @Autowired
  @NotNull
  private NullableBaseResolver<CustomerDTO> customerDTOResolver;

  @Autowired
  @NotNull
  private ReviewConfiguration reviewConfiguration;

  @Override
  @Nullable
  @Mapping(source = "review", target = "text")
  @Mapping(source = "key", target = "target.key")
  @Mapping(constant = "product", target = "target.type")
  public abstract ReviewDTOCreateRequest convert(@Nullable ProductReviewCreateRequest source);

  @AfterMapping
  protected ReviewDTOCreateRequest afterMapping(
      @NotNull final ProductReviewCreateRequest source,
      @NotNull @MappingTarget final ReviewDTOCreateRequest target) {
    return target.key(generateRandomString())
        .state(reviewConfiguration.getInitialStateKey());
  }

  @Override
  @Nullable
  public ReviewDTOCreateRequest processContextMapping(
      @Nullable @Valid final ProductReviewCreateRequest source,
      @Nullable final ReviewDTOCreateRequest target,
      @NotNull final B2xContext context,
      @NotNull final ConverterType converterType) {
    if (target != null) {
      target.user(context.getCustomerId()).locale(context.getCurrentLanguage());
      customerDTOResolver.resolve(context.getCustomerId()).ifPresent(customerDTO -> target
          .author(format("%s %s", Optional.ofNullable(customerDTO.getFirstName()).orElse(EMPTY),
              Optional.ofNullable(customerDTO.getLastName()).orElse(EMPTY)).trim())
          .user(customerDTO.getKey()));
    }
    return target;
  }
}
