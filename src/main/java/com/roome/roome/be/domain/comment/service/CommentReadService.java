package com.roome.roome.be.domain.comment.service;

import com.roome.roome.be.domain.comment.converter.CommentConverter;
import com.roome.roome.be.domain.comment.dto.response.CommentResponse;
import com.roome.roome.be.domain.comment.entity.Comment;
import com.roome.roome.be.domain.comment.enums.CommentableType;
import com.roome.roome.be.domain.comment.repository.CommentLikeRepository;
import com.roome.roome.be.domain.comment.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.ArrayList;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentReadService {

    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final CommentConverter commentConverter;

    public List<CommentResponse> getComments(Long userId, CommentableType type, Long commentableId) {
        List<Comment> parentComments = commentRepository.findParentCommentsByCommentable(type, commentableId);
        List<Long> parentIds = parentComments.stream()
                .map(Comment::getId)
                .collect(Collectors.toList());

        List<Comment> childrenComments = commentRepository.findChildCommentsByParentIds(parentIds);

        Set<Long> allCommentIds = new HashSet<>(parentIds);
        for (Comment child : childrenComments) {
            allCommentIds.add(child.getId());
        }

        Set<Long> likedCommentIds = commentLikeRepository.findLikedCommentIdsByUserAndCommentIds(userId, allCommentIds);

        Map<Long, List<Comment>> childrenByParentId = childrenComments.stream()
                .collect(Collectors.groupingBy(child -> child.getParentComment().getId()));

        List<CommentResponse> result = new ArrayList<>(parentComments.size());

        getchildComments(userId, parentComments, likedCommentIds, childrenByParentId, result);
        return result;
    }

    private void getchildComments(Long userId, List<Comment> parentComments, Set<Long> likedCommentIds, Map<Long, List<Comment>> childrenByParentId, List<CommentResponse> result) {
        for (Comment parent : parentComments) {
            Long parentId = parent.getId();

            boolean isLiked = likedCommentIds.contains(parentId);

            List<Comment> children = childrenByParentId.getOrDefault(parentId, new ArrayList<>());

            List<CommentResponse> childResponses = new ArrayList<>(children.size());
            for (Comment child : children) {
                boolean childIsLiked = likedCommentIds.contains(child.getId());
                CommentResponse childDto = commentConverter.toDto(child, userId, childIsLiked);
                childResponses.add(childDto);
            }

            CommentResponse parentDto = commentConverter.toDtoWithChildren(parent, userId, isLiked, childResponses);
            result.add(parentDto);
        }
    }
}
