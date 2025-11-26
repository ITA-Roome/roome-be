package com.roome.roome.be.domain.comment.validator;

import com.roome.roome.be.common.exception.GeneralException;
import com.roome.roome.be.common.status.ErrorStatus;
import com.roome.roome.be.domain.comment.entity.Comment;
import com.roome.roome.be.domain.comment.enums.CommentableType;
import com.roome.roome.be.domain.comment.repository.CommentRepository;
import com.roome.roome.be.domain.product.repository.ProductRepository;
import com.roome.roome.be.domain.reference.repository.ReferenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentValidator {

    private final CommentRepository commentRepository;
    private final ProductRepository productRepository;
    private final ReferenceRepository referenceRepository;

    private static final int MAX_CONTENT_LENGTH = 400;

    // 댓글이 달릴 대상이 존재하는지 확인
    public void validateCommentableEntity(CommentableType type, Long commentableId) {
        boolean exists;
        if (type == CommentableType.PRODUCT) {
            exists = productRepository.existsById(commentableId);
        } else if (type == CommentableType.REFERENCE) {
            exists = referenceRepository.existsById(commentableId);
        } else {
            // 지원하지 않는 타입 처리
            throw new GeneralException(ErrorStatus.COMMENTABLE_ENTITY_NOT_FOUND);
        }

        if (!exists) {
            throw new GeneralException(ErrorStatus.COMMENTABLE_ENTITY_NOT_FOUND);
        }
    }

    // 댓글 찾기
    public Comment findCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.COMMENT_NOT_FOUND));
    }

    // 사용자 댓글 수정, 삭제 권한 확인
    public void validateCommentAuthor(Comment comment, Long userId) {
        if (!comment.getUser().getId().equals(userId)) {
            throw new GeneralException(ErrorStatus.COMMENT_AUTHOR_MISMATCH);
        }
    }

    // 댓글 길이 검증
    public void validateCommentContent(String content) {
        if (content.length() > MAX_CONTENT_LENGTH) {
            throw new GeneralException(ErrorStatus.COMMENT_CONTENT_TOO_LONG);
        }
    }
}
