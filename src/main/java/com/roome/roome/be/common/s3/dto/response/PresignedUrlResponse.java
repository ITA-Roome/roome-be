package com.roome.roome.be.common.s3.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "단일 Presigned URL 응답")
public record PresignedUrlResponse(
	@Schema(description = "업로드용 Presigned URL")
	String uploadUrl,

	@Schema(description = "S3 객체 키 (DB 저장용)")
	String objectKey
) {}