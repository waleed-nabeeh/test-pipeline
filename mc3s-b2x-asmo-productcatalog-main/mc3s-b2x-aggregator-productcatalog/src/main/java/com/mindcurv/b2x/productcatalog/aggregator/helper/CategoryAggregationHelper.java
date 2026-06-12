package com.mindcurv.b2x.productcatalog.aggregator.helper;

import static com.mindcurv.b2x.customizing.helper.CustomFieldsHelper.getFieldAsString;
import static lombok.AccessLevel.PRIVATE;

import com.commercetools.api.models.category.Category;
import com.mindcurv.b2x.customizing.configuration.AbstractCategoryTypeConfiguration;
import jakarta.validation.Valid;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@NoArgsConstructor(access = PRIVATE)
public final class CategoryAggregationHelper {

  public static final String DEFAULT_TEMPLATE = "default";

  @NotNull
  public static String getRenderingTemplate(
      @NotNull @Valid final Category category,
      @NotNull final AbstractCategoryTypeConfiguration configuration) {
    return configuration.getRenderingTemplate()
        .map(attName -> getFieldAsString(category.getCustom(), attName).orElse(DEFAULT_TEMPLATE))
        .orElse(DEFAULT_TEMPLATE);
  }
}
