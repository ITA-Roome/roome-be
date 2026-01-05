package com.roome.roome.be.domain.user.repository;

import com.roome.roome.be.domain.reference.entity.Reference;
import com.roome.roome.be.domain.user.entity.User;
import com.roome.roome.be.domain.user.entity.UserScrapReference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserScrapReferenceRepository extends JpaRepository<UserScrapReference, Long> {
    Optional<UserScrapReference> findByUserAndReference(User user, Reference reference);
    boolean existsByUserIdAndReferenceId(Long userId, Long referenceId);

    @Query("SELECT r.reference.id FROM UserScrapReference r WHERE r.user.id = :userId AND r.reference.id IN :referenceIds")
    Set<Long> findScrappedReferenceIds(@Param("userId") Long userId, @Param("referenceIds") List<Long> referenceIds);
}
