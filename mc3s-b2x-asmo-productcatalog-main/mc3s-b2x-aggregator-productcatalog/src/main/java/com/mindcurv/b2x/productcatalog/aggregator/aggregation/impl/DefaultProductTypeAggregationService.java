package com.mindcurv.b2x.productcatalog.aggregator.aggregation.impl;

import static com.mindcurv.b2x.commons.models.ConverterType.DETAIL;

import com.commercetools.api.models.product_type.ProductType;
import com.mindcurv.b2x.commons.base.converter.ProductTypeAggregationProductTypeConverter;
import com.mindcurv.b2x.commons.commercetools.services.impl.ProductTypeCommercetoolsService;
import com.mindcurv.b2x.commons.models.AttributeMetaData;
import com.mindcurv.b2x.commons.models.B2xContext;
import com.mindcurv.b2x.commons.models.Pageable;
import com.mindcurv.b2x.commons.models.ProductTypeAggregation;
import com.mindcurv.b2x.commons.resolver.BaseResolver;
import com.mindcurv.b2x.commons.resolver.NullableBaseResolver;
import com.mindcurv.b2x.productcatalog.aggregator.aggregation.ProductTypeAggregationService;
import jakarta.validation.Valid;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DefaultProductTypeAggregationService implements ProductTypeAggregationService {

  @NotNull
  private final ProductTypeCommercetoolsService productTypeCommercetoolsService;
  @NotNull
  private final BaseResolver<List<AttributeMetaData>> metaDataResolver;
  @NotNull
  private final NullableBaseResolver<ProductType> productTypeResolver;
  @NotNull
  @Valid
  private final ProductTypeAggregationProductTypeConverter productTypeAggregationProductTypeConverter;

  @Autowired
  public DefaultProductTypeAggregationService(
      @NotNull final ProductTypeCommercetoolsService productTypeCommercetoolsService,
      @NotNull final BaseResolver<List<AttributeMetaData>> metaDataResolver,
      @NotNull final NullableBaseResolver<ProductType> productTypeResolver,
      @NotNull final ProductTypeAggregationProductTypeConverter productTypeAggregationProductTypeConverter) {
    this.productTypeCommercetoolsService = productTypeCommercetoolsService;
    this.metaDataResolver = metaDataResolver;
    this.productTypeResolver = productTypeResolver;
    this.productTypeAggregationProductTypeConverter = productTypeAggregationProductTypeConverter;
  }

  @Override
  @NotNull
  public Pageable<ProductTypeAggregation> getProductTypes(@NotNull final B2xContext context) {
    return productTypeAggregationProductTypeConverter.convertPageable(
        productTypeCommercetoolsService.getAllItems(), context);
  }

  @Override
  @NotNull
  public Optional<ProductTypeAggregation> getProductTypeById(
      @NotNull final String id, @NotNull final B2xContext context) {
    return productTypeResolver.resolve(id).flatMap(productType ->
        productTypeAggregationProductTypeConverter.convertAsOptional(productType, context, DETAIL));
  }

  @Override
  @NotNull
  public Optional<ProductTypeAggregation> getProductTypeByKey(
      @NotNull final String key, @NotNull final B2xContext context) {
    return productTypeResolver.resolve(key).flatMap(productType ->
        productTypeAggregationProductTypeConverter.convertAsOptional(productType, context, DETAIL));
  }

  @Override
  @NotNull
  public Collection<AttributeMetaData> getProductAttributeMetaDataByKey(@NotNull final String key) {
    return metaDataResolver.resolve(key);
  }

  @Override
  @NotNull
  public Collection<AttributeMetaData> getProductAttributeMetaDataById(@NotNull final String id) {
    return metaDataResolver.resolve(id);
  }

}
