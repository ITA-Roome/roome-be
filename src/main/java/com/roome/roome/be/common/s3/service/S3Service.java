package com.roome.roome.be.common.s3.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.roome.roome.be.common.exception.GeneralException;
import com.roome.roome.be.common.s3.dto.request.PresignedUrlRequest;
import com.roome.roome.be.common.s3.dto.response.PresignedUrlBatchResponse;
import com.roome.roome.be.common.s3.dto.response.PresignedUrlResponse;
import com.roome.roome.be.common.s3.enums.StorageScope;
import com.roome.roome.be.common.status.ErrorStatus;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.*;

@Service
@RequiredArgsConstructor
public class S3Service {

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	private final AmazonS3 amazonS3;
	private static final Set<String> ALLOWED_CT = Set.of("image/jpeg", "image/png", "image/webp");
	private static final Map<String, String> CT_TO_EXT = Map.of(
		"image/jpeg", "jpg",
		"image/png",  "png",
		"image/webp", "webp"
	);
	private static final long MAX_SIZE = 5 * 1024 * 1024; // 5MB
	private static final long DEFAULT_EXPIRE_MILLIS = 5 * 60_000L; // 5분

	public PresignedUrlResponse generatePresignedPutUrl(StorageScope storageScope, long productId, PresignedUrlRequest file) {
		validate(file.contentType(), file.sizeBytes());
		String ext = CT_TO_EXT.get(file.contentType());
		String key = buildKey(storageScope, productId, ext);

		URL url = generatePutUrl(key, file.contentType(), DEFAULT_EXPIRE_MILLIS);
		return new PresignedUrlResponse(url.toString(), key);
	}

	public List<PresignedUrlBatchResponse> generatePresignedPutUrls(StorageScope storageScope, long productId, List<PresignedUrlRequest> files) {
		List<PresignedUrlBatchResponse> list = new ArrayList<>();
		for (PresignedUrlRequest f : files) {
			PresignedUrlResponse r = generatePresignedPutUrl(storageScope, productId, f);
			list.add(new PresignedUrlBatchResponse(r.uploadUrl(), r.objectKey(), f.order()));
		}
		return list;
	}


	private URL generatePutUrl(String objectKey, String contentType, long expireMs) {
		var req = new GeneratePresignedUrlRequest(bucket, objectKey)
			.withMethod(HttpMethod.PUT)
			.withExpiration(new Date(System.currentTimeMillis() + expireMs));
		req.addRequestParameter("Content-Type", contentType);
		return amazonS3.generatePresignedUrl(req);
	}

	private void validate(String contentType, long sizeBytes) {
		if (!ALLOWED_CT.contains(contentType)) throw new GeneralException(ErrorStatus.INVALID_FILE_TYPE);
		if (sizeBytes > MAX_SIZE) throw new GeneralException(ErrorStatus.FILE_TOO_LARGE);
	}

	private String buildKey(StorageScope scope, long productID, String ext) {
		String uuid = UUID.randomUUID().toString();
		return switch (scope) {
			case SHOP_PROFILE   -> "shops/%d/profile/%s.%s".formatted(productID, uuid, ext);
			case PRODUCT_MAIN   -> "products/%d/main/%s.%s".formatted(productID, uuid, ext);
			case PRODUCT_DETAIL -> "products/%d/detail/%s.%s".formatted(productID, uuid, ext);
		};
	}
}

