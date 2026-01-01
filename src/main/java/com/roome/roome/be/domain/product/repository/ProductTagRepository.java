package com.roome.roome.be.domain.product.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.roome.roome.be.domain.product.entity.Product;
import com.roome.roome.be.domain.product.entity.ProductTag;
import com.roome.roome.be.domain.product.enums.TagType;

public interface ProductTagRepository extends JpaRepository<ProductTag, Long> {

	interface ProductTypeRow {
		Long getProductId();
		String getCategoryName();
	}

	@Query("""
    select pt.product.id as productId, max(t.name) as categoryName
    from ProductTag pt
    join pt.tag t
    where pt.product.id in :productIds
      and t.type = :type
    group by pt.product.id
""")
	List<ProductTypeRow> findProductTypeByProductIds(  @Param("productIds") List<Long> productIds,
		@Param("type") TagType type);

	List<ProductTag> findByProduct(Product product);

	@Query("""
        select pt
        from ProductTag pt
        join fetch pt.tag t
        where pt.product.id = :productId
    """)
	List<ProductTag> findByProductIdWithTag(@Param("productId") Long productId);

	@Modifying
	@Query("delete from ProductTag pt where pt.product.id = :productId")
	void deleteByProductId(@Param("productId") Long productId);
}