package com.roome.roome.be.domain.shop.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.roome.roome.be.common.exception.GeneralException;
import com.roome.roome.be.common.s3.service.ImageUrlBuilder;
import com.roome.roome.be.common.s3.service.S3Service;
import com.roome.roome.be.common.status.ErrorStatus;
import com.roome.roome.be.domain.shop.dto.request.ShopRegisterRequest;
import com.roome.roome.be.domain.shop.dto.request.ShopUpdateRequest;
import com.roome.roome.be.domain.shop.dto.response.ShopDetailResponse;
import com.roome.roome.be.domain.shop.dto.response.ShopListResponse;
import com.roome.roome.be.domain.shop.dto.response.ShopRegisterResponse;
import com.roome.roome.be.domain.shop.dto.response.ShopSummaryResponse;
import com.roome.roome.be.domain.shop.entity.Shop;
import com.roome.roome.be.domain.shop.repository.ShopRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ShopService {

	private final ShopRepository shopRepository;
	private final S3Service s3Service;
	private final ImageUrlBuilder imageUrlBuilder;

	@Value("${storage.defaults.shop-logo}")
	private String defaultShopLogoUrl;

	//가게 등록
	@Transactional
	public ShopRegisterResponse registerShop(ShopRegisterRequest shopRegisterRequest) {
		Shop shop = shopRepository.save(Shop.builder()
			.name(shopRegisterRequest.name())
				.description(shopRegisterRequest.description())
			.build());

		commitLogoIfPresent(shop, shopRegisterRequest.logoObjectKey());

		String logoUrl = (shop.getLogoObjectKey() != null && !shop.getLogoObjectKey().isBlank())
			? imageUrlBuilder.build(shop.getLogoObjectKey())
			: defaultShopLogoUrl;

		return ShopRegisterResponse.from(shop, logoUrl);
	}

	//가게 수정
	@Transactional
	public void updateShop(Long shopId, ShopUpdateRequest shopUpdateRequest) {
		Shop shop = shopRepository.findById(shopId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.SHOP_NOT_FOUND));

		if (shopUpdateRequest.name() != null) shop.updateName(shopUpdateRequest.name());
		if (shopUpdateRequest.description() != null) shop.updateDescription(shopUpdateRequest.description());
		commitLogoIfPresent(shop, shopUpdateRequest.logoObjectKey());
	}


	//가게 삭제
	@Transactional
	public void deleteShop(Long shopId) {
		Shop shop = shopRepository.findById(shopId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.SHOP_NOT_FOUND));

		shopRepository.delete(shop);
	}

	//가게 상세 조회, 추후 상세 내용 보완
	@Transactional(readOnly=true)
	public ShopDetailResponse getShopDetail(Long shopId) {
		Shop shop = shopRepository.findById(shopId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.SHOP_NOT_FOUND));

		String logoUrl = (shop.getLogoObjectKey() != null && !shop.getLogoObjectKey().isBlank())
			? imageUrlBuilder.build(shop.getLogoObjectKey())
			: defaultShopLogoUrl; // 기본 이미지

		return ShopDetailResponse.from(shop, logoUrl);
	}

	//가게 목록 조회
	@Transactional(readOnly = true)
	public ShopListResponse getShopList(int page, int size, String name) {
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

		Page<Shop> shops;
		if (name != null && !name.isBlank()) {
			shops = shopRepository.findByNameContaining(name, pageable);
		} else {
			shops = shopRepository.findAll(pageable);
		}

		List<ShopSummaryResponse> content = shops.stream()
			.map(shop -> {
				String logoUrl = (shop.getLogoObjectKey() != null && !shop.getLogoObjectKey().isBlank())
					? imageUrlBuilder.build(shop.getLogoObjectKey())
					: defaultShopLogoUrl;
				return ShopSummaryResponse.from(shop, logoUrl);
			})
			.toList();

		return new ShopListResponse(
			content,
			shops.getNumber(),
			shops.getSize(),
			shops.getTotalElements()
		);
	}

	private void commitLogoIfPresent(Shop shop, String logoObjectKey) {
		if (logoObjectKey == null || logoObjectKey.isBlank()) return;

		String source = logoObjectKey;
		String ext = source.substring(source.lastIndexOf('.') + 1);
		String dest = "shops/%d/profile/%s.%s".formatted(
			shop.getId(), java.util.UUID.randomUUID(), ext
		);

		s3Service.moveAll(java.util.Map.of(source, dest));
		shop.updateLogoObjectKey(dest);
	}
}
