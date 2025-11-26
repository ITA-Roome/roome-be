package com.roome.roome.be.domain.comment.repository;

import com.roome.roome.be.domain.comment.entity.Comment;
import com.roome.roome.be.domain.comment.enums.CommentableType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c JOIN FETCH c.user WHERE c.commentableType = :type AND c.commentableId = :id AND c.parentComment IS NULL ORDER BY c.createdAt DESC")
    List<Comment> findParentCommentsByCommentable(@Param("type") CommentableType type, @Param("id") Long id);

    @Query("SELECT c FROM Comment c JOIN FETCH c.user WHERE c.parentComment.id = :parentId ORDER BY c.createdAt ASC")
    List<Comment> findChildCommentsByParentId(@Param("parentId") Long parentId);

    @Query("SELECT c FROM Comment c JOIN FETCH c.user JOIN FETCH c.parentComment WHERE c.parentComment.id IN :parentIds ORDER BY c.createdAt ASC")
    List<Comment> findChildCommentsByParentIds(@Param("parentIds") List<Long> parentIds);
}
