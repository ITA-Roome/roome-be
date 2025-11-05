package com.roome.roome.be.common.s3.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ImageUrlBuilder {
	@Value("${storage.cdn-base-url}")
	private String cdnBaseUrl;

	public String build(String objectKey) {
		if (objectKey == null || objectKey.isBlank()) return null;

		String base = cdnBaseUrl.endsWith("/") ? cdnBaseUrl : cdnBaseUrl + "/";
		String key  = objectKey.startsWith("/") ? objectKey.substring(1) : objectKey;
		return base + key;
	}
}
