package com.roome.roome.be.domain.product.dto.request;

import java.util.List;

import com.roome.roome.be.domain.product.enums.Category;
import com.roome.roome.be.domain.product.enums.Color;


import jakarta.validation.constraints.Size;

public record UpdateProductRequest(
	String name,
	Integer price,
	Category category,
	Color color,
	String description,
	List<@Size(min=1, max=30) String> tagNames
){}