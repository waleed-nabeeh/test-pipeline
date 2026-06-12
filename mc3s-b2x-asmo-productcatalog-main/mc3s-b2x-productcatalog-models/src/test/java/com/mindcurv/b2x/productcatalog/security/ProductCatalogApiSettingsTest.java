package com.mindcurv.b2x.productcatalog.security;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Spy;

@Tag("UnitTest")
final class ProductCatalogApiSettingsTest {

  @Spy
  @InjectMocks
  private ProductCatalogApiSettings settings;

  @BeforeEach
  void setUp() {
    openMocks(this);
  }

  @Test
  void getModuleName() {
    assertEquals("mc3s-b2x-productcatalog-models", settings.getModuleName(), "unexpected");
  }

  @Test
  void getUnsecuredEndpoints() {
    final var result = settings.getUnsecuredEndpoints();
    assertAll(
        () -> assertEquals(2, result.size(), "expected 2 entries"),
        () -> assertEquals(9, result.get(GET).size(), "expected 1 entries for GET"),
        () -> assertTrue(result.get(GET).containsAll(List.of("/aggregator/catalog",
                "/aggregator/category",
                "/aggregator/category/**",
                "/aggregator/product",
                "/aggregator/product/**",
                "/aggregator/product-store/**",
                "/aggregator/attribute-group/**",
                "/aggregator/listing",
                "/aggregator/listing/**")),
            "expected additional for GET"),
        () -> assertEquals(1, result.get(POST).size(), "expected 1 entry for POST"),
        () -> assertTrue(result.get(POST).contains("/aggregator/listing"),
            "expected additional for POST")

    );

  }
}