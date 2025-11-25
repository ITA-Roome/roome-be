package com.roome.roome.be.domain.comment.service;

import com.roome.roome.be.common.exception.GeneralException;
import com.roome.roome.be.common.status.ErrorStatus;
import com.roome.roome.be.domain.comment.converter.CommentConverter;
import com.roome.roome.be.domain.comment.dto.request.CommentRequest;
import com.roome.roome.be.domain.comment.dto.request.CommentUpdateRequest;
import com.roome.roome.be.domain.comment.dto.response.CommentResponse;
import com.roome.roome.be.domain.comment.entity.Comment;
import com.roome.roome.be.domain.comment.repository.CommentRepository;
import com.roome.roome.be.domain.comment.repository.CommentLikeRepository;
import com.roome.roome.be.domain.comment.validator.CommentValidator;
import com.roome.roome.be.domain.user.entity.User;
import com.roome.roome.be.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentCoreService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final CommentConverter commentConverter;
    private final CommentLikeRepository commentLikeRepository;
    private final CommentValidator commentValidator;

    // 댓글 생성
    public CommentResponse createComment(Long userId, CommentRequest requestDto) {
        commentValidator.validateCommentContent(requestDto.getContent());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        commentValidator.validateCommentableEntity(requestDto.getCommentableType(), requestDto.getCommentableId());
        Comment parentComment = null;
        if (requestDto.getParentCommentId() != null) {
            parentComment = commentValidator.findCommentById(requestDto.getParentCommentId());
        }

        Comment comment = commentConverter.toEntity(requestDto, user, parentComment);
        Comment savedComment = commentRepository.save(comment);

        return commentConverter.toDto(savedComment, userId, false);
    }

    // 댓글 수정
    public CommentResponse updateComment(Long userId, Long commentId, CommentUpdateRequest updateDto) {
        Comment comment = commentValidator.findCommentById(commentId);
        commentValidator.validateCommentAuthor(comment, userId);
        comment.updateContent(updateDto.getContent());

        boolean isLiked = commentLikeRepository.existsByCommentIdAndUserId(commentId, userId);
        return commentConverter.toDto(comment, userId, isLiked);
    }

    // 댓글 삭제
    public void deleteComment(Long userId, Long commentId) {
        Comment comment = commentValidator.findCommentById(commentId);
        commentValidator.validateCommentAuthor(comment, userId);

        commentRepository.delete(comment);
    }
}
