package com.mindcurv.b2x.productcatalog.security;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

import com.mindcurv.b2x.commons.models.B2xApiSettings;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

@Component
public class ProductCatalogApiSettings implements B2xApiSettings {

  @Override
  @NotNull
  public Map<HttpMethod, List<String>> getUnsecuredEndpoints() {
    return Map.of(
        POST, List.of("/aggregator/listing"),
        GET, List.of("/aggregator/catalog",
            "/aggregator/category",
            "/aggregator/category/**",
            "/aggregator/product",
            "/aggregator/product/**",
            "/aggregator/product-store/**",
            "/aggregator/listing",
            "/aggregator/listing/**",
            "/aggregator/attribute-group/**")
    );
  }

  @Override
  @NotNull
  public String getModuleName() {
    return "mc3s-b2x-productcatalog-models";
  }

}
