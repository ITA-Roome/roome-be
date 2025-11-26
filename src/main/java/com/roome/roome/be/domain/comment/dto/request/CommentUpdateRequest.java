package com.roome.roome.be.domain.comment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor

public class CommentUpdateRequest {

    @NotBlank(message = "댓글 내용은 필수입니다.")
    @Size(max = 400, message = "댓글 내용은 최대 400자까지 입력 가능합니다.")
    private String content;
}
