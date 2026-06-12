package com.mindcurv.b2x.productcatalog.review.security;

import static org.springframework.http.HttpMethod.GET;

import com.mindcurv.b2x.commons.models.B2xApiSettings;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

@Component
public class ProductReviewApiSettings implements B2xApiSettings {

  @Override
  @NotNull
  public Map<HttpMethod, List<String>> getUnsecuredEndpoints() {
    return Map.of(GET, List.of("/aggregator/review/product/**")
    );
  }

  @Override
  @NotNull
  public String getModuleName() {
    return "mc3s-b2x-productreview-models";
  }

}
