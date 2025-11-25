package com.roome.roome.be.domain.comment.service;

import com.roome.roome.be.common.exception.GeneralException;
import com.roome.roome.be.common.status.ErrorStatus;
import com.roome.roome.be.domain.comment.entity.Comment;
import com.roome.roome.be.domain.comment.entity.CommentLike;
import com.roome.roome.be.domain.comment.repository.CommentLikeRepository;
import com.roome.roome.be.domain.comment.repository.CommentRepository;
import com.roome.roome.be.domain.comment.validator.CommentValidator;
import com.roome.roome.be.domain.user.entity.User;
import com.roome.roome.be.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentLikeService {

    private final CommentLikeRepository commentLikeRepository;
    private final UserRepository userRepository;
    private final CommentValidator commentValidator;

    public void toggleLike(Long userId, Long commentId) {
        Comment comment = commentValidator.findCommentById(commentId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        // 좋아요 상태 확인 및 토글
        commentLikeRepository.findByCommentIdAndUserId(commentId, userId)
                .ifPresentOrElse(
                        like -> {
                            commentLikeRepository.delete(like);
                            comment.decrementLikeCount();
                        },
                        () -> {
                            CommentLike newLike = CommentLike.builder()
                                    .comment(comment)
                                    .user(user)
                                    .build();
                            commentLikeRepository.save(newLike);
                            comment.incrementLikeCount();
                        }
                );
    }
}
