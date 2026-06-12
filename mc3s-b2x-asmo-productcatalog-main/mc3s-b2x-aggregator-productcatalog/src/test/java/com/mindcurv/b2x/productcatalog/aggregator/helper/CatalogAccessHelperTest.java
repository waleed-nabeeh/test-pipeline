package com.mindcurv.b2x.productcatalog.aggregator.helper;

import static com.mindcurv.b2x.productcatalog.aggregator.helper.ConverterSetupHelper.initB2xContext;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import com.mindcurv.b2x.commons.models.B2xContext;
import com.mindcurv.b2x.productcatalog.aggregator.models.CategoryNavigationAggregation;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

@Tag("UnitTest")
final class CatalogAccessHelperTest {

  private B2xContext context;
  @Mock
  private CategoryNavigationAggregation categoryNavigationAggregation;

  @BeforeEach
  void setUp() {
    openMocks(this);
    context = initB2xContext();

    when(categoryNavigationAggregation.getId()).thenReturn("categoryId");

  }

  @Nested
  class getFilteredContextCategories {

    @Test
    void defaults() {
      assertFalse(CatalogAccessHelper.getFilteredContextCategories(
          List.of(categoryNavigationAggregation), List.of("categoryId")).isEmpty(), "unexpected");
    }

    @Test
    void ancestor() {
      when(categoryNavigationAggregation.getId()).thenReturn("ancestorId");
      when(categoryNavigationAggregation.getAncestors()).thenReturn(
          List.of(new CategoryNavigationAggregation().id("categoryId")));
      assertFalse(CatalogAccessHelper.getFilteredContextCategories(
          List.of(categoryNavigationAggregation), List.of("categoryId")).isEmpty(), "unexpected");
    }

    @Test
    void failed() {
      assertTrue(CatalogAccessHelper.getFilteredContextCategories(
          List.of(categoryNavigationAggregation), List.of("unknown")).isEmpty(), "unexpected");
    }

    @Test
    void failedAncestor() {
      when(categoryNavigationAggregation.getId()).thenReturn("ancestorId");
      when(categoryNavigationAggregation.getAncestors()).thenReturn(
          List.of(new CategoryNavigationAggregation().id("categoryId")));
      assertTrue(CatalogAccessHelper.getFilteredContextCategories(
          List.of(categoryNavigationAggregation), List.of("unknown")).isEmpty(), "unexpected");
    }
  }

  @Nested
  class checkAccess {

    @Test
    void defaults() {
      final var aggregation = new CategoryNavigationAggregation();
      assertTrue(
          CatalogAccessHelper.checkAccess(aggregation, context).isPresent(), "unexpected");
    }

    @Test
    void notRestricted() {
      final var aggregation = new CategoryNavigationAggregation().restricted(false);
      assertTrue(
          CatalogAccessHelper.checkAccess(aggregation, context).isPresent(), "unexpected");
    }

    @Test
    void companyId() {
      final CategoryNavigationAggregation aggregation =
          new CategoryNavigationAggregation()
              .restricted(true)
              .addRestrictedCustomersItem("companyId");
      assertTrue(
          CatalogAccessHelper.checkAccess(aggregation, context).isPresent(), "unexpected");
    }

    @Test
    void customerId() {
      final CategoryNavigationAggregation aggregation =
          new CategoryNavigationAggregation()
              .restricted(true)
              .addRestrictedCustomersItem("customerId");
      assertTrue(
          CatalogAccessHelper.checkAccess(aggregation, context).isPresent(), "unexpected");
    }

    @Test
    void restricted() {
      final CategoryNavigationAggregation aggregation =
          new CategoryNavigationAggregation()
              .restricted(true)
              .addRestrictedCustomersItem("anotherId");
      assertFalse(
          CatalogAccessHelper.checkAccess(aggregation, context).isPresent(), "unexpected");
    }

    @Test
    void nullValue() {
      assertFalse(CatalogAccessHelper.checkAccess(null, context).isPresent(), "unexpected");
    }
  }

  @Nested
  class checkAccessAncestors {

    @Test
    void defaults() {
      final var ancestors =
          asList(
              new CategoryNavigationAggregation(),
              new CategoryNavigationAggregation()
                  .restricted(true)
                  .addRestrictedCustomersItem("customerId"));
      assertTrue(
          CatalogAccessHelper.checkAccessAncestors(ancestors, context), "unexpected");
    }

    @Test
    void restricted() {
      final var ancestors = asList(new CategoryNavigationAggregation(),
          new CategoryNavigationAggregation().restricted(true)
              .addRestrictedCustomersItem("anotherId"));
      assertFalse(
          CatalogAccessHelper.checkAccessAncestors(ancestors, context), "unexpected");
    }

    @Test
    void nullValue() {
      assertTrue(CatalogAccessHelper.checkAccessAncestors(null, context), "unexpected");
    }
  }
}