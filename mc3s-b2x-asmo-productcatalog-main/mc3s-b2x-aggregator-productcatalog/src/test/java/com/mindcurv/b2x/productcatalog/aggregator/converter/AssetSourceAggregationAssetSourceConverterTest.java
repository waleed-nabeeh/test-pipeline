package com.mindcurv.b2x.productcatalog.aggregator.converter;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import com.commercetools.api.models.common.AssetDimensions;
import com.commercetools.api.models.common.AssetSource;
import com.mindcurv.b2x.commons.models.URLMetaData;
import com.mindcurv.b2x.productcatalog.aggregator.converter.impl.CommercetoolsAssetSourceAggregationAssetSourceConverter;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

@Tag("UnitTest")
final class AssetSourceAggregationAssetSourceConverterTest {

  @Mock
  private URLMetaData urlMetaData;

  @Mock
  private AssetSource assetSource;

  private AssetSourceAggregationAssetSourceConverter converter;

  @BeforeEach
  void setUp() {
    openMocks(this);

    when(urlMetaData.getPatterns()).thenReturn(Map.of("assets", "http://localhost/"));

    when(assetSource.getKey()).thenReturn("assetSourceKey");
    when(assetSource.getDimensions()).thenReturn(AssetDimensions.builder().w(1).h(2).build());
    when(assetSource.getContentType()).thenReturn("assetSourceContentType");
    when(assetSource.getUri()).thenReturn("assetSourceUri");

    converter = new CommercetoolsAssetSourceAggregationAssetSourceConverter();
    setField(converter, "urlMetaData", urlMetaData);
  }

  @Nested
  class convert {

    @Test
    void defaults() {

      final var aggregation = converter.convert(assetSource);
      assertNotNull(aggregation, "unexpected");
      assertAll(
          () -> assertEquals("assetSourceKey", aggregation.getKey(), "unexpected"),
          () -> assertEquals(1, aggregation.getWidth(), "unexpected"),
          () -> assertEquals(2, aggregation.getHeight(), "unexpected"),
          () -> assertEquals("assetSourceContentType", aggregation.getContentType(), "unexpected"),
          () -> assertEquals("http://localhost/assetSourceUri", aggregation.getUri(),
              "unexpected"));
    }

    @Test
    void nullValue() {
      assertNull(converter.convert(null), "unexpected");
    }
  }
}
