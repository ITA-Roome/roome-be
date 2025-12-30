package com.roome.roome.be.domain.user.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.roome.roome.be.domain.reference.entity.Reference;
import com.roome.roome.be.domain.user.entity.User;
import com.roome.roome.be.domain.user.entity.UserLikeReference;

public interface UserLikeReferenceRepository extends JpaRepository<UserLikeReference, Long> {
    public Optional<UserLikeReference> findByUserAndReference(User user, Reference reference);

    @Query("SELECT ulr.reference.id FROM UserLikeReference ulr WHERE ulr.user.id = :userId AND ulr.reference.id IN :referenceIds")
    Set<Long> findLikedReferenceIds(@Param("userId") Long userId, @Param("referenceIds") List<Long> referenceIds);

    boolean existsByUserIdAndReferenceId(Long userId, Long referenceId);
}

