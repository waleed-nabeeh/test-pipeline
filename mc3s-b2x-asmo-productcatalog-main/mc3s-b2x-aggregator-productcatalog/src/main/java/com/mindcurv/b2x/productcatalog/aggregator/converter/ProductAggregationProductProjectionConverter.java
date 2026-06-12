package com.mindcurv.b2x.productcatalog.aggregator.converter;

import static com.mindcurv.b2x.commons.models.ConverterType.DETAIL;
import static org.mapstruct.ReportingPolicy.IGNORE;

import com.commercetools.api.models.product.ProductProjection;
import com.mindcurv.b2x.commons.base.converter.ProductTypeAggregationProductTypeReferenceConverter;
import com.mindcurv.b2x.commons.base.converter.StateAggregationStateReferenceConverter;
import com.mindcurv.b2x.commons.base.converter.StringLocalizedStringConverter;
import com.mindcurv.b2x.commons.base.converter.TaxCategoryAggregationTaxCategoryReferenceConverter;
import com.mindcurv.b2x.commons.converter.BaseConverter;
import com.mindcurv.b2x.commons.models.B2xContext;
import com.mindcurv.b2x.commons.models.ConverterType;
import com.mindcurv.b2x.productcatalog.aggregator.models.ProductAggregation;
import com.mindcurv.b2x.productcatalog.aggregator.models.ProductVariantAggregation;
import jakarta.validation.Valid;
import java.util.Collection;
import java.util.LinkedList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring",
    unmappedTargetPolicy = IGNORE,
    implementationName = "Commercetools<CLASS_NAME>",
    implementationPackage = "<PACKAGE_NAME>.impl")
public abstract class ProductAggregationProductProjectionConverter
    implements BaseConverter<ProductProjection, ProductAggregation> {

  @Autowired
  @NotNull
  private CategoryNavigationAggregationCategoryReferenceConverter categoryNavigationAggregationCategoryReferenceConverter;
  @Autowired
  @NotNull
  private ProductVariantAggregationProductVariantConverter variantConverter;
  @Autowired
  @NotNull
  private MetaDataAggregationProductProjectionConverter metaDataConverter;
  @Autowired
  @NotNull
  private StateAggregationStateReferenceConverter stateAggregationStateReferenceConverter;
  @Autowired
  @NotNull
  private StringLocalizedStringConverter stringLocalizedStringConverter;
  @Autowired
  @NotNull
  private ProductTypeAggregationProductTypeReferenceConverter productTypeAggregationProductTypeReferenceConverter;
  @Autowired
  @NotNull
  private TaxCategoryAggregationTaxCategoryReferenceConverter taxCategoryAggregationTaxCategoryReferenceConverter;
  
  @Override
  @Nullable
  @Mapping(ignore = true, target = "name")
  @Mapping(ignore = true, target = "description")
  @Mapping(ignore = true, target = "categories")
  @Mapping(ignore = true, target = "variants")
  @Mapping(ignore = true, target = "type")
  public abstract ProductAggregation convert(@Nullable @Valid ProductProjection source);

  @Override
  @Nullable
  public ProductAggregation processContextMapping(
      @Nullable @Valid final ProductProjection source,
      @Nullable final ProductAggregation target,
      @NotNull final B2xContext context,
      @NotNull final ConverterType type) {
    if (source != null && target != null) {
      target
          .variants(processVariants(source, context, type).stream().toList())
          .name(stringLocalizedStringConverter.convert(source.getName(), context, type))
          .description(
              stringLocalizedStringConverter.convert(source.getDescription(), context, type))
          .categories(categoryNavigationAggregationCategoryReferenceConverter.convertList(
              source.getCategories(), context, type).stream().toList())
          .tax(taxCategoryAggregationTaxCategoryReferenceConverter.convert(source.getTaxCategory(),
              context, type));
      if (type == DETAIL) {
        target.metaData(metaDataConverter.convert(source, context, type))
            .type(productTypeAggregationProductTypeReferenceConverter.convert(
                source.getProductType(), context, type))
            .status(
                stateAggregationStateReferenceConverter.convert(source.getState(), context, type));
      }
    }
    return target;
  }

  @NotNull
  protected Collection<ProductVariantAggregation> processVariants(
      @Valid final ProductProjection source,
      @NotNull final B2xContext context,
      @NotNull final ConverterType type) {
    final var variants = new LinkedList<ProductVariantAggregation>();
    variantConverter.convertList(source.getAllVariants(), context, type)
        .forEach(variant -> variants.add(variant.defaultVariant(variants.isEmpty())));
    return variants;
  }

}
