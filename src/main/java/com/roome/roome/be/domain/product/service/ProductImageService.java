package com.roome.roome.be.domain.product.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.roome.roome.be.common.exception.GeneralException;
import com.roome.roome.be.common.s3.service.S3Service;
import com.roome.roome.be.common.status.ErrorStatus;
import com.roome.roome.be.domain.product.dto.request.CommitProductImagesRequest;
import com.roome.roome.be.domain.product.entity.Product;
import com.roome.roome.be.domain.product.entity.ProductImage;
import com.roome.roome.be.domain.product.repository.ProductImageRepository;
import com.roome.roome.be.domain.product.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductImageService {

	private final S3Service s3Service;
	private final ProductRepository productRepository;
	private final ProductImageRepository productImageRepository;

	@Transactional
	public void commitSessionImages(Long productId, CommitProductImagesRequest commitProductImagesRequest) {
		Product product = productRepository.findById(productId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.PRODUCT_NOT_FOUND));

		if (commitProductImagesRequest.items() == null || commitProductImagesRequest.items().isEmpty()) return;

		Map<String, String> moveMap = new LinkedHashMap<>();
		List<ProductImage> toSave = new ArrayList<>();

		for (CommitProductImagesRequest.Item item : commitProductImagesRequest.items()) {
			String source = item.objectKey();
			String ext = source.substring(source.lastIndexOf('.') + 1);

			String destination = "products/%d/detail/%s.%s".formatted(
				product.getId(), UUID.randomUUID(), ext
			);

			moveMap.put(source, destination);
			toSave.add(
				ProductImage.builder()
					.product(product)
					.objectKey(destination)
					.sortOrder(item.order() == null ? Integer.MAX_VALUE : item.order())
					.build()
			);
		}

		s3Service.moveAll(moveMap);

		productImageRepository.saveAll(toSave);

		int thumbOrder = (commitProductImagesRequest.thumbnailOrder() == null)
			? toSave.stream().mapToInt(ProductImage::getSortOrder).min().orElse(0)
			: commitProductImagesRequest.thumbnailOrder();

		String thumbKey = toSave.stream()
			.filter(pi -> pi.getSortOrder() == thumbOrder)
			.map(ProductImage::getObjectKey)
			.findFirst()
			.orElse(toSave.get(0).getObjectKey());

		product.changeThumbnail(thumbKey);
	}
}