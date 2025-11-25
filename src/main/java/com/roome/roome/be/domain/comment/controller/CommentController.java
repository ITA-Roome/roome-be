package com.roome.roome.be.domain.comment.controller;

import com.roome.roome.be.common.response.ApiResponse;
import com.roome.roome.be.common.status.SuccessStatus;
import com.roome.roome.be.domain.comment.dto.request.CommentRequest;
import com.roome.roome.be.domain.comment.dto.request.CommentUpdateRequest;
import com.roome.roome.be.domain.comment.dto.response.CommentResponse;
import com.roome.roome.be.domain.comment.enums.CommentableType;
import com.roome.roome.be.domain.comment.service.CommentCoreService;
import com.roome.roome.be.domain.comment.service.CommentLikeService;
import com.roome.roome.be.domain.comment.service.CommentReadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comment") // 댓글 전용 경로
@Tag(name = "Comment", description = "댓글 API")
public class CommentController {

    private final CommentCoreService commentCoreService;
    private final CommentReadService commentReadService;
    private final CommentLikeService commentLikeService;

    @PostMapping("")
    @Operation(summary = "댓글 생성", description = "게시글 또는 다른 댓글에 대한 새로운 댓글/대댓글을 생성합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "댓글 생성 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommentResponse.class)))
    public ResponseEntity<ApiResponse<CommentResponse>> createComment(
            @AuthenticationPrincipal Long userId,
            @RequestBody @Valid CommentRequest requestDto
    ) {
        CommentResponse response = commentCoreService.createComment(userId, requestDto);
        return ApiResponse.success(SuccessStatus.CREATE_COMMENT_SUCCESS, response);
    }

    @GetMapping("")
    @Operation(summary = "댓글 목록 조회", description = "특정 대상(상품, 레퍼런스 등)에 달린 부모 댓글과 대댓글 목록을 최적화된 방식으로 조회합니다.")
    @Parameters({
            @Parameter(name = "type", description = "댓글 대상의 타입 (PRODUCT, REFERENCE 등)", required = true, example = "PRODUCT"),
            @Parameter(name = "commentableId", description = "댓글 대상의 ID", required = true, example = "123")
    })
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "댓글 목록 조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommentResponse.class)))
    public ResponseEntity<ApiResponse<List<CommentResponse>>> getComments(
            @AuthenticationPrincipal Long userId,
            @RequestParam CommentableType type,
            @RequestParam Long commentableId
    ) {
        List<CommentResponse> comments = commentReadService.getComments(userId, type, commentableId);
        return ApiResponse.success(SuccessStatus.GET_COMMENT_LIST_SUCCESS, comments);
    }

    @PatchMapping("/{commentId}")
    @Operation(summary = "댓글 수정", description = "특정 댓글의 내용을 수정합니다.")
    @Parameters({
            @Parameter(name = "commentId", description = "수정할 댓글의 ID", example = "456"),
    })
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "댓글 수정 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommentResponse.class)))
    public ResponseEntity<ApiResponse<CommentResponse>> updateComment(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long commentId,
            @RequestBody @Valid CommentUpdateRequest updateDto
    ) {
        CommentResponse response = commentCoreService.updateComment(userId, commentId, updateDto);
        return ApiResponse.success(SuccessStatus.UPDATE_COMMENT_SUCCESS, response);
    }

    @DeleteMapping("/{commentId}")
    @Operation(summary = "댓글 삭제", description = "자신의 댓글을 삭제합니다.")
    @Parameters({
            @Parameter(name = "commentId", description = "삭제할 댓글의 ID", example = "456"),
    })
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "댓글 삭제 성공 (본문 없음)")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long commentId
    ) {
        commentCoreService.deleteComment(userId, commentId);
        return ApiResponse.success(SuccessStatus.DELETE_COMMENT_SUCCESS);
    }

    @PostMapping("/{commentId}/like")
    @Operation(summary = "댓글 좋아요 토글", description = "댓글에 좋아요를 추가하거나 취소합니다.")
    @Parameters({
            @Parameter(name = "commentId", description = "좋아요를 토글할 댓글의 ID", example = "456"),
    })
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "좋아요 토글 성공 (본문 없음)")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "유저나 댓글을 찾을 수 없음")
    public ResponseEntity<ApiResponse<Void>> toggleLike(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long commentId
    ) {
        commentLikeService.toggleLike(userId, commentId);
        return ApiResponse.success(SuccessStatus.TOGGLE_COMMENT_LIKE_SUCCESS);
    }
}
