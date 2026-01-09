package com.roome.roome.be.domain.reference.repository;


import com.roome.roome.be.domain.reference.entity.ReferenceTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReferenceTagRepository extends JpaRepository<ReferenceTag, Long> {

}
