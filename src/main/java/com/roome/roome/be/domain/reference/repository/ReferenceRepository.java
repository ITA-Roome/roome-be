package com.roome.roome.be.domain.reference.repository;

import com.roome.roome.be.domain.reference.entity.Reference;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReferenceRepository extends JpaRepository<Reference, Long> {
    @EntityGraph(attributePaths = {"user", "referenceImageList"})
    List<Reference> findByNameContaining(String name);

    @Override
    @EntityGraph(attributePaths = {"user", "referenceImageList"})
    List<Reference> findAll();
}
