package com.mindcurv.b2x.productcatalog.aggregator.helper;

import static com.mindcurv.b2x.commons.DefaultsConstants.PATH_SEPARATOR;
import static lombok.AccessLevel.PRIVATE;
import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static java.lang.String.format;

import com.mindcurv.b2x.commons.models.B2xContext;
import com.mindcurv.b2x.commons.models.ImageAggregation;
import com.mindcurv.b2x.commons.models.URLMetaData;
import com.mindcurv.b2x.productcatalog.aggregator.models.AssetAggregation;
import com.mindcurv.b2x.productcatalog.aggregator.models.CategoryAggregation;
import com.mindcurv.b2x.productcatalog.aggregator.models.CategoryNavigationAggregation;
import java.util.Collection;
import java.util.Optional;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.MapUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mapstruct.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@NoArgsConstructor(access = PRIVATE)
public final class CatalogHelper {

  private static final Logger LOG = LoggerFactory.getLogger(CatalogHelper.class);

  public static final String MAINNAV_KEY_PATTERN = "tree-%s-%s-%s-%s-%s";
  public static final String CATAGORY_KEY_PATTERN = "category-%s-%s-%s-%s-%s-%s-%s";
  public static final String CATEGORY_TREE_CACHE_KEY = "categoryTree";
  private static final String PATTERN_KEY = "assets";

  @Nullable
  @Named("processUri")
  public static String processUri(@Nullable final String uri, @NotNull final String prefix) {
    if (isBlank(uri)) {
      return null;
    }
    if (uri.contains("://")) {
      return uri;
    }
    final var builder = new StringBuilder(prefix);
    if (!builder.toString().endsWith(PATH_SEPARATOR) && !uri.startsWith(PATH_SEPARATOR)) {
      builder.append(PATH_SEPARATOR);
    }
    return builder.append(uri).toString();
  }

  @NotNull
  public static String determineAssetsPrefix(@NotNull final URLMetaData urlMetaData) {
    return Optional.ofNullable(MapUtils.emptyIfNull(urlMetaData.getPatterns()).get(PATTERN_KEY))
        .orElse(EMPTY);
  }

  @NotNull
  public static Optional<CategoryAggregation> processImageForCategory(
      @Nullable final CategoryAggregation category, @NotNull final String prefix) {
    if (category == null) {
      return Optional.empty();
    }
    Optional.ofNullable(category.getImage()).ifPresent(
        imageAggregation -> category.getImage().url(processUri(imageAggregation.getUrl(), prefix)));
    emptyIfNull(category.getChildren()).forEach(
        child -> processImageForCategoryNavigationAggregation(child, prefix));
    emptyIfNull(category.getAssets()).forEach(asset -> emptyIfNull(asset.getSources()).forEach(
        assetSource -> assetSource.setUri(processUri(assetSource.getUri(), prefix))));
    return Optional.of(category);
  }

  public static void processImageForCategoryNavigationAggregation(
      @NotNull final CategoryNavigationAggregation category, @NotNull final String prefix) {
    Optional.ofNullable(category.getImage()).ifPresent(
        imageAggregation -> category.getImage().url(processUri(imageAggregation.getUrl(), prefix)));
    emptyIfNull(category.getChildren()).forEach(
        child -> processImageForCategoryNavigationAggregation(child, prefix));
  }

  @NotNull
  public static Optional<ImageAggregation> processImageFromAssets(
      @NotNull final Collection<AssetAggregation> assetAggregations) {
    for (final var asset : emptyIfNull(assetAggregations)) {
      final var source = emptyIfNull(asset.getSources()).stream()
          .filter(assetSource -> isNotBlank(assetSource.getUri()))
          .findFirst();
      if (source.isPresent()) {
        return Optional.of(new ImageAggregation()
            .url(source.get().getUri())
            .height(source.get().getHeight())
            .width(source.get().getWidth())
            .label(source.get().getKey()));
      }
    }
    return Optional.empty();
  }

  @NotNull
  public static String getMainNavigationKey(@NotNull final B2xContext context) {
    return format(MAINNAV_KEY_PATTERN, context.getCurrentLanguage(), context.getStore(),
        context.getCompanyId(), context.getActiveUnit(), context.getCustomerId());
  }

  @NotNull
  public static String getCategoryCacheKey(@NotNull final B2xContext context,
      @NotNull final String keyPart, @NotNull final String identifier) {
    final var cacheKey = format(CATAGORY_KEY_PATTERN, keyPart, identifier,
        context.getCurrentLanguage(), context.getStore(),
        context.getCompanyId(), context.getActiveUnit(), context.getCustomerId());
    LOG.info("getCategoryCacheKey :: cache key: {}", cacheKey);
    return cacheKey;
  }
}
