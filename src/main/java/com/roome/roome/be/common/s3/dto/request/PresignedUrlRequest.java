package com.roome.roome.be.common.s3.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Presigned URL 발급 요청 파일 정보")
public record PresignedUrlRequest(
	@Schema(description = "파일 MIME 타입 (예: image/jpeg, image/png)", example = "image/jpeg")
	@NotBlank(message = "contentType은 필수입니다.")
	String contentType,

	@Schema(description = "파일 크기(Byte)", example = "1048576")
	@NotBlank(message = "sizeBytes은 필수입니다.")
	long sizeBytes,

	@Schema(description = "이미지 순서 (여러 장 업로드 시 정렬용)", example = "1")
	@NotBlank(message = "order는 필수입니다.")
	Integer order
) {}