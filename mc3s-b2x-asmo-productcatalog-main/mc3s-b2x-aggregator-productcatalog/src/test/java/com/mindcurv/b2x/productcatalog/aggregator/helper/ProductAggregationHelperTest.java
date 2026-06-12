package com.mindcurv.b2x.productcatalog.aggregator.helper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import com.commercetools.api.models.product.Attribute;
import com.commercetools.api.models.product.ProductDataLike;
import com.mindcurv.b2x.customizing.configuration.AbstractProductTypeConfiguration;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

@Tag("UnitTest")
final class ProductAggregationHelperTest {

  @Mock
  private ProductDataLike productDataLike;
  @Mock
  private Attribute attribute;
  @Mock
  private AbstractProductTypeConfiguration configuration;

  @BeforeEach
  void setUp() {
    openMocks(this);
    when(attribute.getName()).thenReturn("renderingTemplate");
    when(attribute.getValue()).thenReturn("custom-template");
    when(configuration.getRenderingTemplate()).thenReturn(Optional.of("renderingTemplate"));

    when(productDataLike.getAttributes()).thenReturn(List.of(attribute));

  }

  @Nested
  class getRenderingTemplate {

    @Test
    void defaults() {
      final var result = ProductAggregationHelper.getRenderingTemplate(productDataLike,
          configuration);
      assertEquals("custom-template", result, "expected custom template");
    }

    @Test
    void defaultsWithoutConfigurationName() {
      when(configuration.getRenderingTemplate()).thenReturn(Optional.empty());
      final var result = ProductAggregationHelper.getRenderingTemplate(productDataLike,
          configuration);
      assertEquals("default", result, "expected default template");
    }

    @Test
    void attributeValueNotString() {

      when(attribute.getValue()).thenReturn(123);
      final var result = ProductAggregationHelper.getRenderingTemplate(productDataLike,
          configuration);

      assertEquals("default", result, "expected default template when value is not string");
    }
  }
}

