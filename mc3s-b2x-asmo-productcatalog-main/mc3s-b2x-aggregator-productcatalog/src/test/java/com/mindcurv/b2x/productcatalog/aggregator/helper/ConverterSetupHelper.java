package com.mindcurv.b2x.productcatalog.aggregator.helper;

import static java.util.Locale.ENGLISH;
import static java.util.Locale.FRENCH;
import static java.util.Locale.GERMAN;
import static lombok.AccessLevel.PRIVATE;

import com.mindcurv.b2x.commons.models.B2xContext;
import com.mindcurv.b2x.commons.models.PriceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import lombok.NoArgsConstructor;
@NoArgsConstructor(access = PRIVATE)
public final class ConverterSetupHelper {

  public static final int REDUCED_SIZE = 2;
  public static final String CURRENCY = "USD";

  public static B2xContext initB2xContext() {
    final List<String> languages = new ArrayList<>();
    for (final var locale : getTestLocales()) {
      languages.add(locale.toString());
      if (languages.size() == REDUCED_SIZE) {
        break;
      }
    }
    return B2xContext.builder()
        .store("store")
        .languages(languages)
        .currentLanguage("en")
        .priceContext(new PriceContext().currency(CURRENCY))
        .customerId("customerId")
        .companyId("companyId")
        .activeUnit("businessUnit")
        .build();
  }

  public static List<Locale> getTestLocales() {
    return List.of(ENGLISH, GERMAN, FRENCH);
  }
}
