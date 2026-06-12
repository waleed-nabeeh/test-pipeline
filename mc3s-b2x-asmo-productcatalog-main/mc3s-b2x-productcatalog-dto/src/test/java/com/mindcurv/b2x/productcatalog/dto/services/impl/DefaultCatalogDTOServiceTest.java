package com.mindcurv.b2x.productcatalog.dto.services.impl;

import static com.mindcurv.b2x.commons.models.Pageable.initPageableFromList;
import static org.asynchttpclient.util.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import com.mindcurv.b2x.commons.models.PageableSearchRequest;
import com.mindcurv.b2x.connector.adapter.DTOReadAdapter;
import com.mindcurv.b2x.connector.catalog.models.CatalogDTO;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

@Tag("UnitTest")
final class DefaultCatalogDTOServiceTest {

  @Mock
  private DTOReadAdapter<CatalogDTO> readAdapter;

  @Mock
  private CatalogDTO dto;

  private DefaultCatalogDTOService service;

  @BeforeEach
  void setUp() {
    openMocks(this);

    when(dto.getStores()).thenReturn(List.of("store"));

    when(readAdapter.getItems(any())).thenReturn(initPageableFromList(List.of(dto)));

    when(readAdapter.getAllItems(any())).thenReturn(List.of(dto));

    service = new DefaultCatalogDTOService(readAdapter);
  }

  @Nested
  class getItems {

    @Test
    void defaults() {
      final var result = service.getItems(new PageableSearchRequest());
      assertNotNull(result, "unexpected");
      assertFalse(result.getResults().isEmpty(), "unexpected results");
    }
  }

  @Nested
  class getActiveItems {

    @Test
    void defaults() {
      assertFalse(service.getActiveItems("store").isEmpty(), "unexpected results");
    }

    @Test
    void noStore() {
      when(dto.getStores()).thenReturn(null);
      assertFalse(service.getActiveItems("store").isEmpty(), "unexpected results");
    }

    @Test
    void mismatch() {
      assertTrue(service.getActiveItems("unknown").isEmpty(), "unexpected results");
    }
  }
}