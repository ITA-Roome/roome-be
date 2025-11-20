package com.roome.roome.be.domain.inquiry.controller;

import com.roome.roome.be.common.response.ApiResponse;
import com.roome.roome.be.common.status.SuccessStatus;
import com.roome.roome.be.domain.inquiry.dto.request.RegisterInquiryRequest;
import com.roome.roome.be.domain.inquiry.service.InquiryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/inquiries")
@Tag(name = "Inquiry", description = "문의 API")
public class InquiryController {

    private final InquiryService inquiryService;

    @PostMapping("")
    @Operation(summary = "문의하기", description = "유저가 문의 등록")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "문의 성공", content = @Content)
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "문의 유형 불일치", content = @Content)
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "유저가 존재하지 않음", content = @Content)
    public ResponseEntity<ApiResponse<Void>> registerInquiry(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody RegisterInquiryRequest request
    ){
        inquiryService.registerInquiry(userId,request);
        return ApiResponse.success(SuccessStatus.REGISTER_INQUIRY_SUCCESS);
    }
}
