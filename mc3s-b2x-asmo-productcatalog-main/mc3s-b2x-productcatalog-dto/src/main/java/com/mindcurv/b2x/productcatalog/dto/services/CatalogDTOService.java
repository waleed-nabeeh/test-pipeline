package com.mindcurv.b2x.productcatalog.dto.services;

import com.mindcurv.b2x.commons.models.Pageable;
import com.mindcurv.b2x.commons.models.PageableSearchRequest;
import com.mindcurv.b2x.connector.catalog.models.CatalogDTO;
import jakarta.validation.Valid;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;

public interface CatalogDTOService {

  @NotNull
  Pageable<CatalogDTO> getItems(@NotNull @Valid PageableSearchRequest searchRequest);

  @NotNull
  Collection<CatalogDTO> getActiveItems(@NotNull String store);

}
