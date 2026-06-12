package com.mindcurv.b2x.productcatalog.aggregator.converter;

import static com.mindcurv.b2x.commons.models.ConverterType.DETAIL;
import static com.mindcurv.b2x.commons.models.ConverterType.LIST;
import static com.mindcurv.b2x.productcatalog.aggregator.helper.ConverterSetupHelper.initB2xContext;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import com.commercetools.api.models.common.LocalizedString;
import com.commercetools.api.models.product.Attribute;
import com.commercetools.api.models.product.AttributeAccess;
import com.commercetools.api.models.product.ProductProjection;
import com.commercetools.api.models.product.ProductVariant;
import com.mindcurv.b2x.commons.base.converter.StringLocalizedStringConverter;
import com.mindcurv.b2x.commons.models.B2xContext;
import com.mindcurv.b2x.customizing.configuration.impl.BaseProductConfiguration;
import com.mindcurv.b2x.productcatalog.aggregator.converter.impl.CommercetoolsMetaDataAggregationProductProjectionConverter;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

@Tag("UnitTest")
final class MetaDataAggregationProductProjectionConverterTest {

  @Mock
  private StringLocalizedStringConverter stringLocalizedStringConverter;
  @Mock
  private BaseProductConfiguration baseProductConfiguration;
  @Mock
  private ProductProjection productProjection;

  private B2xContext context;
  private MetaDataAggregationProductProjectionConverter converter;

  @BeforeEach
  void setUp() {
    openMocks(this);
    context = initB2xContext();
    when(productProjection.getSlug()).thenReturn(LocalizedString.of());
    when(productProjection.getMetaDescription()).thenReturn(LocalizedString.of());
    when(productProjection.getMetaKeywords()).thenReturn(LocalizedString.of());
    when(productProjection.getMetaTitle()).thenReturn(LocalizedString.of());
    final var master = mock(ProductVariant.class);
    final var attribute = mock(Attribute.class);
    when(attribute.getName()).thenReturn("renderingTemplate");
    when(attribute.getValue()).thenReturn("renderingTemplateValue");
    when(productProjection.getAttributes()).thenReturn(List.of(attribute));
    when(master.getAttributeByName(notNull())).thenReturn(AttributeAccess.of(attribute));
    when(productProjection.getMasterVariant()).thenReturn(master);

    when(stringLocalizedStringConverter.convert(notNull(), any())).thenReturn("localizedValue");

    when(baseProductConfiguration.getRenderingTemplate()).thenReturn(
        Optional.of("renderingTemplate"));

    converter = new CommercetoolsMetaDataAggregationProductProjectionConverter();
    setField(converter, "stringLocalizedStringConverter", stringLocalizedStringConverter);
    setField(converter, "baseProductConfiguration", baseProductConfiguration);
  }

  @Nested
  class convert {

    @Test
    void defaults() {
      final var aggregation = converter.convert(productProjection, context, LIST);
      assertNotNull(aggregation, "unexpected");
      assertAll(
          () -> assertEquals("renderingTemplateValue", aggregation.getRenderingTemplate(),
              "unexpected template"),
          () -> assertNotNull(aggregation.getSlug(), "unexpected slug"),
          () -> assertNull(aggregation.getDescription(), "unexpected description"),
          () -> assertNull(aggregation.getKeywords(), "unexpected keywords"),
          () -> assertNull(aggregation.getTitle(), "unexpected title"));
    }

    @Test
    void detail() {
      final var aggregation = converter.convert(productProjection, context, DETAIL);
      assertNotNull(aggregation, "unexpected");
      assertAll(
          () -> assertEquals("renderingTemplateValue", aggregation.getRenderingTemplate(),
              "unexpected template"),
          () -> assertNotNull(aggregation.getSlug(), "unexpected slug"),
          () -> assertNotNull(aggregation.getDescription(), "unexpected description"),
          () -> assertNotNull(aggregation.getKeywords(), "unexpected keywords"),
          () -> assertNotNull(aggregation.getTitle(), "unexpected title"));
    }

    @Test
    void nullValue() {
      assertNull(converter.convert(null, context, DETAIL), "unexpected");
    }
  }
}
