package com.roome.roome.be.domain.search.service;

import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.roome.roome.be.domain.search.dto.response.SearchRankingListResponse;
import com.roome.roome.be.domain.search.dto.response.SearchRankingResponseElement;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class SearchService {

	private final StringRedisTemplate redisTemplate;

	private static final String POPULAR_KEYWORDS_ZSET_KEY = "search:popularRank:global";
	private static final double RANKING_INCREMENT_SCORE = 1;
	private static final long RANKING_START = 0L;
	private static final long RANKING_END = 9L;

	//키워드 검색
	public void recordSearch(String rawKeyword, Long userId) {

		if (rawKeyword == null || rawKeyword.isBlank()) return;

		String keyword = normalize(rawKeyword);

		redisTemplate.opsForZSet()
			.incrementScore(POPULAR_KEYWORDS_ZSET_KEY, keyword, RANKING_INCREMENT_SCORE);

	}

	// 인기 검색어
	@Transactional(readOnly = true)
	public SearchRankingListResponse getPopularKeywords() {

		AtomicInteger rank = new AtomicInteger(1);
		ZSetOperations<String, String> zSetOps = redisTemplate.opsForZSet();

		var tuples = Optional.ofNullable(
			zSetOps.reverseRangeWithScores(
				POPULAR_KEYWORDS_ZSET_KEY,
				RANKING_START,
				RANKING_END
			)
		).orElse(Collections.emptySet());

		var elements = tuples.stream()
			.map(t -> SearchRankingResponseElement.of(
				rank.getAndIncrement(),
				t.getValue(),
				t.getScore() == null ? 0.0 : t.getScore()
			))
			.toList();

		return SearchRankingListResponse.from(elements);
	}

	private String normalize(String keyword) {
		return keyword.trim().replaceAll("\\s+", " ");
	}
}