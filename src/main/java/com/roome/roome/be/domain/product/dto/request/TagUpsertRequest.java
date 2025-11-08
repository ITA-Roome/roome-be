package com.roome.roome.be.domain.product.dto.request;

import com.roome.roome.be.domain.product.enums.TagType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TagUpsertRequest(
	@NotBlank(message ="태그명은 필수입니다.")
	String name,

	@NotNull(message = "태그 타입은 필수입니다.")
	TagType type
) { }