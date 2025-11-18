package com.roome.roome.be.domain.reference.entity;

import com.roome.roome.be.common.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ReferenceImage extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reference_id", nullable = false)
    private Reference reference;

    @Column(nullable = false, length = 256, unique = true)
    private String objectKey;

}
