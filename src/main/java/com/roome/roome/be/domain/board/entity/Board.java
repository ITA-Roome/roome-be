package com.roome.roome.be.domain.board.entity;

import com.roome.roome.be.common.base.BaseEntity;
import com.roome.roome.be.domain.chat.enums.ChatMode;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "board")
public class Board extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChatMode category;

    private String keywords;

    @Builder.Default
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BoardProduct> boardProducts = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BoardReference> boardReferences = new ArrayList<>();

    public void addProduct(BoardProduct product) {
        this.boardProducts.add(product);
        product.assignBoard(this);
    }

    public void addReference(BoardReference reference) {
        this.boardReferences.add(reference);
        reference.assignBoard(this);
    }
}
