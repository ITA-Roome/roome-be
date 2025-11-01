package com.roome.roome.be.domain.shop.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.roome.roome.be.common.exception.GeneralException;
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

	//가게 등록
	@Transactional
	public ShopRegisterResponse registerShop(ShopRegisterRequest shopRegisterRequest) {
		Shop shop = Shop.builder()
			.name(shopRegisterRequest.name())
			.build();

		Shop savedShop = shopRepository.save(shop);
		return ShopRegisterResponse.from(savedShop);
	}

	//가게 이름 수정
	@Transactional
	public void updateShop(Long shopId, ShopUpdateRequest shopUpdateRequest) {
		Shop shop = shopRepository.findById(shopId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.SHOP_NOT_FOUND));

		if (shopUpdateRequest.name() != null) {
			shop.updateName(shopUpdateRequest.name());
		}
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
	public ShopDetailResponse getShopDetail(Long shopId){
		Shop shop = shopRepository.findById(shopId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.SHOP_NOT_FOUND));
		return ShopDetailResponse.from(shop);

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

		List<ShopSummaryResponse> content = shops
			.stream()
			.map(ShopSummaryResponse::from)
			.toList();

		return new ShopListResponse(
			content,
			shops.getNumber(),
			shops.getSize(),
			shops.getTotalElements()
		);
	}

}
