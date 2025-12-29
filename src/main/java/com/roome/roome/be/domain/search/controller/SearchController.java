package com.roome.roome.be.domain.search.controller;


import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.roome.roome.be.common.response.ApiResponse;
import com.roome.roome.be.common.status.SuccessStatus;
import com.roome.roome.be.domain.search.dto.response.RecentSearchListResponse;
import com.roome.roome.be.domain.search.dto.response.SearchRankingListResponse;
import com.roome.roome.be.domain.search.service.SearchService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/search")
@Tag(name = "Search")
public class SearchController {

	private final SearchService searchService;


	// 인기 검색어 리스트 조회 (검색 화면 진입 시)
	@GetMapping(value = "/keywords/popular", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(
		summary = "인기 검색어 조회",
		description = "전역 인기 검색어 Top10 목록을 조회합니다."
	)
	@io.swagger.v3.oas.annotations.responses.ApiResponse(
		responseCode = "200",
		description = "인기 검색어 조회 성공",
		content = @Content(mediaType = "application/json",
			schema = @Schema(implementation = SearchRankingListResponse.class))
	)
	public ResponseEntity<ApiResponse<SearchRankingListResponse>> getPopularKeywords() {
		SearchRankingListResponse response = searchService.getPopularKeywords();
		return ApiResponse.success(SuccessStatus.GET_POPULAR_KEYWORDS_LIST_SUCCESS, response);
	}

	//최근 검색어
	@GetMapping("/keywords/recent")
	@Operation(
		summary = "최근 검색어 조회",
		description = "로그인한 유저의 최근 검색어 10개를 조회합니다. 첫번째 키워드가 가장 최신 검색어 입니다."
	)
	@io.swagger.v3.oas.annotations.responses.ApiResponse(
		responseCode = "200",
		description = "최근 검색어 조회 성공",
		content = @Content(mediaType = "application/json",
			schema = @Schema(implementation = RecentSearchListResponse.class))
	)
	public ResponseEntity<ApiResponse<RecentSearchListResponse>> getRecentKeywords(
		@AuthenticationPrincipal Long userId
	) {
		RecentSearchListResponse response = searchService.getRecentSearchList(userId);
		return ApiResponse.success(SuccessStatus.GET_RECENT_KEYWORDS_LIST_SUCCESS, response);
	}

}