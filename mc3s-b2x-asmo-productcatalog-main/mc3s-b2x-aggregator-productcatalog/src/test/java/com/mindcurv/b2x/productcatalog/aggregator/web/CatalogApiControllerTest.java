package com.mindcurv.b2x.productcatalog.aggregator.web;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.springframework.http.HttpStatus.OK;

import com.mindcurv.b2x.commons.models.Pageable;
import com.mindcurv.b2x.commons.token.services.ContextService;
import com.mindcurv.b2x.commons.token.services.TokenService;
import com.mindcurv.b2x.productcatalog.aggregator.aggregation.CatalogAggregationService;
import com.mindcurv.b2x.productcatalog.aggregator.models.CatalogAggregation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.web.context.request.NativeWebRequest;

@Tag("UnitTest")
final class CatalogApiControllerTest {

  @Mock
  private CatalogAggregationService catalogAggregationService;
  @Mock
  private ContextService contextService;
  @Mock
  private CatalogAggregation aggregation;
  @Mock
  private Pageable<CatalogAggregation> pageable;
  @Mock
  private TokenService tokenService;
  @Mock
  private NativeWebRequest nativeWebRequest;

  @Spy
  @InjectMocks
  private CatalogApiController controller;

  @BeforeEach
  void setUp() {
    openMocks(this);

    when(pageable.getResults()).thenReturn(singletonList(aggregation));
    when(pageable.getCount()).thenReturn(1L);
    when(pageable.getTotal()).thenReturn(1L);
    when(pageable.getTotalPages()).thenReturn(1L);
    when(pageable.getOffset()).thenReturn(0L);
    when(pageable.getLimit()).thenReturn(20L);

    when(catalogAggregationService.getCatalogs(any(), any())).thenReturn(pageable);
  }

  @Nested
  class catalogs {

    @Test
    void defaults() {
      final var responseEntity = controller.catalogs(0L, 20L);
      assertNotNull(responseEntity, "unexpected");
      assertAll(
          () -> assertEquals(OK, responseEntity.getStatusCode(), "unexpected"),
          () -> assertTrue(responseEntity.hasBody(), "unexpected"));
    }
  }
}
