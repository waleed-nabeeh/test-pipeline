package com.mindcurv.b2x.productcatalog.aggregator.helper;

import static com.mindcurv.b2x.customizing.helper.CustomFieldsHelper.getCustomFieldsAccessor;
import static lombok.AccessLevel.PRIVATE;
import static org.apache.commons.collections4.CollectionUtils.containsAny;
import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

import com.commercetools.api.models.common.Reference;
import com.commercetools.api.models.type.CustomFields;
import com.mindcurv.b2x.commons.models.B2xContext;
import com.mindcurv.b2x.productcatalog.aggregator.models.CategoryNavigationAggregation;
import jakarta.validation.Valid;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@NoArgsConstructor(access = PRIVATE)
public final class CatalogAccessHelper {

  private static final Logger LOG = LoggerFactory.getLogger(CatalogAccessHelper.class);

  public static boolean accessAllowed(
      @Nullable @Valid final Collection<String> restrictedIds,
      @NotNull final B2xContext context) {
    if (isNotEmpty(restrictedIds) && !containsAny(restrictedIds, context.getCustomerId(),
        context.getCompanyId())) {
      LOG.info("accessAllowed :: access not allowed {}", restrictedIds);
      return false;
    }
    LOG.debug("accessAllowed :: access allowed");
    return true;
  }

  @NotNull
  public static Collection<String> getRestrictedCustomerIds(
      @Nullable @Valid final CustomFields custom,
      @Nullable final String fieldName) {
    final var attName = Optional.ofNullable(fieldName);
    if (attName.isPresent()) {
      return getCustomFieldsAccessor(custom).map(
          accessor -> emptyIfNull(accessor.asSetReference(attName.get())).stream()
              .map(Reference::getId).toList()).orElseGet(List::of);
    }
    LOG.debug("getRestrictedCustomerIds :: no values configured");
    return List.of();
  }

  @NotNull
  public static Optional<CategoryNavigationAggregation> checkAccess(
      @Nullable final CategoryNavigationAggregation aggregation,
      @NotNull final B2xContext context) {
    if (aggregation == null) {
      return Optional.empty();
    }
    final var restrictedIds = aggregation.getRestrictedCustomers();
    if (isEmpty(restrictedIds)) {
      return Optional.of(aggregation);
    }
    final var testIds = List.of(
        context.getCustomerId() == null ? "anonymous" : context.getCustomerId(),
        context.getCompanyId() == null ? "anonymous" : context.getCompanyId());
    return containsAny(restrictedIds, testIds) ? Optional.of(aggregation) : Optional.empty();
  }

  public static boolean checkAccessAncestors(
      @Nullable final Collection<CategoryNavigationAggregation> ancestors,
      @NotNull final B2xContext context) {
    return emptyIfNull(ancestors).stream()
        .noneMatch(ancestor -> checkAccess(ancestor, context).isEmpty());
  }

  @NotNull
  public static Collection<CategoryNavigationAggregation> getFilteredContextCategories(
      @Nullable final Collection<CategoryNavigationAggregation> initial,
      @Nullable final Collection<String> catalogIds) {
    if (catalogIds == null) {
      return emptyIfNull(initial);
    }
    final var categories = new LinkedList<CategoryNavigationAggregation>();
    for (final var category : emptyIfNull(initial)) {
      var allowed = catalogIds.contains(category.getId());
      if (!allowed) {
        allowed = !category.getAncestors().stream()
            .filter(ancestor -> catalogIds.contains(ancestor.getId()))
            .toList().isEmpty();
      }
      if (allowed) {
        categories.add(category);
      }
    }
    return categories;
  }
}
