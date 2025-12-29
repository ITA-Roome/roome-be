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

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer price;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "scrap_count",nullable = false)
    private Integer scrapCount;

    @Column(name = "like_count",nullable = false)
    private Integer likeCount;

    @Column(name = "reference_url", length = 1024, nullable = false)
    private String referenceUrl;

    @OneToMany(mappedBy = "reference", fetch = FetchType.LAZY)
    private List<ReferenceItem> referenceItemList = new ArrayList<>();

    @OneToMany(mappedBy = "reference", fetch = FetchType.LAZY)
    private List<ReferenceImage> referenceImageList = new ArrayList<>();

    @OneToMany(mappedBy = "reference", fetch = FetchType.LAZY)
    private List<ReferenceTag> referenceTagList = new ArrayList<>();

    public void incrementLikeCount() { this.likeCount++; }
    public void decrementLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }
}
