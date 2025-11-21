package com.roome.roome.be.domain.search.dto.response;

public record SearchRankingListResponse(
	java.util.List<SearchRankingResponseElement> rankings
) {
	public static SearchRankingListResponse from(java.util.List<SearchRankingResponseElement> list) {
		return new SearchRankingListResponse(list);
	}
}
