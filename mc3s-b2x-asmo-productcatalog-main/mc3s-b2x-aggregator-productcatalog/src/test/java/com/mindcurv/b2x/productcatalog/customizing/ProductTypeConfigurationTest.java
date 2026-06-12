package com.mindcurv.b2x.productcatalog.customizing;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("UnitTest")
final class ProductTypeConfigurationTest {
  private ProductTypeConfiguration configuration;

  @BeforeEach
  void setUp() {
    configuration = new ProductTypeConfiguration();
  }

  @Test
  void getTypeKey() {
    assertEquals("undefined", configuration.getTypeKey(), "unexpected key");
  }
}