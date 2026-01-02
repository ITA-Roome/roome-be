package com.roome.roome.be.domain.chat.controller;

import com.roome.roome.be.common.response.ApiResponse;
import com.roome.roome.be.common.status.SuccessStatus;
import com.roome.roome.be.domain.chat.dto.request.ChatMessageRequest;
import com.roome.roome.be.domain.chat.dto.response.ChatMessageResponse;
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
@Tag(name = "Chat", description = "대화형 추천 챗봇 API")
public class ChatController {

    private final ChatService chatService;

    @Operation(
            summary = "챗봇 메시지 처리",
            description = """
                버튼 클릭 / 자유 입력을 포함한 모든 사용자 입력을 처리합니다.
                
                - 프론트는 message + sessionId + inputType 만 전달
                - 서버가 상태를 판단하여 다음 질문 또는 추천 결과를 반환합니다.
                """
    )
    @PostMapping("/message")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "응답 성공", content = @Content(schema = @Schema(implementation = ChatMessageResponse.class)))
    public ResponseEntity<ApiResponse<ChatMessageResponse>> message(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody ChatMessageRequest request
    ) {
        ChatMessageResponse response = chatService.handle(userId, request);
        return ApiResponse.success(SuccessStatus.CHAT_SCENARIO_SUCCESS, response);
    }

}
