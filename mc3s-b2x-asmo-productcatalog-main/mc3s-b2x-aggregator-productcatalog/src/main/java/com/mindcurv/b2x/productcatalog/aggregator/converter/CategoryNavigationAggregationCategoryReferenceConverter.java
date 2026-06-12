package com.mindcurv.b2x.productcatalog.aggregator.converter;

import static org.mapstruct.ReportingPolicy.IGNORE;

import com.commercetools.api.models.category.Category;
import com.commercetools.api.models.category.CategoryReference;
import com.mindcurv.b2x.commons.converter.BaseConverter;
import com.mindcurv.b2x.commons.models.B2xContext;
import com.mindcurv.b2x.commons.models.ConverterType;
import com.mindcurv.b2x.commons.resolver.NullableBaseResolver;
import com.mindcurv.b2x.productcatalog.aggregator.models.CategoryNavigationAggregation;
import jakarta.validation.Valid;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring",
    unmappedTargetPolicy = IGNORE,
    implementationName = "Commercetools<CLASS_NAME>",
    implementationPackage = "<PACKAGE_NAME>.impl")
public abstract class CategoryNavigationAggregationCategoryReferenceConverter
    implements BaseConverter<CategoryReference, CategoryNavigationAggregation> {

  @Autowired
  @NotNull
  @Valid
  private NullableBaseResolver<Category> categoryResolver;
  @Autowired
  @NotNull
  private CategoryNavigationAggregationCategoryConverter categoryNavigationAggregationCategoryConverter;

  @Override
  @Nullable
  public abstract CategoryNavigationAggregation convert(@Nullable @Valid CategoryReference source);

  @Override
  @Nullable
  public CategoryNavigationAggregation processContextMapping(
      @Nullable @Valid final CategoryReference source,
      @Nullable CategoryNavigationAggregation target,
      @NotNull final B2xContext context,
      @NotNull final ConverterType type) {
    return resolve(source).map(category ->
        categoryNavigationAggregationCategoryConverter.convertAsOptional(
            category, context, type).orElse(target)).orElse(target);
  }

  @NotNull
  protected Optional<Category> resolve(@Nullable @Valid final CategoryReference source) {
    if (source == null) {
      return Optional.empty();
    }
    return Optional.ofNullable(source.getObj()).or(() -> categoryResolver.resolve(source.getId()));
  }
}
