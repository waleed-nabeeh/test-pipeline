package com.mindcurv.b2x.productcatalog.all.helper;

import static lombok.AccessLevel.PRIVATE;

import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@NoArgsConstructor(access = PRIVATE)
public class ProductCatalogAllHelper {

  private static final Logger LOG = LoggerFactory.getLogger(ProductCatalogAllHelper.class);

  public static void logTest() {
    LOG.debug("test ::");
  }
}
