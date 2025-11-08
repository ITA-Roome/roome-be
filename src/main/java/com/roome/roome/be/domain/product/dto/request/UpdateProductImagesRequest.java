package com.roome.roome.be.domain.product.dto.request;

import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateProductImagesRequest(

	Long sessionId,
	@NotNull(message = "이미지 목록은 필수입니다.")
	@Size(min = 1, max = 30, message = "이미지는 1~30장까지 등록할 수 있습니다.")
	List<Item> items,

	@Min(value = 0, message = "thumbnailOrder는 0 이상이어야 합니다.")
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