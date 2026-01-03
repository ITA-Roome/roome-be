package com.roome.roome.be.domain.search.service;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.roome.roome.be.domain.search.dto.response.RecentSearchListResponse;
import com.roome.roome.be.domain.search.dto.response.SearchRankingListResponse;
import com.roome.roome.be.domain.search.dto.response.SearchRankingResponseElement;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class SearchService {

	private final StringRedisTemplate redisTemplate;

	private static final String POPULAR_KEYWORDS_ZSET_KEY = "search:popularRank:global";
	private static final String RECENT_KEY_PREFIX = "search::recent::";
	private static final int RECENT_LIMIT = 10;
	private static final double RANKING_INCREMENT_SCORE = 1;
	private static final long RANKING_START = 0L;
	private static final long RANKING_END = 9L;
	private static final Duration RECENT_TTL = Duration.ofDays(90);

	//키워드 검색
	public void recordSearch(String rawKeyword, Long userId) {

		if (rawKeyword == null || rawKeyword.isBlank()) return;

		String keyword = normalize(rawKeyword);

		redisTemplate.opsForZSet()
			.incrementScore(POPULAR_KEYWORDS_ZSET_KEY, keyword, RANKING_INCREMENT_SCORE);

		addRecentKeyword(userId, keyword);
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


	//최근 검색어
	@Transactional(readOnly = true)
	public RecentSearchListResponse getRecentSearchList(Long userId) {

		String key = recentKey(userId);

		List<String> list = redisTemplate.opsForList().range(key, 0, RECENT_LIMIT);

		if (list == null) {
			return RecentSearchListResponse.from(Collections.emptyList());
		}

		Collections.reverse(list);

		return RecentSearchListResponse.from(list);
	}

	private void addRecentKeyword(Long userId, String keyword) {

		String key = recentKey(userId);
		redisTemplate.opsForList().remove(key, 0, keyword);
		redisTemplate.opsForList().rightPush(key, keyword);
		redisTemplate.opsForList().trim(key, -RECENT_LIMIT, -1);
		redisTemplate.expire(key, RECENT_TTL);
	}

	private String recentKey(Long userId) {
		return RECENT_KEY_PREFIX + userId;
	}

	private String normalize(String keyword) {
		return keyword.trim().replaceAll("\\s+", " ");
	}
}