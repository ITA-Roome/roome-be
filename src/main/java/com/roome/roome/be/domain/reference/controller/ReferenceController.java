package com.roome.roome.be.domain.reference.controller;

import com.roome.roome.be.common.response.ApiResponse;
import com.roome.roome.be.common.status.SuccessStatus;
import com.roome.roome.be.domain.reference.service.ReferenceService;
import io.swagger.v3.oas.annotations.Operation;
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

}
