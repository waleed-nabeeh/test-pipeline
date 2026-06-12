package com.mindcurv.b2x.productcatalog.aggregator.converter;

import static com.mindcurv.b2x.commons.models.ConverterType.DETAIL;
import static com.mindcurv.b2x.customizing.helper.CustomFieldsHelper.getFieldAsString;
import static com.mindcurv.b2x.productcatalog.aggregator.helper.CategoryAggregationHelper.getRenderingTemplate;
import static org.mapstruct.MappingConstants.ComponentModel.DEFAULT;
import static org.mapstruct.ReportingPolicy.IGNORE;

import com.commercetools.api.models.category.Category;
import com.mindcurv.b2x.commons.base.converter.StringLocalizedStringConverter;
import com.mindcurv.b2x.commons.converter.BaseConverter;
import com.mindcurv.b2x.commons.models.B2xContext;
import com.mindcurv.b2x.commons.models.ConverterType;
import com.mindcurv.b2x.customizing.configuration.impl.CatalogCategoryConfiguration;
import com.mindcurv.b2x.productcatalog.aggregator.models.MetaDataAggregation;
import jakarta.validation.Valid;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring",
    unmappedTargetPolicy = IGNORE,
    implementationName = "Commercetools<CLASS_NAME>",
    implementationPackage = "<PACKAGE_NAME>.impl")
public abstract class MetaDataAggregationCategoryConverter
    implements BaseConverter<Category, MetaDataAggregation> {

  @Autowired
  @NotNull
  private StringLocalizedStringConverter stringLocalizedStringConverter;
  @Autowired
  @NotNull
  private CatalogCategoryConfiguration catalogCategoryConfiguration;

  @Override
  @Nullable
  @Mapping(ignore = true, target = "slug")
  @Mapping(ignore = true, target = "description")
  public abstract MetaDataAggregation convert(@Nullable @Valid Category source);

  @AfterMapping
  protected MetaDataAggregation afterMapping(@NotNull @Valid final Category source,
      @NotNull @MappingTarget final MetaDataAggregation target) {
    return target.renderingTemplate(
        getFieldAsString(source.getCustom(), "renderingTemplate").orElse(DEFAULT));
  }

  @Override
  @Nullable
  public MetaDataAggregation processContextMapping(@Nullable @Valid final Category source,
      @Nullable final MetaDataAggregation target,
      @NotNull final B2xContext context,
      @NotNull final ConverterType converterType) {
    if (source == null || target == null) {
      return target;
    }
    target.slug(stringLocalizedStringConverter.convert(source.getSlug(), context));
    if (converterType == DETAIL) {
      target
          .renderingTemplate(getRenderingTemplate(source, catalogCategoryConfiguration))
          .title(stringLocalizedStringConverter.convert(source.getMetaTitle(), context))
          .description(stringLocalizedStringConverter.convert(source.getMetaDescription(), context))
          .keywords(stringLocalizedStringConverter.convert(source.getMetaKeywords(), context));
    }
    return target;
  }
}
