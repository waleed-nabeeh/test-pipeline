package com.mindcurv.b2x.productcatalog.dto.converter;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.MockitoAnnotations.openMocks;

import com.mindcurv.b2x.connector.models.AttributeDTO;
import com.mindcurv.b2x.productcatalog.dto.converter.impl.DefaultCustomAggregationMixinConverter;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("UnitTest")
final class CustomAggregationMixinConverterTest {

  private AttributeDTO attributeDTO;

  private CustomAggregationMixinConverter converter;

  @BeforeEach
  void setUp() {
    openMocks(this);

    attributeDTO = new AttributeDTO("key", "value");
    converter = new DefaultCustomAggregationMixinConverter();
  }

  @Nested
  class processAttributes {

    @Test
    void defaults() {
      final var result = converter.processAttributes(List.of(attributeDTO));
      final var entry = result.entrySet().stream().findFirst().orElse(null);
      assertNotNull(entry, "unexpected entry");
      assertAll(
          () -> assertEquals("key", entry.getKey(), "unexpected key"),
          () -> assertNotNull(entry.getValue(), "unexpected value")
      );
    }

    @Test
    void nullSource() {
      final var result = converter.processAttributes(null);
      assertEquals(Map.of(), result, "unexpected");
    }
  }
}