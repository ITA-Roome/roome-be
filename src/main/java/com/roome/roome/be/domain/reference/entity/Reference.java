package com.roome.roome.be.domain.reference.entity;

import com.roome.roome.be.common.base.BaseEntity;
import com.roome.roome.be.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Reference extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "scrap_count",nullable = false)
    private Integer scrapCount;

    @OneToMany(mappedBy = "reference", fetch = FetchType.LAZY)
    private List<ReferenceImage> referenceImageList = new ArrayList<>();

    @Column(name = "like_count",nullable = false)
    private Integer likeCount;

    public void incrementLikeCount() { this.likeCount++; }
    public void decrementLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }
}
