package com.roome.roome.be.domain.product.service;

import com.roome.roome.be.common.exception.GeneralException;
import com.roome.roome.be.common.status.ErrorStatus;
import com.roome.roome.be.domain.product.dto.request.TagUpsertRequest;
import com.roome.roome.be.domain.product.entity.Product;
import com.roome.roome.be.domain.product.entity.ProductTag;
import com.roome.roome.be.domain.product.entity.Tag;
import com.roome.roome.be.domain.product.enums.TagType;
import com.roome.roome.be.domain.product.repository.ProductRepository;
import com.roome.roome.be.domain.product.repository.ProductTagRepository;
import com.roome.roome.be.domain.product.repository.TagRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductTagService {

	private final ProductRepository productRepository;
	private final TagRepository tagRepository;
	private final ProductTagRepository productTagRepository;

	@Transactional
	public void applyTags(Long productId, List<TagUpsertRequest> upserts) {
		Product product = productRepository.findById(productId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.PRODUCT_NOT_FOUND));

		List<TagUpsertRequest> cleaned = (upserts == null ? List.<TagUpsertRequest>of() : upserts).stream()
			.filter(tagUpsertRequest -> tagUpsertRequest != null && tagUpsertRequest.name() != null && !tagUpsertRequest.name().trim().isEmpty() && tagUpsertRequest.type() != null)
			.map(tagUpsertRequest -> new TagUpsertRequest(tagUpsertRequest.name().trim(), tagUpsertRequest.type()))
			.distinct()
			.toList();

		List<ProductTag> currentLinks = productTagRepository.findByProduct(product);
		List<Tag> currentTags = currentLinks.stream().map(ProductTag::getTag).toList();

		record Key(String name, TagType type) {
		}
		var currentSet = currentTags.stream()
			.map(tag -> new Key(tag.getName(), tag.getType()))
			.collect(Collectors.toSet());

		List<Tag> targetTags = new ArrayList<>();
		for (TagUpsertRequest tagUpsertRequest : cleaned) {
			Tag tag = tagRepository.findByNameAndType(tagUpsertRequest.name(), tagUpsertRequest.type())
				.orElseGet(() -> tagRepository.save(
					Tag.builder().name(tagUpsertRequest.name()).type(tagUpsertRequest.type()).build()
				));
			targetTags.add(tag);
		}
		var targetSet = targetTags.stream()
			.map(tag -> new Key(tag.getName(), tag.getType()))
			.collect(Collectors.toSet());

		var toAdd = new HashSet<>(targetSet);
		toAdd.removeAll(currentSet);
		var toDel = new HashSet<>(currentSet);
		toDel.removeAll(targetSet);

		for (Tag tag : targetTags) {
			Key key = new Key(tag.getName(), tag.getType());
			if (toAdd.contains(key)) {
				productTagRepository.save(ProductTag.builder().product(product).tag(tag).build());
			}
		}
		for (ProductTag link : currentLinks) {
			Key key = new Key(link.getTag().getName(), link.getTag().getType());
			if (toDel.contains(key)) {
				productTagRepository.delete(link);
			}
		}
	}
}