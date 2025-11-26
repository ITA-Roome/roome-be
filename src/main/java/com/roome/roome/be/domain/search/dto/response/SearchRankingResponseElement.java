package com.roome.roome.be.domain.search.dto.response;

public record SearchRankingResponseElement(
	int rank,
	String keyword,
	double score
) {
	public static SearchRankingResponseElement of(int rank, String keyword, Double score) {
		return new SearchRankingResponseElement(rank, keyword, score == null ? 0.0 : score);
	}
}