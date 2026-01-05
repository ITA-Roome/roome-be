package com.roome.roome.be.domain.reference.repository;

import com.roome.roome.be.domain.reference.entity.Reference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ReferenceRepository extends JpaRepository<Reference, Long> {
    @EntityGraph(attributePaths = {"user", "referenceImageList"})
    Page<Reference> findByNameContaining(String name, Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"user", "referenceImageList"})
    Page<Reference> findAll(Pageable pageable);
}
