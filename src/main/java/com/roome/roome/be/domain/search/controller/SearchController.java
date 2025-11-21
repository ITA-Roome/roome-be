package com.roome.roome.be.domain.search.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.roome.roome.be.common.response.ApiResponse;
import com.roome.roome.be.common.status.SuccessStatus;
import com.roome.roome.be.domain.search.dto.request.SearchRequest;
import com.roome.roome.be.domain.search.dto.response.SearchRankingListResponse;
import com.roome.roome.be.domain.search.service.SearchService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/search")
public class SearchController {

	private final SearchService searchService;

	// 검색
	@PostMapping("/keywords")
	@Operation(
		summary = "검색어 기록",
		description = "검색 발생 시 인기·최근 검색어로 기록합니다."
	)
	@io.swagger.v3.oas.annotations.responses.ApiResponse(
		responseCode = "200",
		description = "검색어 기록 성공",
		content = @Content(mediaType = "application/json", schema = @Schema())
	)
	public ResponseEntity<ApiResponse<Void>> recordKeyword(
		@AuthenticationPrincipal Long userId,
		@RequestBody SearchRequest request
	) {
		searchService.recordSearch(request.keyword(), userId);
		return ApiResponse.success(SuccessStatus.RECORD_SEARCH_KEYWORD_SUCCESS);
	}


	// 인기 검색어
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

}