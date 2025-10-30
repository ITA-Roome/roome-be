package com.roome.roome.be.domain.product.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Color {
	WHITE("화이트"),
	BLACK("블랙"),
	GRAY("그레이"),
	BEIGE("베이지"),
	BROWN("브라운"),
	IVORY("아이보리"),
	CREAM("크림"),
	WOOD("우드"),
	BLUE("블루"),
	NAVY("네이비"),
	GREEN("그린"),
	OLIVE("올리브"),
	YELLOW("옐로우"),
	ORANGE("오렌지"),
	RED("레드"),
	PINK("핑크"),
	PURPLE("퍼플"),
	GOLD("골드"),
	SILVER("실버"),
	TRANSPARENT("투명");

	private final String description;
}