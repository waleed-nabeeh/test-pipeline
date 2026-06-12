package com.mindcurv.b2x.productcatalog.aggregator.aggregation;

import com.mindcurv.b2x.commons.models.B2xContext;
import com.mindcurv.b2x.commons.models.Pageable;
import com.mindcurv.b2x.commons.models.PageableSearchRequest;
import com.mindcurv.b2x.productcatalog.aggregator.models.CatalogAggregation;
import org.jetbrains.annotations.NotNull;

public interface CatalogAggregationService {

  @NotNull
  Pageable<CatalogAggregation> getCatalogs(
      @NotNull B2xContext b2xContext, @NotNull PageableSearchRequest searchRequest);

}
