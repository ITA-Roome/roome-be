package com.roome.roome.be.domain.product.entity;

import com.roome.roome.be.common.base.BaseEntity;
import com.roome.roome.be.domain.shop.enums.Category;
import com.roome.roome.be.domain.shop.enums.Color;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
	private int price;

	@Column(nullable = false)
	private Category category;

	@Column(nullable = false)
	private Color color;

}
