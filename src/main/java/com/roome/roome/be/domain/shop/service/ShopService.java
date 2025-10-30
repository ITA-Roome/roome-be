package com.roome.roome.be.domain.shop.service;

import org.springframework.stereotype.Service;

import com.roome.roome.be.domain.shop.dto.request.ShopRegisterRequest;
import com.roome.roome.be.domain.shop.dto.response.ShopRegisterResponse;
import com.roome.roome.be.domain.shop.entity.Shop;
import com.roome.roome.be.domain.shop.repository.ShopRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ShopService {

	private final ShopRepository shopRepository;

	@Transactional
	public ShopRegisterResponse registerShop(ShopRegisterRequest shopRegisterRequest) {
		Shop shop = Shop.builder()
			.name(shopRegisterRequest.name())
			.build();

		Shop savedShop = shopRepository.save(shop);
		return ShopRegisterResponse.from(savedShop);
	}
}
