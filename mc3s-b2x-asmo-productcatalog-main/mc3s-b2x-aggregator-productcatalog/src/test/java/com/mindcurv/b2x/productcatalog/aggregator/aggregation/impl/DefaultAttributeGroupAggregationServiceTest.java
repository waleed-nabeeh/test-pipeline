package com.mindcurv.b2x.productcatalog.aggregator.aggregation.impl;

import static com.mindcurv.b2x.commons.models.Pageable.emptyPageable;
import static com.mindcurv.b2x.commons.models.Pageable.initPageableFromList;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.springframework.test.util.ReflectionTestUtils.invokeMethod;

import com.commercetools.api.models.ResourcePagedQueryResponse;
import com.commercetools.api.models.attribute_group.AttributeGroup;
import com.mindcurv.b2x.commons.commercetools.services.impl.AttributeGroupCommercetoolsService;
import com.mindcurv.b2x.commons.models.B2xContext;
import com.mindcurv.b2x.commons.models.PageableSearchRequest;
import com.mindcurv.b2x.productcatalog.aggregator.converter.AttributeGroupAggregationAttributeGroupConverter;
import com.mindcurv.b2x.productcatalog.aggregator.models.AttributeGroupAggregation;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

@Tag("UnitTest")
final class DefaultAttributeGroupAggregationServiceTest {

  @Mock
  private AttributeGroupCommercetoolsService attributeGroupCommercetoolsService;
  @Mock
  private AttributeGroupAggregationAttributeGroupConverter groupConverter;
  @Mock
  private ResourcePagedQueryResponse<AttributeGroup> pagedQueryResponse;
  @Mock
  private AttributeGroup attributeGroup;

  @Mock
  private PageableSearchRequest searchRequest;

  private B2xContext context;
  private DefaultAttributeGroupAggregationService service;

  @BeforeEach
  void setUp() {
    openMocks(this);

    when(attributeGroup.getKey()).thenReturn("Type-GLOBAL");

    context = B2xContext.builder().store("store").languages(List.of("en")).build();
    when(pagedQueryResponse.getResults()).thenReturn(List.of(attributeGroup));
    when(attributeGroupCommercetoolsService.getAllItems()).thenReturn(List.of(attributeGroup));
    when(attributeGroupCommercetoolsService.getItems(anyLong(), anyLong()))
        .thenReturn(pagedQueryResponse);

    when(attributeGroupCommercetoolsService.getItemById(anyString())).thenReturn(attributeGroup);
    when(attributeGroupCommercetoolsService.getItemByKey(anyString())).thenReturn(attributeGroup);

    when(groupConverter.convertAsOptional(any(), any()))
        .thenReturn(Optional.of(new AttributeGroupAggregation()));

    when(groupConverter.convertPageableFromResponse(any(), any()))
        .thenReturn(initPageableFromList(List.of(new AttributeGroupAggregation())));
    when(groupConverter.convertPageableFromResponse(null, context)).thenReturn(emptyPageable());

    service =
        new DefaultAttributeGroupAggregationService(
            attributeGroupCommercetoolsService, groupConverter);
    invokeMethod(service, "postConstruct");
  }

  @Test
  void getItems() {
    assertNotNull(service.getItems(searchRequest, context), "unexpected");
  }

  @Nested
  class getGroupsForType {

    @Test
    void defaults() {
      assertEquals(1, service.getGroupsForType("Type", context).size(), "unexpected");
    }

    @Test
    void failed() {
      assertTrue(service.getGroupsForType("unknown", context).isEmpty(), "unexpected");
    }
  }

  @Nested
  class findByKey {

    @Test
    void defaults() {
      assertTrue(service.findByKey("key", context).isPresent(), "unexpected");
    }

    @Test
    void nullValue() {
      assertTrue(service.findByKey(null, context).isEmpty(), "unexpected");
    }

    @Test
    void empty() {
      assertTrue(service.findByKey(EMPTY, context).isEmpty(), "unexpected");
    }
  }

  @Nested
  class findById {

    @Test
    void defaults() {
      assertNotNull(service.findById("id", context), "unexpected");
    }

    @Test
    void nullValue() {
      assertTrue(service.findById(null, context).isEmpty(), "unexpected");
    }

    @Test
    void empty() {
      assertTrue(service.findById(EMPTY, context).isEmpty(), "unexpected");
    }
  }
}
