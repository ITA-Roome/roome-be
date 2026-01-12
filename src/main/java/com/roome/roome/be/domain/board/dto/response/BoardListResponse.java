package com.roome.roome.be.domain.board.dto.response;

import com.roome.roome.be.domain.board.entity.Board;
import com.roome.roome.be.domain.board.entity.BoardProduct;
import com.roome.roome.be.domain.board.entity.BoardReference;
import com.roome.roome.be.domain.chat.enums.ChatMode;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public record BoardListResponse(
        Long boardId,
        Long userId,
        String title,
        String description,
        List<String> keywords,
        List<String> imageUrls,
        List<String> productNames, // [추가] 제품명 리스트
        ChatMode category,
        LocalDateTime createdAt
) {
    public static BoardListResponse from(Board board) {
        List<String> parsedKeywords = (board.getKeywords() != null && !board.getKeywords().isBlank())
                ? Arrays.asList(board.getKeywords().split(", "))
                : Collections.emptyList();

        List<String> images;
        List<String> names;

        if (board.getCategory() == ChatMode.PRODUCT) {
            images = board.getBoardProducts().stream()
                    .map(BoardProduct::getImageUrl)
                    .limit(4)
                    .collect(Collectors.toList());

            names = board.getBoardProducts().stream()
                    .map(BoardProduct::getName)
                    .collect(Collectors.toList());

        } else {
            images = board.getBoardReferences().stream()
                    .map(BoardReference::getImageUrl)
                    .limit(4)
                    .collect(Collectors.toList());

            names = Collections.emptyList();
        }

        return new BoardListResponse(
                board.getId(),
                board.getUserId(),
                board.getTitle(),
                board.getDescription(),
                parsedKeywords,
                images,
                names,
                board.getCategory(),
                board.getCreatedAt()
        );
    }
}