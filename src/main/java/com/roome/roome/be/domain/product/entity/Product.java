package com.roome.roome.be.domain.product.entity;

import java.util.ArrayList;
import java.util.List;

import com.roome.roome.be.common.base.BaseEntity;
import com.roome.roome.be.domain.product.enums.ProductCategory;
import com.roome.roome.be.domain.shop.entity.Shop;

import jakarta.persistence.*;
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

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private Integer price;

	@Column(columnDefinition = "TEXT")
	private String description;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "shop_id", nullable = false)
	private Shop shop;

	@Column(length = 512)
	private String thumbnailKey;

	@Column(name = "product_url", length = 1024, nullable = false)
	private String productUrl;

	@OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
	private List<ProductTag> productTagList = new ArrayList<>();

	@OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
	private List<ProductImage> productImageList = new ArrayList<>();

	@Column(name = "like_count",nullable = false)
	private Integer likeCount;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ProductCategory category;

	public void updateName(String name) { this.name = name; }
	public void updatePrice(Integer price) { this.price = price; }
	public void updateDescription(String description) { this.description = description; }
	public void updateCategory(ProductCategory category) { this.category = category; }
	public void updateProductUrl(String productUrl) { this.productUrl = productUrl; }
	public void updateThumbnail(String thumbnailKey) { this.thumbnailKey = thumbnailKey; }
	public void incrementLikeCount() { this.likeCount++; }
	public void decrementLikeCount() {
		if (this.likeCount > 0) {
			this.likeCount--;
		}

	}
}
