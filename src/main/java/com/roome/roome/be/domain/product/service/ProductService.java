package com.roome.roome.be.domain.product.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.roome.roome.be.common.exception.GeneralException;
import com.roome.roome.be.common.s3.service.ImageUrlBuilder;
import com.roome.roome.be.common.status.ErrorStatus;
import com.roome.roome.be.domain.product.dto.request.RegisterProductRequest;
import com.roome.roome.be.domain.product.dto.response.ProductDetailResponse;
import com.roome.roome.be.domain.product.dto.response.ProductImageResponse;
import com.roome.roome.be.domain.product.dto.response.ProductTagResponse;
import com.roome.roome.be.domain.product.entity.Product;
import com.roome.roome.be.domain.product.repository.ProductImageRepository;
import com.roome.roome.be.domain.product.repository.ProductRepository;
import com.roome.roome.be.domain.product.repository.ProductTagRepository;
import com.roome.roome.be.domain.shop.dto.response.ShopSummaryResponse;
import com.roome.roome.be.domain.shop.repository.ShopRepository;

import lombok.RequiredArgsConstructor;

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
	private final ImageUrlBuilder imageUrlBuilder;

	//상품 등록
	@Transactional
	public Long register(RegisterProductRequest registerProductRequest) {
	Product product = productRepository.save(Product.builder()
		.name(registerProductRequest.name())
		.price(registerProductRequest.price())
		.category(registerProductRequest.category())
		.color(registerProductRequest.color())
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

   //상품 상세 조회
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
		   .map(productTag -> new ProductTagResponse(productTag.getTag().getId(), productTag.getTag().getName()))
		   .toList();

	   var shop = new ShopSummaryResponse(product.getShop().getId(), product.getShop().getName());

	   return new ProductDetailResponse(
		   product.getId(),
		   product.getName(),
		   product.getPrice(),
		   product.getCategory().name(),
		   product.getColor().name(),
		   product.getDescription(),
		   shop,
		   thumbnailUrl,
		   images,
		   tags
	   );
    }
}