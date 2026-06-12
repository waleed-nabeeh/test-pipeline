package com.mindcurv.b2x.productcatalog.aggregator.helper;

import static com.mindcurv.b2x.commons.commercetools.helper.ProductAttributeHelper.getProductAttribute;
import static com.mindcurv.b2x.productcatalog.aggregator.helper.CategoryAggregationHelper.DEFAULT_TEMPLATE;
import static lombok.AccessLevel.PRIVATE;

import com.commercetools.api.models.product.ProductDataLike;
import com.mindcurv.b2x.customizing.configuration.AbstractProductTypeConfiguration;
import jakarta.validation.Valid;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@NoArgsConstructor(access = PRIVATE)
public final class ProductAggregationHelper {

  @NotNull
  public static String getRenderingTemplate(
      @NotNull @Valid final ProductDataLike productDataLike,
      @NotNull final AbstractProductTypeConfiguration configuration) {
    final var attName = configuration.getRenderingTemplate();
    if (attName.isPresent()) {
      final var value = getProductAttribute(productDataLike, attName.get());
      if (value.isPresent() && value.get().getValue() instanceof String renderingTemplate) {
        return renderingTemplate;
      }
    }
    return DEFAULT_TEMPLATE;
  }
}
