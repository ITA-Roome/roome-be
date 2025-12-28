package com.roome.roome.be.domain.user.repository;

import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.roome.roome.be.domain.product.dto.response.CommonProductInfo;
import com.roome.roome.be.domain.product.dto.response.ProductTagInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.roome.roome.be.domain.product.entity.QProduct.product;
import static com.roome.roome.be.domain.product.entity.QProductImage.productImage;
import static com.roome.roome.be.domain.product.entity.QProductTag.productTag;
import static com.roome.roome.be.domain.product.entity.QTag.tag;
import static com.roome.roome.be.domain.user.entity.QUserLikeProduct.*;

@Repository
@RequiredArgsConstructor
public class UserLikeProductCustomRepositoryImpl implements UserLikeProductCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<CommonProductInfo> findUserLikeProductListByUserId(Long userId) {
        return jpaQueryFactory
                .from(userLikeProduct)
                .join(userLikeProduct.product, product)
                .leftJoin(product.productImageList, productImage)
                .leftJoin(product.productTagList, productTag)
                .leftJoin(productTag.tag, tag)
                .where(userLikeProduct.user.id.eq(userId))
                .orderBy(userLikeProduct.updatedAt.desc())
                .transform(
                        groupBy(product.id).list(
                                Projections.constructor(CommonProductInfo.class,
                                        product.id,
                                        product.name,
                                        product.category,
                                        product.price,
                                        product.description,
                                        product.productUrl,
                                        product.thumbnailKey,
                                        GroupBy.set(productImage.imageUrl),
                                        GroupBy.set(
                                                Projections.constructor(
                                                        ProductTagInfo.class,
                                                        tag.id,
                                                        tag.name,
                                                        tag.type
                                                )
                                        ),
                                        product.createdAt,
                                        product.updatedAt
                                )
                        )
                );
    }
}
