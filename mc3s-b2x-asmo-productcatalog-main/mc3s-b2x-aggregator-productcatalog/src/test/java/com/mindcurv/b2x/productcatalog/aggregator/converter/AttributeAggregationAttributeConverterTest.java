package com.mindcurv.b2x.productcatalog.aggregator.converter;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import com.commercetools.api.models.common.LocalizedString;
import com.commercetools.api.models.product.Attribute;
import com.mindcurv.b2x.productcatalog.aggregator.converter.impl.CommercetoolsAttributeAggregationAttributeConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

@Tag("UnitTest")
final class AttributeAggregationAttributeConverterTest {

  @Mock
  private Attribute attribute;

  private AttributeAggregationAttributeConverter converter;

  @BeforeEach
  void setUp() {
    openMocks(this);

    when(attribute.getValue()).thenReturn(LocalizedString.of());
    when(attribute.getName()).thenReturn("name");

    converter = new CommercetoolsAttributeAggregationAttributeConverter();
  }

  @Nested
  class convert {

    @Test
    void defaults() {
      final var result = converter.convert(attribute);
      assertNotNull(result, "expected value");
      assertAll(
          () -> assertEquals("name", result.getName(), "expected name"),
          () -> assertEquals(attribute.getValue(), result.getValue(), "expected value")
      );
    }

    @Test
    void nullValue() {
      final var result = converter.convert(null);
      assertNull(result, "expected null");
    }
  }
}