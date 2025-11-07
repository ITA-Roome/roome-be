package com.roome.roome.be.domain.product.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.roome.roome.be.domain.product.entity.Tag;
import com.roome.roome.be.domain.product.enums.TagType;

public interface TagRepository extends JpaRepository<Tag, Long> {

	Optional<Tag> findByNameAndType(String name, TagType type);
}
