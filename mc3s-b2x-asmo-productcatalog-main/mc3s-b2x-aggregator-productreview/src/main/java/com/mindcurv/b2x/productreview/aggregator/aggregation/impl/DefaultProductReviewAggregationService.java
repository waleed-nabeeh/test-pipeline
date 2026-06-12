package com.mindcurv.b2x.productreview.aggregator.aggregation.impl;

import static com.mindcurv.b2x.commons.models.Pageable.convertPageable;
import static com.mindcurv.b2x.commons.models.Pageable.initPageableFromList;
import static com.mindcurv.b2x.connector.helper.QueryHelper.createFilterOption;
import static com.mindcurv.b2x.connector.helper.QueryHelper.getFilterOption;
import static com.mindcurv.b2x.connector.models.ItemType.CUSTOMER;

import com.mindcurv.b2x.commons.models.B2xContext;
import com.mindcurv.b2x.commons.models.Pageable;
import com.mindcurv.b2x.commons.models.PageableSearchRequest;
import com.mindcurv.b2x.commons.resolver.NullableBaseResolver;
import com.mindcurv.b2x.connector.adapter.DTOCreateAdapter;
import com.mindcurv.b2x.connector.adapter.DTOReadAdapter;
import com.mindcurv.b2x.connector.models.ReferenceDTO;
import com.mindcurv.b2x.connector.product.models.ProductDTO;
import com.mindcurv.b2x.connector.review.models.ReviewDTO;
import com.mindcurv.b2x.connector.review.models.ReviewDTOCreateRequest;
import com.mindcurv.b2x.productcatalog.review.models.ProductReviewAggregation;
import com.mindcurv.b2x.productcatalog.review.models.ProductReviewCreateRequest;
import com.mindcurv.b2x.productcatalog.review.models.ProductReviewSummary;
import com.mindcurv.b2x.productcatalog.review.openapi.PageableOfProductReviewAggregation;
import com.mindcurv.b2x.productreview.aggregator.aggregation.ProductReviewAggregationService;
import com.mindcurv.b2x.productreview.aggregator.converter.ProductReviewAggregationReviewDTOConverter;
import com.mindcurv.b2x.productreview.aggregator.converter.ProductReviewSummaryProductDTOConverter;
import com.mindcurv.b2x.productreview.aggregator.converter.ReviewDTOCreateRequestProductReviewCreateRequestConverter;
import jakarta.validation.Valid;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DefaultProductReviewAggregationService implements ProductReviewAggregationService {

  private static final Logger LOG = LoggerFactory.getLogger(
      DefaultProductReviewAggregationService.class);

  @NotNull
  private final NullableBaseResolver<ProductDTO> productDTOResolver;
  @NotNull
  private final DTOReadAdapter<ReviewDTO> reviewReadAdapter;
  @NotNull
  private final DTOCreateAdapter<ReviewDTO, ReviewDTOCreateRequest> reviewCreateAdapter;
  @NotNull
  private final ProductReviewAggregationReviewDTOConverter productReviewAggregationReviewDTOConverter;
  @NotNull
  private final ProductReviewSummaryProductDTOConverter productReviewSummaryProductDTOConverter;
  @NotNull
  private final ReviewDTOCreateRequestProductReviewCreateRequestConverter reviewDTOCreateRequestProductReviewCreateRequestConverter;

  @Autowired
  public DefaultProductReviewAggregationService(
      @NotNull final NullableBaseResolver<ProductDTO> productDTOResolver,
      @NotNull final DTOReadAdapter<ReviewDTO> reviewReadAdapter,
      @NotNull final DTOCreateAdapter<ReviewDTO, ReviewDTOCreateRequest> reviewCreateAdapter,
      @NotNull final ProductReviewAggregationReviewDTOConverter productReviewAggregationReviewDTOConverter,
      @NotNull final ProductReviewSummaryProductDTOConverter productReviewSummaryProductDTOConverter,
      @NotNull final ReviewDTOCreateRequestProductReviewCreateRequestConverter reviewDTOCreateRequestProductReviewCreateRequestConverter) {
    this.productDTOResolver = productDTOResolver;
    this.reviewReadAdapter = reviewReadAdapter;
    this.reviewCreateAdapter = reviewCreateAdapter;
    this.productReviewAggregationReviewDTOConverter = productReviewAggregationReviewDTOConverter;
    this.productReviewSummaryProductDTOConverter = productReviewSummaryProductDTOConverter;
    this.reviewDTOCreateRequestProductReviewCreateRequestConverter = reviewDTOCreateRequestProductReviewCreateRequestConverter;
  }

  @Override
  @NotNull
  public Optional<ProductReviewAggregation> createProductReview(
      @NotNull @Valid final ProductReviewCreateRequest request, @NotNull final B2xContext context) {
    return reviewDTOCreateRequestProductReviewCreateRequestConverter.convertAsOptional(request,
        context).flatMap(createRequest -> reviewCreateAdapter.createAsOptional(createRequest)
        .flatMap(productReviewAggregationReviewDTOConverter::convertAsOptional));
  }

  @Override
  @NotNull
  public Optional<ProductReviewAggregation> findReviewById(@NotNull final String id,
      @NotNull final B2xContext context) {
    return reviewReadAdapter.findById(id)
        .flatMap(productReviewAggregationReviewDTOConverter::convertAsOptional);
  }

  @Override
  @NotNull
  public Optional<ProductReviewAggregation> findReviewByKey(@NotNull final String key,
      @NotNull final B2xContext context) {
    return reviewReadAdapter.findByKey(key)
        .flatMap(productReviewAggregationReviewDTOConverter::convertAsOptional);
  }

  @Override
  @NotNull
  public Optional<ProductReviewSummary> getProductReviewSummaryByProductKey(
      @NotNull final String key,
      @NotNull final Long offset, @NotNull final Long limit,
      @NotNull final B2xContext context) {
    return productDTOResolver.resolve(key)
        .map(dto -> fetchProductReviewSummary(dto, offset, limit));
  }

  @Override
  @NotNull
  public Optional<ProductReviewSummary> getProductReviewSummaryByProductId(
      @NotNull final String id,
      @NotNull final Long offset, @NotNull final Long limit,
      @NotNull final B2xContext context) {
    return productDTOResolver.resolve(id).map(dto -> fetchProductReviewSummary(dto, offset, limit));
  }

  @Override
  @NotNull
  public Pageable<ProductReviewAggregation> getMyProductReviews(
      @NotNull final B2xContext context) {
    final var searchRequest = new PageableSearchRequest();
    createFilterOption(CUSTOMER.getValue(),
        new ReferenceDTO().id(context.getCustomerId()).type(CUSTOMER.getValue()))
        .ifPresent(searchRequest::addFilterItem);
    return processResult(reviewReadAdapter.getItems(searchRequest));
  }

  @NotNull
  protected ProductReviewSummary fetchProductReviewSummary(
      @NotNull @Valid final ProductDTO productDTO, Long offset, Long limit) {
    LOG.info("fetchProductReviewSummary :: product '{}'", productDTO.getKey());
    final var searchRequest = new PageableSearchRequest().offset(offset).limit(limit);
      createFilterOption("target", new ReferenceDTO().id(productDTO.getId()).type("product"))
        .ifPresent(searchRequest::addFilterItem);
    final var summary = productReviewSummaryProductDTOConverter.convertAsOptional(productDTO);
    return summary.isPresent() ? summary.get().reviews(
        processResult(reviewReadAdapter.getItems(searchRequest))) : new ProductReviewSummary();
  }

  @NotNull
  protected PageableOfProductReviewAggregation processResult(
      @NotNull @Valid final Pageable<ReviewDTO> productReviewDTOPageable) {
    LOG.info("processResult :: {}", productReviewDTOPageable);
    final var pageable = convertPageable(initPageableFromList(
        productReviewAggregationReviewDTOConverter.convertList(
            productReviewDTOPageable.getResults())), new PageableOfProductReviewAggregation());
    pageable.setTotal(productReviewDTOPageable.getTotal())
        .setCount(productReviewDTOPageable.getCount())
        .setTotalPages(productReviewDTOPageable.getTotalPages());
    return pageable;
  }


}
