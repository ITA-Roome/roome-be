package com.roome.roome.be.domain.product.entity;

import java.util.ArrayList;
import java.util.List;

import com.roome.roome.be.common.base.BaseEntity;
import com.roome.roome.be.domain.product.enums.Category;
import com.roome.roome.be.domain.shop.entity.Shop;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Category category;

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

	public void updateName(String name) { this.name = name; }
	public void updatePrice(Integer price) { this.price = price; }
	public void updateCategory(Category category) { this.category = category; }
	public void updateDescription(String description) { this.description = description; }
	public void updateProductUrl(String productUrl) { this.productUrl = productUrl; }
	public void updateThumbnail(String thumbnailKey) { this.thumbnailKey = thumbnailKey; }
	public void incrementLikeCount() { this.likeCount++; }
	public void decrementLikeCount() {
		if (this.likeCount > 0) {
			this.likeCount--;
		}

	}
}
