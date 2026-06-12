package com.mindcurv.b2x.productcatalog.dto.services.impl;

import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;

import com.mindcurv.b2x.commons.models.FilterOption;
import com.mindcurv.b2x.commons.models.Pageable;
import com.mindcurv.b2x.commons.models.PageableSearchRequest;
import com.mindcurv.b2x.commons.models.SearchRequest;
import com.mindcurv.b2x.connector.adapter.DTOReadAdapter;
import com.mindcurv.b2x.connector.catalog.models.CatalogDTO;
import com.mindcurv.b2x.productcatalog.dto.services.CatalogDTOService;
import java.util.Collection;
import java.util.LinkedList;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class DefaultCatalogDTOService implements CatalogDTOService {

  private static final Logger LOG = LoggerFactory.getLogger(DefaultCatalogDTOService.class);

  @NotNull
  private final DTOReadAdapter<CatalogDTO> readAdapter;

  @Autowired
  public DefaultCatalogDTOService(@NotNull final DTOReadAdapter<CatalogDTO> readAdapter) {
    this.readAdapter = readAdapter;
  }

  @Override
  @NotNull
  public Pageable<CatalogDTO> getItems(@NotNull final PageableSearchRequest searchRequest) {
    return readAdapter.getItems(searchRequest);
  }

  @Override
  @NotNull
  public Collection<CatalogDTO> getActiveItems(@NotNull final String store) {
    final var result = new LinkedList<CatalogDTO>();
    final var request = new SearchRequest()
        .addFilterItem(new FilterOption("store", store))
        .addFilterItem(new FilterOption("active", "true"));
    for (final var item : readAdapter.getAllItems(request)) {
      final var stores = emptyIfNull(item.getStores()).stream().toList();
      if (stores.isEmpty() || stores.contains(store)) {
        result.add(item);
      }
    }
    LOG.info("getActiveItems :: {} results", result.size());
    return result;
  }
}
