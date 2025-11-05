package com.roome.roome.be.domain.product.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.roome.roome.be.common.exception.GeneralException;
import com.roome.roome.be.common.status.ErrorStatus;
import com.roome.roome.be.domain.product.dto.request.RegisterProductRequest;
import com.roome.roome.be.domain.product.entity.Product;
import com.roome.roome.be.domain.product.repository.ProductRepository;
import com.roome.roome.be.domain.shop.repository.ShopRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

	private final ProductRepository productRepository;
	private final ShopRepository shopRepository;
	private final ProductTagService productTagService;
	private final ProductImageService productImageService;

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
}