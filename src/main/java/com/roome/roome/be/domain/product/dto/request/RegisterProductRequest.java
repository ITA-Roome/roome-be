package com.roome.roome.be.domain.product.dto.request;

import java.util.List;

import com.roome.roome.be.domain.product.enums.Category;
import com.roome.roome.be.domain.product.enums.Color;

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

	@NotNull(message = "색상은 필수입니다.")
	Color color,

	@Size(max = 2000, message = "상품 설명은 최대 2000자까지 가능합니다.")
	String description,

	@NotNull(message = "샵 ID는 필수입니다.")
	Long shopId,

	List<@Size(min=1, max=30) String>tags,
	// 이미지 커밋용 DTO
    @NotNull
	CommitProductImagesRequest images
) { }
