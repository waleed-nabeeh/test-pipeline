package com.mindcurv.b2x.productcatalog.aggregator.services;

import com.mindcurv.b2x.commons.models.B2xContext;
import com.mindcurv.b2x.productcatalog.aggregator.models.CategoryNavigationAggregation;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;

public interface CategoryTreeService {

  @NotNull
  Collection<CategoryNavigationAggregation> generateTree(@NotNull B2xContext context);

}
