package com.mindcurv.b2x.productcatalog.customizing;

import com.mindcurv.b2x.customizing.configuration.AbstractProductTypeConfiguration;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class ProductTypeConfiguration extends AbstractProductTypeConfiguration {

  @Override
  @NotNull
  public String getTypeKey() {
    return "undefined";
  }
}
