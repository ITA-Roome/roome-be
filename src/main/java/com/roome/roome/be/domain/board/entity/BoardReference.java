package com.roome.roome.be.domain.board.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "board_reference")
public class BoardReference {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_reference_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @Column(nullable = false)
    private Long referenceId;

    @Column(nullable = false, length = 2048)
    private String imageUrl;

    public void assignBoard(Board board) {
        this.board = board;
    }
}