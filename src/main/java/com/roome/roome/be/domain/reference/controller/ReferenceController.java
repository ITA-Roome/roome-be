package com.roome.roome.be.domain.reference.controller;

import com.roome.roome.be.common.response.ApiResponse;
import com.roome.roome.be.common.status.SuccessStatus;
import com.roome.roome.be.domain.reference.dto.response.ReferenceDetailResponse;
import com.roome.roome.be.domain.reference.dto.response.ReferenceListResponse;
import com.roome.roome.be.domain.reference.service.ReferenceService;
import com.roome.roome.be.domain.search.service.SearchService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/references")
@Tag(name = "Reference")
public class ReferenceController {
    private final ReferenceService referenceService;
    private final SearchService searchService;

    @GetMapping("")
    @Operation(
            summary = "레퍼런스 리스트 조회",
            description = "레퍼런스 리스트를 사용자에 맞게 출력합니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "스크랩 내역 리스트 조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReferenceListResponse.class)))
    public ResponseEntity<ApiResponse<ReferenceListResponse>> getReferenceList(
        @AuthenticationPrincipal Long userId,
        @RequestParam(required = false) String keyWord
    ){

        searchService.recordSearch(keyWord, userId);
        ReferenceListResponse response = referenceService.getReferenceList(userId);
        return ApiResponse.success(SuccessStatus.GET_REFERENCE_LIST_SUCCESS,response);
    }

    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "레퍼런스 업로드",
            description = "레퍼런스 생성 && 레퍼런스 이미지 등록"
    )
    public ResponseEntity<ApiResponse<Void>> createReference(
            @AuthenticationPrincipal Long userId,
            @RequestPart("files") List<MultipartFile> files
    ){
        referenceService.registerReference(userId,files);
        return ApiResponse.success(SuccessStatus.REGISTER_REFERENCE_SUCCESS);
    }

    @GetMapping("/{referenceId}")
    @Operation(
            summary = "레퍼런스 상세 조회",
            description = "레퍼런스의 상세 정보를 조회합니다. 로그인한 유저의 경우 좋아요/스크랩 여부가 반영됩니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "레퍼런스 상세 조회 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReferenceDetailResponse.class))
    )
    public ResponseEntity<ApiResponse<ReferenceDetailResponse>> getReferenceDetail(
            @PathVariable Long referenceId,
            @AuthenticationPrincipal Long userId
    ) {
        ReferenceDetailResponse response = referenceService.getReferenceDetail(referenceId, userId);
        return ApiResponse.success(SuccessStatus.GET_REFERENCE_DETAIL_SUCCESS, response);
    }
}
