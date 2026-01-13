package com.roome.roome.be.domain.board.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "board_product")
public class BoardProduct {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_product_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 2048)
    private String imageUrl;

    @Column(length = 1000)
    private String reason;

    @Column(length = 1000)
    private String advantage;

    private String mood;

    private String recommendedPlace;

    public void assignBoard(Board board) {
        this.board = board;
    }
}