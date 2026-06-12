package com.mindcurv.b2x.productcatalog.aggregator.aggregation;

import com.mindcurv.b2x.commons.models.B2xContext;
import com.mindcurv.b2x.commons.models.Pageable;
import com.mindcurv.b2x.commons.models.PageableSearchRequest;
import com.mindcurv.b2x.productcatalog.aggregator.models.AttributeGroupAggregation;
import jakarta.validation.Valid;
import java.util.Map;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface AttributeGroupAggregationService {

  @NotNull
  Map<String, AttributeGroupAggregation> getGroupsForType(
      @NotNull String typeKey, @NotNull B2xContext context);

  @NotNull
  Pageable<AttributeGroupAggregation> getItems(
      @NotNull @Valid PageableSearchRequest searchRequest, @NotNull B2xContext context);

  @NotNull
  Optional<AttributeGroupAggregation> findByKey(@Nullable String key, @NotNull B2xContext context);

  @NotNull
  Optional<AttributeGroupAggregation> findById(@Nullable String id, @NotNull B2xContext context);
}
