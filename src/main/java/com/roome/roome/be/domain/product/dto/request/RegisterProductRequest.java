package com.roome.roome.be.domain.product.dto.request;

import java.util.List;

import com.roome.roome.be.domain.product.enums.Category;
import org.hibernate.validator.constraints.URL;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record RegisterProductRequest(
	@NotBlank(message = "상품명은 필수입니다.")
	@Size(max = 50, message = "상품명은 최대 50자까지 가능합니다.")
	String name,

	@NotNull(message = "가격은 필수입니다.")
	@Positive(message = "가격은 0보다 커야 합니다.")
	Integer price,

	@NotNull(message = "카테고리는 필수입니다.")
	Category category,

	@Size(max = 2000, message = "상품 설명은 최대 2000자까지 가능합니다.")
	@NotNull(message = "상품설명은 필수입니다.")
	String description,

	@URL(protocol = "https", message = "상품 링크는 https URL이어야 합니다.")
	@NotNull(message = "상품링크는 필수입니다.")
	String productUrl,

	@NotNull(message = "샵 ID는 필수입니다.")
	Long shopId,

	List<TagUpsertRequest>tags,

	@NotNull(message = "상품이미지는 필수입니다.")
	RegisterProductImagesRequest images
) { }
