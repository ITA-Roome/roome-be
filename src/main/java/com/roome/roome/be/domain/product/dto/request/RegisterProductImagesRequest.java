package com.roome.roome.be.domain.product.dto.request;

import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterProductImagesRequest(
	@NotBlank(message = "세션id는 필수입니다.")
	Long sessionId,

	@NotBlank(message = "상품 이미지 정보은 필수입니다.")
	List<Item> items,

	@NotBlank(message = "썸네일 사진의 순서는 필수입니다.")
	Integer thumbnailOrder
) {
	public record Item(
		@NotBlank(message = "objectKey는 필수입니다.")
		@Size(max = 1024, message = "objectKey는 최대 1024자까지 가능합니다.")
		String objectKey,

		@NotNull(message = "order는 필수입니다.")
		@Min(value = 0, message = "order는 0 이상이어야 합니다.")
		Integer order
	) {}
}
