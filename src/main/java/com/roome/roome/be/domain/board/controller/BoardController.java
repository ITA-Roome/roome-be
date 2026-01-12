package com.roome.roome.be.domain.board.controller;

import com.roome.roome.be.common.response.ApiResponse;
import com.roome.roome.be.common.status.SuccessStatus;
import com.roome.roome.be.domain.board.dto.request.BoardSaveRequest;
import com.roome.roome.be.domain.board.dto.response.BoardListResponse;
import com.roome.roome.be.domain.board.service.BoardService;
import com.roome.roome.be.domain.chat.dto.response.ChatMessageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
@Tag(name = "Boards", description = "루미와의 대화 저장(내 보드)")
public class BoardController {

    private final BoardService boardService;

    @PostMapping
    @Operation(
            summary = "추천 결과 내 보드에 저장"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "응답 성공", content = @Content(schema = @Schema(implementation = ChatMessageResponse.class)))
    public ResponseEntity<ApiResponse<Long>> saveBoard(
            @AuthenticationPrincipal Long userId,
            @RequestBody BoardSaveRequest request
    ) {
        Long boardId = boardService.saveBoardFromSession(request.sessionId(), userId);

        return ApiResponse.success(SuccessStatus.CREATE_BOARD_SUCCESS, boardId);
    }

    @GetMapping
    @Operation(
            summary = "루미와의 대화 추천 목록 조회"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "응답 성공", content = @Content(schema = @Schema(implementation = ChatMessageResponse.class)))
    public ResponseEntity<ApiResponse<Page<BoardListResponse>>> getBoardList(
            @AuthenticationPrincipal Long userId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<BoardListResponse> response = boardService.getBoardList(userId, pageable);
        return ApiResponse.success(SuccessStatus.GET_BOARD_LIST_SUCCESS, response);
    }
}