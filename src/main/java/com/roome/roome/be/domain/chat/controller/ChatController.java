package com.roome.roome.be.domain.chat.controller;

import com.roome.roome.be.common.response.ApiResponse;
import com.roome.roome.be.common.status.SuccessStatus;
import com.roome.roome.be.domain.chat.dto.request.ChatMessageRequest;
import com.roome.roome.be.domain.chat.dto.request.ChatProductScenarioRequest;
import com.roome.roome.be.domain.chat.dto.request.ChatReferenceScenarioRequest;
import com.roome.roome.be.domain.chat.dto.response.ChatMessageResponse;
import com.roome.roome.be.domain.chat.dto.response.ChatProductScenarioResponse;
import com.roome.roome.be.domain.chat.dto.response.ChatReferenceScenarioResponse;
import com.roome.roome.be.domain.chat.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/chat")
@Tag(name = "Chat", description = "채팅 API")
public class ChatController {

    private final ChatService chatService;


    @Operation(summary = "대화형 챗봇 메시지 처리", description = "버튼/자유입력을 받아 다음 질문 또는 추천 결과를 반환합니다.")
    @PostMapping("/message")
    public ResponseEntity<ApiResponse<ChatMessageResponse>> message(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody ChatMessageRequest request
    ) {
        ChatMessageResponse response = chatService.handle(userId, request);
        return ApiResponse.success(SuccessStatus.CHAT_SCENARIO_SUCCESS, response);
    }


//    @Operation(summary = "시나리오 대화 - 제품 추천", description = "사용자의 공간/분위기/예산을 기반으로 제품 or 레퍼런스 생성")
//    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "시나리오 채팅 대화 성공", content = @Content(schema = @Schema(implementation = ChatProductScenarioResponse.class)))
//    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "요청값이 올바르지 않음", content = @Content)
//    @PostMapping("/scenario/product")
//    public ResponseEntity<ApiResponse<ChatProductScenarioResponse>> processProductScenario(
//            @AuthenticationPrincipal Long userId,
//            @Valid @RequestBody ChatProductScenarioRequest request
//    ) {
//        ChatProductScenarioResponse response = chatService.processChatProductScenario(userId, request);
//        return ApiResponse.success(SuccessStatus.CHAT_SCENARIO_SUCCESS,response);
//    }
//
//    @Operation(summary = "시나리오 대화 - 인테리어 추천", description = "사용자의 공간/분위기/예산을 기반으로 제품 or 레퍼런스 생성")
//    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "시나리오 채팅 대화 성공", content = @Content(schema = @Schema(implementation = ChatReferenceScenarioResponse.class)))
//    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "요청값이 올바르지 않음", content = @Content)
//    @PostMapping("/scenario/interior")
//    public ResponseEntity<ApiResponse<ChatReferenceScenarioResponse>> processInteriorScenario(
//            @AuthenticationPrincipal Long userId,
//            @Valid @RequestBody ChatReferenceScenarioRequest request
//    ) {
//        ChatReferenceScenarioResponse response = chatService.processChatReferenceScenario(userId, request);
//        return ApiResponse.success(SuccessStatus.CHAT_SCENARIO_SUCCESS,response);
//    }



}
