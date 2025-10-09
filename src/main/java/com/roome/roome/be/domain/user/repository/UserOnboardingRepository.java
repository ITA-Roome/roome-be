package com.roome.roome.be.domain.user.repository;

import com.roome.roome.be.domain.user.entity.User;
import com.roome.roome.be.domain.user.entity.UserOnboarding;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserOnboardingRepository extends JpaRepository<UserOnboarding, Long> {
    Optional<UserOnboarding> findByUser(User user);
}
