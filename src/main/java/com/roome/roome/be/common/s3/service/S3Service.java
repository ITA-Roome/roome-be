package com.roome.roome.be.common.s3.service;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.amazonaws.services.s3.model.ObjectMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import org.springframework.web.multipart.MultipartFile;

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


	public PresignedUrlResponse generatePresignedPutUrl(StorageScope storageScope, long id, PresignedUrlRequest presignedUrlRequest) {
		validate(presignedUrlRequest.contentType(), presignedUrlRequest.sizeBytes());
		String ext = CT_TO_EXT.get(presignedUrlRequest.contentType());
		String key = buildKey(storageScope, id, ext);
		URL url = generatePutUrl(key, presignedUrlRequest.contentType(), DEFAULT_EXPIRE_MILLIS);
		return new PresignedUrlResponse(url.toString(), key);
	}

	public List<PresignedUrlBatchResponse> generatePresignedPutUrls(StorageScope storageScope, long id, List<PresignedUrlRequest> presignedUrlRequests) {
		List<PresignedUrlBatchResponse> list = new ArrayList<>();
		for (PresignedUrlRequest presignedUrlRequest : presignedUrlRequests) {
			PresignedUrlResponse presignedUrlResponse = generatePresignedPutUrl(storageScope, id, presignedUrlRequest);
			list.add(new PresignedUrlBatchResponse(presignedUrlResponse.uploadUrl(), presignedUrlResponse.objectKey(), presignedUrlRequest.order()));
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

	private String buildKey(StorageScope storageScope, long id, String ext) {
		String uuid = UUID.randomUUID().toString();
		return switch (storageScope) {
			case SHOP_PROFILE   -> "shops/%d/profile/%s.%s".formatted(id, uuid, ext);
			case PRODUCT_DETAIL -> "products/%d/detail/%s.%s".formatted(id, uuid, ext);
			case UPLOAD_SESSION -> "uploads/%d/detail/%s.%s".formatted(id, uuid, ext);
			case REFERENCE      -> "references/%d/%s.%s".formatted(id, uuid, ext); // ★ 추가
		};
	}

	//상품 등록을 위해 이미지 경로 임시 저장 후 실제 db에 옮기기 위해
	@Transactional
	public void moveAll(Map<String, String> sourceToDestinationMap) {
		for (Map.Entry<String, String> entry : sourceToDestinationMap.entrySet()) {
			String source = entry.getKey();
			String dest = entry.getValue();

			amazonS3.copyObject(bucket, source, bucket, dest);

			try {
				amazonS3.deleteObject(bucket, source);
			} catch (Exception ignore) {
			}
		}
	}

	public void deleteObject(String objectKey) {
		if (objectKey == null || objectKey.isBlank()) return;
		amazonS3.deleteObject(bucket, objectKey);
	}

	public String uploadObject(StorageScope storageScope, long id, MultipartFile file) {
		validate(file.getContentType(), file.getSize());

		String ext = CT_TO_EXT.get(file.getContentType());
		String objectKey = buildKey(storageScope, id, ext);

		try{
			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentType(file.getContentType());
			metadata.setContentLength(file.getSize());

			amazonS3.putObject(bucket, objectKey, file.getInputStream(), metadata);
		}catch (Exception e){
			throw new GeneralException(ErrorStatus.FILE_TOO_LARGE);
		}

		return objectKey;
	}
}

