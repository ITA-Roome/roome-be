package com.roome.roome.be.domain.reference.repository;

import com.roome.roome.be.domain.reference.entity.ReferenceImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReferenceImageRepository extends JpaRepository<ReferenceImage, Long> {
}
