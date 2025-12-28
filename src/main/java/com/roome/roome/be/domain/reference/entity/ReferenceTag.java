package com.roome.roome.be.domain.reference.entity;

import com.roome.roome.be.common.base.BaseEntity;
import com.roome.roome.be.domain.product.entity.Tag;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ReferenceTag extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reference", nullable = false) // ERD 컬럼명과 정확히 맞춤
    private Reference reference;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tag", nullable = false)
    private Tag tag;
}
