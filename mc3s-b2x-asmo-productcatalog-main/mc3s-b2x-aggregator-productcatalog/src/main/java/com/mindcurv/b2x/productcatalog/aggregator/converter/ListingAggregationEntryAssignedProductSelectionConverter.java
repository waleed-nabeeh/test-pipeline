package com.mindcurv.b2x.productcatalog.aggregator.converter;

import static org.mapstruct.ReportingPolicy.IGNORE;

import com.commercetools.api.models.product_selection.AssignedProductSelection;
import com.commercetools.api.models.product_selection.ProductVariantSelectionIncludeAllExcept;
import com.commercetools.api.models.product_selection.ProductVariantSelectionIncludeOnly;
import com.mindcurv.b2x.commons.converter.BaseConverter;
import com.mindcurv.b2x.commons.helper.MapperHelper;
import com.mindcurv.b2x.productcatalog.aggregator.models.ListingAggregationEntry;
import jakarta.validation.Valid;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring",
    unmappedTargetPolicy = IGNORE,
    implementationName = "Commercetools<CLASS_NAME>",
    implementationPackage = "<PACKAGE_NAME>.impl",
    uses = {
        MapperHelper.class
    })
public abstract class ListingAggregationEntryAssignedProductSelectionConverter
    implements BaseConverter<AssignedProductSelection, ListingAggregationEntry> {

  @Override
  @Mapping(source = "productSelection.id", target = "id")
  @Mapping(source = "productSelection.obj.key", target = "key")
  @Mapping(source = "productSelection.obj.mode", target = "mode", qualifiedByName = "convertJsonEnum")
  public abstract ListingAggregationEntry convert(@Nullable @Valid AssignedProductSelection source);

  @AfterMapping
  protected ListingAggregationEntry afterMapping(@NotNull final AssignedProductSelection source,
      @MappingTarget @NotNull final ListingAggregationEntry target) {
    final var variantSelection = source.getVariantSelection();
    if (variantSelection instanceof ProductVariantSelectionIncludeOnly selection) {
      target.includedSkus(selection.getSkus());
    } else if (variantSelection instanceof ProductVariantSelectionIncludeAllExcept selection) {
      target.excludedSkus(selection.getSkus());
    }
    return target;
  }
}
