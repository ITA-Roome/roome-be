package com.roome.roome.be.domain.comment.dto.response;

import com.roome.roome.be.domain.comment.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponse {

    private Long id;
    private Long userId;
    private String nickname;
    private String profileImage;
    private String content;
    private Integer likeCount;
    private Boolean isLiked;
    private Boolean isAuthor;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<CommentResponse> childComments;

    public static CommentResponse from(Comment comment, Long currentUserId, boolean isLiked) {
        return CommentResponse.builder()
                .id(comment.getId())
                .userId(comment.getUser().getId())
                .nickname(comment.getUser().getNickname())
                .profileImage(comment.getUser().getProfileImage())
                .content(comment.getContent())
                .likeCount(comment.getLikeCount())
                .isLiked(isLiked)
                .isAuthor(comment.getUser().getId().equals(currentUserId))
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}
