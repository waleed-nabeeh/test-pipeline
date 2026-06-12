package com.mindcurv.b2x.productcatalog.aggregator.converter;

import static com.mindcurv.b2x.commons.models.ConverterType.DETAIL;
import static com.mindcurv.b2x.productcatalog.aggregator.helper.ConverterSetupHelper.initB2xContext;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import com.commercetools.api.models.attribute_group.AttributeGroup;
import com.commercetools.api.models.attribute_group.AttributeReference;
import com.commercetools.api.models.common.LocalizedString;
import com.mindcurv.b2x.commons.base.converter.StringLocalizedStringConverter;
import com.mindcurv.b2x.commons.models.B2xContext;
import com.mindcurv.b2x.productcatalog.aggregator.converter.impl.CommercetoolsAttributeGroupAggregationAttributeGroupConverter;
import com.mindcurv.b2x.productcatalog.aggregator.models.AttributeGroupAggregation;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

@Tag("UnitTest")
final class AttributeGroupAggregationAttributeGroupConverterTest {

  @Mock
  private StringLocalizedStringConverter stringLocalizedStringConverter;
  @Mock
  private AttributeGroup attributeGroup;

  private B2xContext context;

  private AttributeGroupAggregationAttributeGroupConverter converter;

  @BeforeEach
  void setUp() {
    openMocks(this);
    context = initB2xContext();

    when(attributeGroup.getId()).thenReturn("attributeGroupId");
    when(attributeGroup.getKey()).thenReturn("attributeGroupKey");
    when(attributeGroup.getName()).thenReturn(LocalizedString.of());
    when(attributeGroup.getDescription()).thenReturn(LocalizedString.of());
    final var attributeReference = AttributeReference.builder().key("key").build();
    when(attributeGroup.getAttributes()).thenReturn(List.of(attributeReference));

    when(stringLocalizedStringConverter.convert(notNull(), any(), any())).thenReturn(
        "localizedValue");

    converter = new CommercetoolsAttributeGroupAggregationAttributeGroupConverter();
    setField(converter, "stringLocalizedStringConverter", stringLocalizedStringConverter);
  }

  @Nested
  class convert {

    @Test
    void defaults() {

      final var aggregation = converter.convert(attributeGroup);
      assertNotNull(aggregation, "unexpected");
      assertAll(
          () -> assertEquals("attributeGroupId", aggregation.getId(), "unexpected"),
          () -> assertEquals("attributeGroupKey", aggregation.getKey(), "unexpected"));
    }

    @Test
    void nullValue() {
      assertNull(converter.convert(null), "unexpected");
    }
  }

  @Nested
  class processContextMapping {

    @Test
    void defaults() {

      final var aggregation = converter.processContextMapping(attributeGroup,
          new AttributeGroupAggregation(), context, DETAIL);
      assertNotNull(aggregation, "unexpected");
      assertAll(
          () -> assertNotNull(aggregation.getName(), "unexpected"),
          () -> assertNotNull(aggregation.getDescription(), "unexpected"),
          () -> assertNotNull(aggregation.getAttributes(), "unexpected"),
          () -> assertEquals(1, aggregation.getAttributes().size(), "unexpected"));
    }

    @Test
    void nullSource() {
      final var target = new AttributeGroupAggregation();
      assertEquals(target, converter.processContextMapping(null, target, context, DETAIL),
          "unexpected");
    }

    @Test
    void nullValue() {
      assertNull(converter.processContextMapping(attributeGroup, null, context, DETAIL),
          "unexpected");
    }
  }
}
