package com.roome.roome.be.domain.reference.repository;

import com.roome.roome.be.domain.reference.entity.Reference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReferenceRepository extends JpaRepository<Reference, Long> {
}
