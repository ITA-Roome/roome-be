package com.roome.roome.be.domain.comment.dto.request;

import com.roome.roome.be.domain.comment.enums.CommentableType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentRequest {

    @NotNull(message = "댓글 대상 타입은 필수입니다.")
    private CommentableType commentableType;

    @NotNull(message = "댓글 대상 ID는 필수입니다.")
    private Long commentableId;

    @NotBlank(message = "댓글 내용은 필수입니다.")
    @Size(max = 400, message = "댓글 내용은 최대 400자까지 입력 가능합니다.")
    private String content;

    private Long parentCommentId;
}
