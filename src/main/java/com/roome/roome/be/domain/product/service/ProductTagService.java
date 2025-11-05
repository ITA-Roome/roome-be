package com.roome.roome.be.domain.product.service;

import com.roome.roome.be.common.exception.GeneralException;
import com.roome.roome.be.common.status.ErrorStatus;
import com.roome.roome.be.domain.product.entity.Product;
import com.roome.roome.be.domain.product.entity.ProductTag;
import com.roome.roome.be.domain.product.entity.Tag;
import com.roome.roome.be.domain.product.repository.ProductRepository;
import com.roome.roome.be.domain.product.repository.ProductTagRepository;
import com.roome.roome.be.domain.product.repository.TagRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductTagService {

	private final ProductRepository productRepository;
	private final TagRepository tagRepository;
	private final ProductTagRepository productTagRepository;

	@Transactional
	public void applyTags(Long productId, List<String> tagNames) {
		Product product = productRepository.findById(productId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.PRODUCT_NOT_FOUND));

		List<String> cleaned = (tagNames == null ? List.<String>of() : tagNames).stream()
			.map(String::trim).filter(string -> !string.isEmpty()).distinct().toList();

		List<Tag> currentTags = productTagRepository.findByProduct(product)
			.stream().map(ProductTag::getTag).toList();
		var current = currentTags.stream().map(Tag::getName).collect(java.util.stream.Collectors.toSet());

		List<Tag> targetTags = new java.util.ArrayList<>();
		for (String name : cleaned) {
			Tag t = tagRepository.findByName(name)
				.orElseGet(() -> tagRepository.save(Tag.builder().name(name).build()));
			targetTags.add(t);
		}
		var target = targetTags.stream().map(Tag::getName).collect(java.util.stream.Collectors.toSet());
		var toAddNames = new java.util.HashSet<>(target);   toAddNames.removeAll(current);
		var toDelNames = new java.util.HashSet<>(current);  toDelNames.removeAll(target);

		for (Tag tag : targetTags) {
			if (toAddNames.contains(tag.getName())) {
				productTagRepository.save(ProductTag.builder().product(product).tag(tag).build());
			}
		}

		for (Tag tag : currentTags) {
			if (toDelNames.contains(tag.getName())) {
				productTagRepository.deleteByProductAndTag(product, tag);
			}
		}
	}
}