package com.roome.roome.be.domain.shop.service;

import org.springframework.stereotype.Service;

import com.roome.roome.be.common.exception.GeneralException;
import com.roome.roome.be.common.status.ErrorStatus;
import com.roome.roome.be.domain.shop.dto.request.ShopRegisterRequest;
import com.roome.roome.be.domain.shop.dto.request.ShopUpdateRequest;
import com.roome.roome.be.domain.shop.dto.response.ShopDetailResponse;
import com.roome.roome.be.domain.shop.dto.response.ShopRegisterResponse;
import com.roome.roome.be.domain.shop.entity.Shop;
import com.roome.roome.be.domain.shop.repository.ShopRepository;

import jakarta.transaction.Transactional;
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
	@Transactional
	public ShopDetailResponse getShopDetail(Long shopId){
		Shop shop = shopRepository.findById(shopId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.SHOP_NOT_FOUND));
		return ShopDetailResponse.from(shop);

	}

}
