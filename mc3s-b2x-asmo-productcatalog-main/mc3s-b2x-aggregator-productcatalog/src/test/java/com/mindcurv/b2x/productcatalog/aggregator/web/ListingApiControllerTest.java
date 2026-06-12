package com.mindcurv.b2x.productcatalog.aggregator.web;

import static com.mindcurv.b2x.commons.models.Pageable.emptyPageable;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.springframework.http.HttpStatus.OK;

import com.mindcurv.b2x.commons.token.services.ContextService;
import com.mindcurv.b2x.commons.token.services.TokenService;
import com.mindcurv.b2x.productcatalog.aggregator.aggregation.ListingAggregationService;
import com.mindcurv.b2x.productcatalog.aggregator.models.ListingAggregation;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.web.context.request.NativeWebRequest;

@Tag("UnitTest")
final class ListingApiControllerTest {

  @Mock
  private ListingAggregationService aggregationService;
  @Mock
  private ContextService contextService;
  @Mock
  private TokenService tokenService;
  @Mock
  private NativeWebRequest nativeWebRequest;

  @Spy
  @InjectMocks
  private ListingApiController controller;

  @BeforeEach
  void setUp() {
    openMocks(this);

    when(aggregationService.productListingsById(anyString(), any()))
        .thenReturn(new ListingAggregation());
    when(aggregationService.productListingsByKey(anyString(), any()))
        .thenReturn(new ListingAggregation());
    when(aggregationService.productListingsBySku(anyString(), any()))
        .thenReturn(new ListingAggregation());

    when(aggregationService.productListingsByCart(anyString(), any())).thenReturn(emptyPageable());
    when(aggregationService.productListingsByCurrentCart(any())).thenReturn(emptyPageable());
    when(aggregationService.productListingsByOrder(anyString(), any())).thenReturn(emptyPageable());
    when(aggregationService.productListingsByShoppinglist(anyString(), any()))
        .thenReturn(emptyPageable());
    when(aggregationService.productListingsByReferences(anyList(), any()))
        .thenReturn(emptyPageable());
  }

  @Nested
  class listingById {

    @Test
    void defaults() {
      final var responseEntity = controller.listingById("id");
      assertNotNull(responseEntity, "unexpected");
      assertAll(
          () -> assertEquals(OK, responseEntity.getStatusCode(), "unexpected"),
          () -> assertTrue(responseEntity.hasBody(), "unexpected"));
    }
  }

  @Nested
  class listingByKey {

    @Test
    void defaults() {
      final var responseEntity = controller.listingByKey("key");
      assertNotNull(responseEntity, "unexpected");
      assertAll(
          () -> assertEquals(OK, responseEntity.getStatusCode(), "unexpected"),
          () -> assertTrue(responseEntity.hasBody(), "unexpected"));
    }
  }

  @Nested
  class listingBySku {

    @Test
    void defaults() {
      final var responseEntity = controller.listingBySku("sku");
      assertNotNull(responseEntity, "unexpected");
      assertAll(
          () -> assertEquals(OK, responseEntity.getStatusCode(), "unexpected"),
          () -> assertTrue(responseEntity.hasBody(), "unexpected"));
    }
  }

  @Nested
  class listingByCart {

    @Test
    void defaults() {
      final var responseEntity = controller.listingByCart("id");
      assertNotNull(responseEntity, "unexpected");
      assertAll(
          () -> assertEquals(OK, responseEntity.getStatusCode(), "unexpected"),
          () -> assertTrue(responseEntity.hasBody(), "unexpected"));
    }
  }

  @Nested
  class listingByOrder {

    @Test
    void defaults() {
      final var responseEntity = controller.listingByOrder("id");
      assertNotNull(responseEntity, "unexpected");
      assertAll(
          () -> assertEquals(OK, responseEntity.getStatusCode(), "unexpected"),
          () -> assertTrue(responseEntity.hasBody(), "unexpected"));
    }
  }

  @Nested
  class listingByShoppinglist {

    @Test
    void defaults() {
      final var responseEntity = controller.listingByShoppinglist("id");
      assertNotNull(responseEntity, "unexpected");
      assertAll(
          () -> assertEquals(OK, responseEntity.getStatusCode(), "unexpected"),
          () -> assertTrue(responseEntity.hasBody(), "unexpected"));
    }
  }

  @Nested
  class listingsByReference {

    @Test
    void defaults() {
      final var responseEntity = controller.listingsByReference(List.of());
      assertNotNull(responseEntity, "unexpected");
      assertAll(
          () -> assertEquals(OK, responseEntity.getStatusCode(), "unexpected"),
          () -> assertTrue(responseEntity.hasBody(), "unexpected"));
    }
  }
}
