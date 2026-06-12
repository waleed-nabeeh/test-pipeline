package com.mindcurv.b2x.productcatalog.review.security;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.springframework.http.HttpMethod.GET;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Spy;

@Tag("UnitTest")
final class ProductReviewApiSettingsTest {

  @Spy
  @InjectMocks
  private ProductReviewApiSettings settings;

  @BeforeEach
  void setUp() {
    openMocks(this);
  }

  @Test
  void getModuleName() {
    assertEquals("mc3s-b2x-productreview-models", settings.getModuleName(), "unexpected");
  }

  @Test
  void getUnsecuredEndpoints() {
    final var result = settings.getUnsecuredEndpoints();
    assertAll(
        () -> assertEquals(1, result.size(), "expected 1 entry"),
        () -> assertEquals(1, result.get(GET).size(), "expected 1 entries for GET"),
        () -> assertTrue(result.get(GET).contains("/aggregator/review/product/**"),
            "expected additional for GET"));
  }
}