package com.mindcurv.b2x.productcatalog.aggregator.aggregation.impl;

import static com.mindcurv.b2x.commons.DefaultsConstants.DASH;
import static org.apache.commons.collections4.MapUtils.emptyIfNull;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.split;

import com.commercetools.api.models.attribute_group.AttributeGroup;
import com.mindcurv.b2x.commons.commercetools.services.impl.AttributeGroupCommercetoolsService;
import com.mindcurv.b2x.commons.models.B2xContext;
import com.mindcurv.b2x.commons.models.Pageable;
import com.mindcurv.b2x.commons.models.PageableSearchRequest;
import com.mindcurv.b2x.productcatalog.aggregator.aggregation.AttributeGroupAggregationService;
import com.mindcurv.b2x.productcatalog.aggregator.converter.AttributeGroupAggregationAttributeGroupConverter;
import com.mindcurv.b2x.productcatalog.aggregator.models.AttributeGroupAggregation;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DefaultAttributeGroupAggregationService implements AttributeGroupAggregationService {

  private static final Logger LOG = LoggerFactory.getLogger(
      DefaultAttributeGroupAggregationService.class);

  public static final int MIN_CHUNKS = 1;
  @NotNull
  private final AttributeGroupCommercetoolsService attributeGroupCommercetoolsService;

  @NotNull
  @Valid
  private final AttributeGroupAggregationAttributeGroupConverter attributeGroupConverter;

  private static final Map<String, Map<String, AttributeGroup>> GROUPS = new LinkedHashMap<>();

  @Autowired
  public DefaultAttributeGroupAggregationService(
      @NotNull final AttributeGroupCommercetoolsService attributeGroupCommercetoolsService,
      @NotNull final AttributeGroupAggregationAttributeGroupConverter attributeGroupConverter) {
    this.attributeGroupCommercetoolsService = attributeGroupCommercetoolsService;
    this.attributeGroupConverter = attributeGroupConverter;
  }

  @PostConstruct
  private void postConstruct() {
    GROUPS.clear();
    for (final var attributeGroup : attributeGroupCommercetoolsService.getAllItems()) {
      final var chunks = split(attributeGroup.getKey(), DASH);
      if (chunks.length > MIN_CHUNKS) {
        final var type = chunks[0];
        final var mapForGroup = GROUPS.getOrDefault(type, new LinkedHashMap<>());
        mapForGroup.put(chunks[1], attributeGroup);
        GROUPS.put(type, mapForGroup);
      }
    }
    LOG.info("postConstruct :: prepared {} types", GROUPS.size());
  }

  @Override
  @NotNull
  public Map<String, AttributeGroupAggregation> getGroupsForType(
      @NotNull final String typeKey, @NotNull final B2xContext context) {
    final var groupMap = new LinkedHashMap<String, AttributeGroupAggregation>();
    emptyIfNull(GROUPS.get(typeKey)).forEach((key, value) ->
        attributeGroupConverter.convertAsOptional(value, context)
            .ifPresent(aggregation -> groupMap.put(key, aggregation)));
    return groupMap;
  }

  @Override
  public @NotNull Pageable<AttributeGroupAggregation> getItems(
      @NotNull @Valid final PageableSearchRequest searchRequest,
      @NotNull final B2xContext context) {
    return attributeGroupConverter.convertPageableFromResponse(
            attributeGroupCommercetoolsService.getItems(searchRequest.getOffset(),
                searchRequest.getLimit()), context)
        .setSearchRequest(searchRequest);
  }

  @Override
  @NotNull
  public Optional<AttributeGroupAggregation> findByKey(
      @Nullable final String key, @NotNull final B2xContext context) {
    return isBlank(key) ? Optional.empty() : attributeGroupConverter.convertAsOptional(
        attributeGroupCommercetoolsService.getItemByKey(key), context);
  }

  @Override
  @NotNull
  public Optional<AttributeGroupAggregation> findById(
      @Nullable final String id, @NotNull final B2xContext context) {
    return isBlank(id) ? Optional.empty() : attributeGroupConverter.convertAsOptional(
        attributeGroupCommercetoolsService.getItemById(id), context);
  }
}
