package com.roome.roome.be.domain.product.dto.request;

import com.roome.roome.be.domain.product.enums.TagType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TagUpsertRequest(
	@NotBlank String name,
	@NotNull TagType type
) { }