package com.roome.roome.be.domain.comment.converter;

import com.roome.roome.be.domain.comment.dto.request.CommentRequest;
import com.roome.roome.be.domain.comment.dto.response.CommentResponse;
import com.roome.roome.be.domain.comment.entity.Comment;
import com.roome.roome.be.domain.user.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CommentConverter {

    public Comment toEntity(CommentRequest requestDto, User user, Comment parentComment) {
        return Comment.builder()
                .user(user)
                .parentComment(parentComment)
                .commentableType(requestDto.getCommentableType())
                .commentableId(requestDto.getCommentableId())
                .content(requestDto.getContent())
                .likeCount(0)
                .build();
    }

    public CommentResponse toDto(Comment comment, Long currentUserId, boolean isLiked) {
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

    public CommentResponse toDtoWithChildren(
            Comment parent,
            Long currentUserId,
            boolean isLiked,
            List<CommentResponse> childResponses) {

        return CommentResponse.builder()
                .id(parent.getId())
                .userId(parent.getUser().getId())
                .nickname(parent.getUser().getNickname())
                .profileImage(parent.getUser().getProfileImage())
                .content(parent.getContent())
                .likeCount(parent.getLikeCount())
                .isLiked(isLiked)
                .isAuthor(parent.getUser().getId().equals(currentUserId))
                .createdAt(parent.getCreatedAt())
                .updatedAt(parent.getUpdatedAt())
                .childComments(childResponses)
                .build();
    }
}
