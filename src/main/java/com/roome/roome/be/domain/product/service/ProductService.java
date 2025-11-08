package com.roome.roome.be.domain.product.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.roome.roome.be.domain.product.enums.TagType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.roome.roome.be.common.exception.GeneralException;
import com.roome.roome.be.common.s3.service.ImageUrlBuilder;
import com.roome.roome.be.common.s3.service.S3Service;
import com.roome.roome.be.common.status.ErrorStatus;
import com.roome.roome.be.domain.product.dto.request.RegisterProductRequest;
import com.roome.roome.be.domain.product.dto.request.UpdateProductRequest;
import com.roome.roome.be.domain.product.dto.response.ProductDetailResponse;
import com.roome.roome.be.domain.product.dto.response.ProductImageResponse;
import com.roome.roome.be.domain.product.dto.response.ProductListItemResponse;
import com.roome.roome.be.domain.product.dto.response.ProductTagResponse;
import com.roome.roome.be.domain.product.entity.Product;
import com.roome.roome.be.domain.product.enums.Category;
import com.roome.roome.be.domain.product.repository.ProductImageRepository;
import com.roome.roome.be.domain.product.repository.ProductRepository;
import com.roome.roome.be.domain.product.repository.ProductTagRepository;
import com.roome.roome.be.domain.shop.dto.response.ShopSummaryResponse;
import com.roome.roome.be.domain.shop.repository.ShopRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

	private final ProductRepository productRepository;
	private final ShopRepository shopRepository;
	private final ProductImageRepository productImageRepository;
	private final ProductTagRepository productTagRepository;

	private final ProductTagService productTagService;
	private final ProductImageService productImageService;
	private final S3Service s3Service;
	private final ImageUrlBuilder imageUrlBuilder;

	// 상품 등록
	@Transactional
	public Long register(RegisterProductRequest registerProductRequest) {
		Product product = productRepository.save(Product.builder()
			.name(registerProductRequest.name())
			.price(registerProductRequest.price())
			.category(registerProductRequest.category())
			.productUrl(registerProductRequest.productUrl())
			.description(registerProductRequest.description())
			.shop(shopRepository.findById(registerProductRequest.shopId())
				.orElseThrow(() -> new GeneralException(ErrorStatus.SHOP_NOT_FOUND)))
			.build());

		if (registerProductRequest.images() != null) {
			productImageService.commitSessionImages(product.getId(), registerProductRequest.images());
		}

		productTagService.applyTags(product.getId(), registerProductRequest.tags());
		return product.getId();
	}

	// 상품 상세 조회
	public ProductDetailResponse getDetail(Long productId) {
		Product product = productRepository.findById(productId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.PRODUCT_NOT_FOUND));

		var images = productImageRepository.findByProductIdOrderBySortOrder(productId).stream()
			.map(productImage -> new ProductImageResponse(
				productImage.getObjectKey(),
				imageUrlBuilder.build(productImage.getObjectKey()),
				productImage.getSortOrder()
			))
			.toList();

		String thumbnailUrl = (product.getThumbnailKey() != null)
			? imageUrlBuilder.build(product.getThumbnailKey())
			: (images.isEmpty() ? null : images.get(0).url());

		var tags = productTagRepository.findByProductIdWithTag(productId).stream()
			.map(productTag -> new ProductTagResponse(
				productTag.getTag().getId(),
				productTag.getTag().getType(),
				productTag.getTag().getName()))
			.toList();

		var shop = new ShopSummaryResponse(product.getShop().getId(), product.getShop().getName());

		return new ProductDetailResponse(
			product.getId(),
			product.getName(),
			product.getPrice(),
			product.getProductUrl(),
			product.getCategory().name(),
			product.getDescription(),
			shop,
			thumbnailUrl,
			images,
			tags
		);
	}

	// 목록 조회
	@Transactional(readOnly = true)
	public Page<ProductListItemResponse> getList(
			Long shopId,                 // 가게 필터
			Category category,
			List<String> colorTags,
			List<String> materialTags,
			List<String> styleTags,
			List<String> featureTags,
			List<String> moodTags,
			String match,                // 기본 any
			String keyWord,
			Integer minPrice,
			Integer maxPrice,
			Pageable pageable
	) {
		// [수정 3] match 문자열 정규화
		final String normalizedMatch = (match == null) ? "any" : match.trim().toLowerCase();

		// [수정 4] 모든 태그 파라미터를 Map으로 조립
		Map<TagType, List<String>> tagFilters = new HashMap<>();
		if (colorTags != null && !colorTags.isEmpty()) tagFilters.put(TagType.COLOR, colorTags);
		if (materialTags != null && !materialTags.isEmpty()) tagFilters.put(TagType.MATERIAL, materialTags);
		if (styleTags != null && !styleTags.isEmpty()) tagFilters.put(TagType.STYLE, styleTags);
		if (featureTags != null && !featureTags.isEmpty()) tagFilters.put(TagType.FEATURE, featureTags);
		if (moodTags != null && !moodTags.isEmpty()) tagFilters.put(TagType.MOOD, moodTags);

		Page<Product> page = productRepository.findByDynamicFilters(
				shopId,
				category,
				keyWord,
				minPrice,
				maxPrice,
				tagFilters,
				normalizedMatch,
				pageable
		);

		return page.map(p -> ProductListItemResponse.from(p, imageUrlBuilder));

	}

	// 상품 수정
	@Transactional
	public void updateProduct(Long productId, UpdateProductRequest updateProductRequest) {
		Product product = productRepository.findById(productId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.PRODUCT_NOT_FOUND));

		if (updateProductRequest.name() != null) product.updateName(updateProductRequest.name());
		if (updateProductRequest.price() != null) product.updatePrice(updateProductRequest.price());
		if (updateProductRequest.category() != null) product.updateCategory(updateProductRequest.category());
		if (updateProductRequest.productUrl() != null) product.updateProductUrl(updateProductRequest.productUrl());
		if (updateProductRequest.description() != null) product.updateDescription(updateProductRequest.description());

		if (updateProductRequest.tags() != null) {
			productTagService.applyTags(product.getId(), updateProductRequest.tags());
		}
	}

	// 상품 삭제
	@Transactional
	public void deleteProduct(Long productId) {
		Product product = productRepository.findById(productId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.PRODUCT_NOT_FOUND));

		List<String> objectKeys = productImageRepository.findByProductIdOrderBySortOrder(productId)
			.stream()
			.map(productImage -> productImage.getObjectKey())
			.toList();
		String thumbnailKey = product.getThumbnailKey();

		productImageRepository.deleteByProductId(productId);
		productTagRepository.deleteByProductId(productId);
		productRepository.delete(product);

		TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
			@Override public void afterCommit() {
				try {
					if (thumbnailKey != null) s3Service.deleteObject(thumbnailKey);
					for (String key : objectKeys) {
						s3Service.deleteObject(key);
					}
				} catch (Exception e) {
					log.warn("S3 delete failed for productId={}", productId, e);
				}
			}
		});
	}
}
