package com.roome.roome.be.domain.board.dto.response;

import com.roome.roome.be.domain.board.entity.Board;
import com.roome.roome.be.domain.board.entity.BoardProduct;
import com.roome.roome.be.domain.board.entity.BoardReference;
import com.roome.roome.be.domain.chat.enums.ChatMode;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public record BoardListResponse(
        Long boardId,
        Long userId,
        String title,
        List<String> keywords,
        ChatMode category,
        List<ProductItem> products,
        List<ReferenceItem> references,
        LocalDateTime createdAt
) {

    public record ProductItem(
            Long productId,
            String name,
            Integer price,
            String imageUrl,
            String reason,
            String advantage,
            String mood,
            String recommendedPlace
    ) {
        public static ProductItem from(BoardProduct bp) {
            return new ProductItem(
                    bp.getProductId(),
                    bp.getName(),
                    bp.getPrice(),
                    bp.getImageUrl(),
                    bp.getReason(),
                    bp.getAdvantage(),
                    bp.getMood(),
                    bp.getRecommendedPlace()
            );
        }
    }

    public record ReferenceItem(
            Long referenceId,
            String imageUrl
    ) {
        public static ReferenceItem from(BoardReference br) {
            return new ReferenceItem(br.getReferenceId(), br.getImageUrl());
        }
    }

    public static BoardListResponse from(Board board) {
        List<String> parsedKeywords = (board.getKeywords() != null && !board.getKeywords().isBlank())
                ? Arrays.asList(board.getKeywords().split(", "))
                : Collections.emptyList();

        List<ProductItem> productList = Collections.emptyList();
        List<ReferenceItem> referenceList = Collections.emptyList();

        if (board.getCategory() == ChatMode.PRODUCT) {
            productList = board.getBoardProducts().stream()
                    .map(ProductItem::from)
                    .toList();
        } else {
            referenceList = board.getBoardReferences().stream()
                    .map(ReferenceItem::from)
                    .toList();
        }

        return new BoardListResponse(
                board.getId(),
                board.getUserId(),
                board.getTitle(),
                parsedKeywords,
                board.getCategory(),
                productList,
                referenceList,
                board.getCreatedAt()
        );
    }
}