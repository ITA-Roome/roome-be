package com.roome.roome.be.domain.product.entity;

import com.roome.roome.be.common.base.BaseEntity;
import com.roome.roome.be.domain.product.enums.Category;
import com.roome.roome.be.domain.product.enums.Color;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Product extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 50)
	private String name;

	@Column(nullable = false)
	private Integer price;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Category category;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Color color;

	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<ProductTag> productTags = new ArrayList<>();
}
