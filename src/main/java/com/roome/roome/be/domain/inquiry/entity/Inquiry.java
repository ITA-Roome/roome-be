package com.roome.roome.be.domain.inquiry.entity;

import com.roome.roome.be.common.base.BaseEntity;
import com.roome.roome.be.domain.inquiry.enums.InquiryStatus;
import com.roome.roome.be.domain.inquiry.enums.InquiryType;
import com.roome.roome.be.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor()
@Builder
public class Inquiry extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(length = 300, nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InquiryStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InquiryType type;

    @OneToOne(mappedBy = "inquiry", cascade = CascadeType.ALL, orphanRemoval = true)
    private InquiryAnswer answer;

    public void updateInquiryStatus(InquiryStatus status) {
        this.status = status;
    }

}
