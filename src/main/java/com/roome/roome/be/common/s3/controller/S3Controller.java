package com.roome.roome.be.common.s3.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.roome.roome.be.common.response.ApiResponse;
import com.roome.roome.be.common.s3.service.S3Service;
import com.roome.roome.be.common.s3.dto.request.PresignedUrlRequest;
import com.roome.roome.be.common.s3.dto.response.PresignedUrlBatchResponse;
import com.roome.roome.be.common.s3.dto.response.PresignedUrlResponse;
import com.roome.roome.be.common.s3.enums.StorageScope;
import com.roome.roome.be.common.status.SuccessStatus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Tag(name = "S3", description = "S3 Presigned URL API")
public class S3Controller {

	private final S3Service s3Service;

	//상품 등록 전 사진 임시저장을 위한 api
	@PostMapping("/uploads/{sessionId}/images/presigned")
	@Operation(summary = "세션 업로드용 Presigned URL(여러장 가능), sessionId는 프론트에서 임의 랜덤으로 만들어주시면 됩니다.")
	@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 파일 형식이거나 용량 제한(5MB)을 초과한 경우", content = @Content)
	@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "관리자 권한이 없는 경우", content = @Content)
	public ResponseEntity<ApiResponse<List<PresignedUrlBatchResponse>>> generatePresignedUrls(
		@PathVariable Long sessionId,
		@RequestBody List<PresignedUrlRequest> files
	) {
		var responses = s3Service.generatePresignedPutUrls(StorageScope.UPLOAD_SESSION, sessionId, files);
		return ApiResponse.success(SuccessStatus.S3_PRESIGNED_ISSUE_SUCCESS, responses);
	}

}
