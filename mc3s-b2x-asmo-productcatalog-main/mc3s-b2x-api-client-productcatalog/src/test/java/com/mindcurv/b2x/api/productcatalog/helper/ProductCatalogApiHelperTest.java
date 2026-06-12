package com.mindcurv.b2x.api.productcatalog.helper;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import com.mindcurv.b2x.api.productcatalog.invoker.ApiClient;
import com.mindcurv.b2x.commons.models.URLMetaData;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

@Tag("UnitTest")
final class ProductCatalogApiHelperTest {

  @Nested
  class initApiClientWithBasePath {

    @Test
    void defaults() {
      final var client = ProductCatalogApiHelper.initApiClientWithBasePath(new ApiClient(),
          "basePath");
      assertAll(() -> assertEquals("basePath", client.getBasePath(), "Base path should be set"));
    }

    @Test
    void noBasePath() {
      final var client = ProductCatalogApiHelper.initApiClientWithBasePath(new ApiClient(), null);
      assertAll(() -> assertNotEquals("base", client.getBasePath(), "unexpected"));
    }
  }

  @Nested
  class initClientByToken {

    @Mock
    private URLMetaData urlMetaData;

    @BeforeEach
    void setUp() {
      openMocks(this);

      when(urlMetaData.getHosts()).thenReturn(Map.of("productcatalog", "http://whatever.com"));
    }

    @Test
    void defaults() {
      final var client = ProductCatalogApiHelper.initClientByToken(new ApiClient(), "token",
          urlMetaData);
      assertAll(
          () -> assertEquals("http://whatever.com", client.getBasePath(), "unexpected base path")
      );
    }

    @Test
    void noBasePath() {
      when(urlMetaData.getHosts()).thenReturn(null);
      final var client = ProductCatalogApiHelper.initClientByToken(new ApiClient(), "token",
          urlMetaData);
      assertAll(
          () -> assertNotEquals("http://whatever.com", client.getBasePath(), "unexpected base path")
      );
    }

    @Test
    void nullToken() {
      final var client = ProductCatalogApiHelper.initClientByToken(new ApiClient(), null,
          urlMetaData);
      assertNotNull(client, "unexpected");
    }
  }
}
