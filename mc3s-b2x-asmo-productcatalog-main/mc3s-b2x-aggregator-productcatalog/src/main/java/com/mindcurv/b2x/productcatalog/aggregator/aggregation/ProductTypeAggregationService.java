package com.mindcurv.b2x.productcatalog.aggregator.aggregation;

import com.commercetools.api.models.product_type.ProductType;
import com.commercetools.api.models.product_type.ProductTypeReference;
import com.mindcurv.b2x.commons.models.AttributeMetaData;
import com.mindcurv.b2x.commons.models.B2xContext;
import com.mindcurv.b2x.commons.models.Pageable;
import com.mindcurv.b2x.commons.models.ProductTypeAggregation;
import jakarta.validation.Valid;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ProductTypeAggregationService {

  @NotNull
  Pageable<ProductTypeAggregation> getProductTypes(@NotNull B2xContext context);

  @NotNull
  Optional<ProductTypeAggregation> getProductTypeById(
      @NotNull String id, @NotNull B2xContext context);

  @NotNull
  Optional<ProductTypeAggregation> getProductTypeByKey(
      @NotNull String key, @NotNull B2xContext context);

  @NotNull
  Collection<AttributeMetaData> getProductAttributeMetaDataById(@NotNull String id);

  @NotNull
  Collection<AttributeMetaData> getProductAttributeMetaDataByKey(@NotNull String key);

  @NotNull
  default Collection<AttributeMetaData> getProductAttributeMetaData(
      @Nullable @Valid final ProductType productType) {
    return Optional.ofNullable(productType).map(
            type -> getProductAttributeMetaDataById(type.getId()))
        .orElseGet(List::of);
  }

  @NotNull
  default Collection<AttributeMetaData> getProductAttributeMetaData(
      @Nullable @Valid final ProductTypeReference productTypeReference) {
    return Optional.ofNullable(productTypeReference).map(
            reference -> getProductAttributeMetaDataById(reference.getId()))
        .orElseGet(List::of);
  }
}
