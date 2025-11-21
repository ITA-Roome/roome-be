package com.roome.roome.be.domain.search.dto.response;

import java.util.List;

public record RecentSearchListResponse(
	List<String> keywords
) {
	public static RecentSearchListResponse from(List<String> list) {
		return new RecentSearchListResponse(list);
	}
}