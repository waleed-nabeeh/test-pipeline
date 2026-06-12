package com.mindcurv.b2x.productcatalog.aggregator.web;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

import com.mindcurv.b2x.commons.models.Pageable;
import com.mindcurv.b2x.commons.token.services.ContextService;
import com.mindcurv.b2x.commons.token.services.TokenService;
import com.mindcurv.b2x.productcatalog.aggregator.aggregation.AttributeGroupAggregationService;
import com.mindcurv.b2x.productcatalog.aggregator.models.AttributeGroupAggregation;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.web.context.request.NativeWebRequest;

@Tag("UnitTest")
final class AttributeGroupApiControllerTest {

  @Mock
  private AttributeGroupAggregationService attributeGroupAggregationService;
  @Mock
  private ContextService contextService;
  @Mock
  private AttributeGroupAggregation aggregation;
  @Mock
  private Pageable<AttributeGroupAggregation> pageable;
  @Mock
  private TokenService tokenService;
  @Mock
  private NativeWebRequest nativeWebRequest;

  @Spy
  @InjectMocks
  private AttributeGroupApiController controller;

  @BeforeEach
  void setUp() {
    openMocks(this);

    when(pageable.getResults()).thenReturn(singletonList(aggregation));
    when(pageable.getCount()).thenReturn(1L);
    when(pageable.getTotal()).thenReturn(1L);
    when(pageable.getTotalPages()).thenReturn(1L);
    when(pageable.getOffset()).thenReturn(0L);
    when(pageable.getLimit()).thenReturn(20L);

    when(attributeGroupAggregationService.findById(anyString(), any()))
        .thenReturn(Optional.of(aggregation));
    when(attributeGroupAggregationService.findByKey(anyString(), any()))
        .thenReturn(Optional.of(aggregation));
    when(attributeGroupAggregationService.getItems(any(), any())).thenReturn(pageable);
    when(attributeGroupAggregationService.getGroupsForType(anyString(), any()))
        .thenReturn(Map.of());
  }

  @Nested
  class attributeGroupsForType {

    @Test
    void defaults() {
      final var responseEntity = controller.attributeGroupsForType("type");
      assertNotNull(responseEntity, "unexpected");
      assertAll(
          () -> assertEquals(OK, responseEntity.getStatusCode(), "unexpected"),
          () -> assertTrue(responseEntity.hasBody(), "unexpected"));
    }
  }

  @Nested
  class attributeGroups {

    @Test
    void defaults() {
      final var responseEntity = controller.attributeGroups(0L, 20L);
      assertNotNull(responseEntity, "unexpected");
      assertAll(
          () -> assertEquals(OK, responseEntity.getStatusCode(), "unexpected"),
          () -> assertTrue(responseEntity.hasBody(), "unexpected"));
    }
  }

  @Nested
  class attributeGroupByKey {

    @Test
    void defaults() {
      final var responseEntity = controller.attributeGroupByKey("key");
      assertNotNull(responseEntity, "unexpected");
      assertAll(
          () -> assertEquals(OK, responseEntity.getStatusCode(), "unexpected"),
          () -> assertTrue(responseEntity.hasBody(), "unexpected"));
    }

    @Test
    void notFound() {
      when(attributeGroupAggregationService.findByKey(anyString(), any()))
          .thenReturn(Optional.empty());
      final var responseEntity = controller.attributeGroupByKey("key");
      assertNotNull(responseEntity, "unexpected");
      assertAll(
          () -> assertEquals(NOT_FOUND, responseEntity.getStatusCode(), "unexpected"),
          () -> assertFalse(responseEntity.hasBody(), "unexpected"));
    }
  }

  @Nested
  class attributeGroupById {

    @Test
    void defaults() {
      final var responseEntity = controller.attributeGroupById("id");
      assertNotNull(responseEntity, "unexpected");
      assertAll(
          () -> assertEquals(OK, responseEntity.getStatusCode(), "unexpected"),
          () -> assertTrue(responseEntity.hasBody(), "unexpected"));
    }

    @Test
    void notFound() {
      when(attributeGroupAggregationService.findById(anyString(), any()))
          .thenReturn(Optional.empty());
      final var responseEntity = controller.attributeGroupById("id");
      assertNotNull(responseEntity, "unexpected");
      assertAll(
          () -> assertEquals(NOT_FOUND, responseEntity.getStatusCode(), "unexpected"),
          () -> assertFalse(responseEntity.hasBody(), "unexpected"));
    }
  }
}
