package com.mindcurv.b2x.productcatalog.all.helper;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("UnitTest")
final class ProductCatalogAllHelperTest {

  @Test
  void log() {
    assertDoesNotThrow(ProductCatalogAllHelper::logTest);
  }
}