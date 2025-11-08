package com.roome.roome.be.domain.product.dto.request;

import java.util.List;

import org.hibernate.validator.constraints.URL;

import com.roome.roome.be.domain.product.enums.Category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record UpdateProductRequest(
	@Size(max = 50, message = "상품명은 최대 50자까지 가능합니다.")
	String name,

	@Positive(message = "가격은 0보다 커야 합니다.")
	Integer price,

	Category category,

	@Size(max = 2000, message = "상품 설명은 최대 2000자까지 가능합니다.")
	String description,

	@URL(protocol = "https", message = "상품 링크는 https URL이어야 합니다.")
	String productUrl,

	List<@Size(min=1, max=30) TagUpsertRequest>tags
){}