package com.mindcurv.b2x.productcatalog.aggregator.helper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import com.commercetools.api.models.category.Category;
import com.commercetools.api.models.type.CustomFields;
import com.commercetools.api.models.type.FieldContainer;
import com.mindcurv.b2x.customizing.configuration.AbstractCategoryTypeConfiguration;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

@Tag("UnitTest")
final class CategoryAggregationHelperTest {

  @Mock
  private Category category;
  @Mock
  private CustomFields customFields;
  @Mock
  private AbstractCategoryTypeConfiguration configuration;

  @BeforeEach
  void setUp() {
    openMocks(this);
    when(customFields.getFields()).thenReturn(
        FieldContainer.builder().values(Map.of("renderingTemplate", "custom-template")).build());

    when(category.getCustom()).thenReturn(customFields);

    when(configuration.getRenderingTemplate()).thenReturn(Optional.of("renderingTemplate"));

  }

  @Nested
  class getRenderingTemplate {

    @Test
    void defaults() {
      final var result = CategoryAggregationHelper.getRenderingTemplate(category, configuration);
      assertEquals("custom-template", result, "expected custom template");
    }

    @Test
    void defaultsWithoutConfigurationName() {
      when(configuration.getRenderingTemplate()).thenReturn(Optional.empty());
      final var result = CategoryAggregationHelper.getRenderingTemplate(category, configuration);
      assertEquals("default", result, "expected default template");
    }

    @Test
    void attributeValueNotString() {
      when(customFields.getFields()).thenReturn(
          FieldContainer.builder().values(Map.of("renderingTemplate", 123)).build());

      final var result = CategoryAggregationHelper.getRenderingTemplate(category, configuration);

      assertEquals("default", result, "expected default template when value is not string");
    }
  }
}

