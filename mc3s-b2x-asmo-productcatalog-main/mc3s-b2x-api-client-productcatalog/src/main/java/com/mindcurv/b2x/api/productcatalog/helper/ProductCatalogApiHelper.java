package com.mindcurv.b2x.api.productcatalog.helper;

import static lombok.AccessLevel.PRIVATE;
import static org.apache.commons.collections4.MapUtils.emptyIfNull;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import com.mindcurv.b2x.api.productcatalog.invoker.ApiClient;
import com.mindcurv.b2x.commons.models.URLMetaData;
import java.util.Optional;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@NoArgsConstructor(access = PRIVATE)
public class ProductCatalogApiHelper {

  private static final Logger LOG = LoggerFactory.getLogger(ProductCatalogApiHelper.class);

  private static final String BEARER_PREFIX = "Bearer ";
  public static final String SYSTEM_ID = "productcatalog";

  @NotNull
  public static ApiClient initApiClientWithBasePath(@NotNull final ApiClient client,
      @Nullable final String basePath) {
    Optional.ofNullable(basePath).ifPresent(client::setBasePath);
    return client;
  }

  @NotNull
  public static ApiClient initClientByToken(@NotNull final ApiClient client,
      @Nullable final String bearerToken, @NotNull final URLMetaData urlMetaData) {
    initApiClientWithBasePath(client, emptyIfNull(urlMetaData.getHosts()).get(SYSTEM_ID));
    final var apiBearerToken = Optional.ofNullable(bearerToken);
    if (apiBearerToken.isPresent()) {
      LOG.info("initClientByToken :: setting bearer token to api client");
      client.addDefaultHeader(AUTHORIZATION, BEARER_PREFIX + apiBearerToken.get());
    } else {
      LOG.warn("initClientByToken :: no credentials provided");
    }
    return client;
  }

}
