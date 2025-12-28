package com.roome.roome.be.domain.chat.controller;

import com.roome.roome.be.common.response.ApiResponse;
import com.roome.roome.be.common.status.SuccessStatus;
import com.roome.roome.be.domain.chat.dto.request.ChatScenarioRequest;
import com.roome.roome.be.domain.chat.dto.response.ChatScenarioResponse;
import com.roome.roome.be.domain.chat.service.ChatService;
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


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/chat")
@Tag(name = "Chat", description = "채팅 API")
public class ChatController {

    private final ChatService chatService;

    @Operation(summary = "시나리오 대화", description = "사용자의 공간/분위기/예산을 기반으로 제품 or 레퍼런스 생성")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "시나리오 채팅 대화 성공", content = @Content)
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "요청값이 올바르지 않음", content = @Content)
    @PostMapping("/scenario")
    public ResponseEntity<ApiResponse<ChatScenarioResponse>> startInteriorScenario(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody ChatScenarioRequest request
    ) {
        ChatScenarioResponse response = chatService.processChatScenario(userId, request);
        return ApiResponse.success(SuccessStatus.CHAT_SCENARIO_SUCCESS,response);
    }

}
