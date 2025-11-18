package com.roome.roome.be.domain.reference.controller;

import com.roome.roome.be.common.response.ApiResponse;
import com.roome.roome.be.common.status.SuccessStatus;
import com.roome.roome.be.domain.reference.dto.response.ReferenceListResponse;
import com.roome.roome.be.domain.reference.dto.response.ReferenceToggleScrapResponse;
import com.roome.roome.be.domain.reference.service.ReferenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
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

    @GetMapping("")
    @Operation(
            summary = "레퍼런스 리스트 조회",
            description = "레퍼런스 리스트를 사용자에 맞게 출력합니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "스크랩 내역 리스트 조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReferenceListResponse.class)))
    public ResponseEntity<ApiResponse<ReferenceListResponse>> getReferenceList(
      @AuthenticationPrincipal Long userId
    ){
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

    @PostMapping("/{referenceId}/scrap")
    @Operation(
            summary = "레퍼런스 스크랩 토글 ",
            description = "이미 스크랩이 되어 있으면 취소하고, 되어 있지 않으면 스크랩에 추가합니다."
    )
    @Parameters({
            @Parameter(name = "referenceId", description = "스크랩할 Reference ID", example = "1")
    })
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "스크랩 토글 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReferenceToggleScrapResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "유저가 존재하지 않거나 레퍼런스가 존재하지 않음", content = @Content(mediaType = "application/json"))
    public ResponseEntity<ApiResponse<ReferenceToggleScrapResponse>> toggleProductScrap(
            @PathVariable("referenceId") Long referenceId,
            @AuthenticationPrincipal Long userId
    ) {
        ReferenceToggleScrapResponse response = referenceService.toggleReferenceScrap(referenceId, userId);
        return ApiResponse.success(SuccessStatus.CREATE_REFERENCE_SCRAP,response);
    }
}
