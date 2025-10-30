package com.roome.roome.be.domain.user.entity;

import com.roome.roome.be.common.base.BaseEntity;
import com.roome.roome.be.domain.user.enums.LoginType;
import com.roome.roome.be.domain.user.enums.Role;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String password;

    @Column(nullable = false, length = 20)
    private String nickname;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private LoginType loginType;

    @Column(nullable = false, length = 20)
    private String phoneNumber;

    private String providerId;

    private String refreshToken;

    @Enumerated(EnumType.STRING)
    private Role role;  // ← 여기!

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void clearRefreshToken() {
        this.refreshToken = null;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

}
