package com.roome.roome.be.domain.reference.controller;

import com.roome.roome.be.common.response.ApiResponse;
import com.roome.roome.be.common.status.SuccessStatus;
import com.roome.roome.be.domain.reference.dto.response.CommonReferenceInfo;
import com.roome.roome.be.domain.reference.dto.response.ReferenceDetailResponse;
import com.roome.roome.be.domain.reference.service.ReferenceService;
import com.roome.roome.be.domain.search.service.SearchService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/references")
@Tag(name = "Reference")
public class ReferenceController {
    private final ReferenceService referenceService;
    private final SearchService searchService;

    @GetMapping("")
    @Operation(
            summary = "레퍼런스 리스트 조회 (검색 + 페이징)",
            description = """
                레퍼런스 목록을 조회합니다. 검색어(keyWord) 필터링과 페이징을 지원합니다.
                
                [기본 정렬]
                - 스크랩 수(scrapCount) 내림차순
                
                [정렬 가능 필드]
                - id, scrapCount, createdAt
                """
    )
    @Parameters({
            @Parameter(name = "keyWord", description = "레퍼런스 제목 검색어", example = "화이트"),
            @Parameter(name = "page", description = "페이지 번호 (0부터 시작)", example = "0"),
            @Parameter(name = "size", description = "페이지 크기", example = "10"),
            @Parameter(name = "sort", description = "정렬 (예: scrapCount,desc | createdAt,asc)")
    })
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))
    )
    public ResponseEntity<ApiResponse<Page<CommonReferenceInfo>>> getReferenceList(
            @AuthenticationPrincipal Long userId,

            @RequestParam(required = false) String keyWord,

            @ParameterObject
            @PageableDefault(size = 10, sort = "scrapCount", direction = Sort.Direction.DESC) Pageable pageable
    ){
        searchService.recordSearch(keyWord, userId);
        Page<CommonReferenceInfo> response = referenceService.getReferenceList(userId, keyWord, pageable);

        return ApiResponse.success(SuccessStatus.GET_REFERENCE_LIST_SUCCESS, response);
    }

    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "레퍼런스 업로드",
            description = "레퍼런스 생성 && 레퍼런스 이미지 등록"
    )
    public ResponseEntity<ApiResponse<Void>> createReference(
            @AuthenticationPrincipal Long userId,
            @RequestPart("file") MultipartFile file,
            @RequestPart String name,
            @RequestPart String description,
            @RequestPart String mood
    ){
        referenceService.registerReference(userId,file, name,description,mood);
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