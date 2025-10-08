package com.roome.roome.be.domain.user.repository;

import com.roome.roome.be.domain.user.entity.User;
import com.roome.roome.be.domain.user.enums.LoginType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByLoginTypeAndProviderId(LoginType loginType, String providerId);
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
    Optional<User> findByEmail(String email);
    Optional<User> findById(Long id);
    Optional<User> findUserByPhoneNumber(String phoneNumber);

    @Modifying
    @Query("DELETE FROM User u " +
            "WHERE u.id = :id")
    void deleteById(@Param("id") Long id);
}
