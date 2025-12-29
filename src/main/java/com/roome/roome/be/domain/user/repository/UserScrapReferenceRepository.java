package com.roome.roome.be.domain.user.repository;

import com.roome.roome.be.domain.reference.entity.Reference;
import com.roome.roome.be.domain.user.entity.User;
import com.roome.roome.be.domain.user.entity.UserScrapReference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserScrapReferenceRepository extends JpaRepository<UserScrapReference, Long> {
    Optional<UserScrapReference> findByUserAndReference(User user, Reference reference);
    boolean existsByUserIdAndReferenceId(Long userId, Long referenceId);
}
