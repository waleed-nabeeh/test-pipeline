package com.mindcurv.b2x.productcatalog.aggregator.helper;

import static com.mindcurv.b2x.productcatalog.aggregator.helper.ConverterSetupHelper.initB2xContext;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import com.mindcurv.b2x.commons.models.B2xContext;
import com.mindcurv.b2x.commons.models.ImageAggregation;
import com.mindcurv.b2x.commons.models.URLMetaData;
import com.mindcurv.b2x.productcatalog.aggregator.models.CategoryAggregation;
import com.mindcurv.b2x.productcatalog.aggregator.models.CategoryNavigationAggregation;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

@Tag("UnitTest")
final class CatalogHelperTest {

  @Mock
  private URLMetaData urlMetaData;
  private B2xContext context;

  @BeforeEach
  void setUp() {

    openMocks(this);
    context = initB2xContext();

    when(urlMetaData.getPatterns()).thenReturn(Map.of("assets", "http://localhost"));

  }

  @Nested
  class getMainNavigationKey {

    @Test
    void defaults() {
      assertEquals("tree-en-store-companyId-businessUnit-customerId",
          CatalogHelper.getMainNavigationKey(context));
    }

    @Test
    void fallback() {
      assertEquals("tree-en-null-null-null-null",
          CatalogHelper.getMainNavigationKey(B2xContext.builder().build()));
    }
  }

  @Nested
  class processUri {

    @Test
    void defaults() {
      assertEquals("http://localhost/uri", CatalogHelper.processUri("/uri", "http://localhost"),
          "expected uri");
    }

    @Test
    void noSlash() {
      assertEquals("http://localhost/uri", CatalogHelper.processUri("uri", "http://localhost"),
          "expected uri");
    }
  }

  @Nested
  class determineAssetsPrefix {

    @Test
    void defaults() {
      assertEquals("http://localhost", CatalogHelper.determineAssetsPrefix(urlMetaData),
          "expected prefix");
    }

    @Test
    void noSetting() {
      when(urlMetaData.getPatterns()).thenReturn(Map.of());
      assertEquals(EMPTY, CatalogHelper.determineAssetsPrefix(urlMetaData), "expected empty");
    }

    @Test
    void nullSetting() {
      when(urlMetaData.getPatterns()).thenReturn(null);
      assertEquals(EMPTY, CatalogHelper.determineAssetsPrefix(urlMetaData), "expected empty");
    }

  }

  @Nested
  class processImageForCategory {

    @Mock
    private CategoryAggregation categoryAggregation;
    @Mock
    private ImageAggregation imageAggregation;

    @BeforeEach
    void setUp() {
      openMocks(this);
      when(imageAggregation.getUrl()).thenReturn("url");
      when(categoryAggregation.getImage()).thenReturn(imageAggregation);

    }

    @Test
    void defaults() {
      final var result = CatalogHelper.processImageForCategory(categoryAggregation, "prefix")
          .orElse(null);
      assertNotNull(result, "expected result");
    }
  }

  @Nested
  class processImageForCategoryNavigationAggregation {

    @Mock
    private CategoryNavigationAggregation categoryAggregation;
    @Mock
    private ImageAggregation imageAggregation;

    @BeforeEach
    void setUp() {
      openMocks(this);
      when(imageAggregation.getUrl()).thenReturn("url");
      when(categoryAggregation.getImage()).thenReturn(imageAggregation);

    }

    @Test
    void defaults() {
      CatalogHelper.processImageForCategoryNavigationAggregation(
          categoryAggregation, "prefix");
      assertNotNull(categoryAggregation, "expected result");
    }
  }

  @Nested
  class getCategoryCacheKey {

    @Test
    void defaults() {
      assertEquals(
          "category-key-identifier-en-store-companyId-businessUnit-customerId",
          CatalogHelper.getCategoryCacheKey(context, "key", "identifier"),
          "unexpected");
    }

    @Test
    void emptyValues() {
      assertEquals("category-key-identifier-en-null-null-null-null",
          CatalogHelper.getCategoryCacheKey(B2xContext.builder().build(), "key", "identifier"),
          "unexpected");
    }
  }

}