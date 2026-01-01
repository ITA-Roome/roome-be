package com.roome.roome.be.domain.user.repository;

import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.roome.roome.be.domain.product.dto.response.CommonProductInfo;
import com.roome.roome.be.domain.product.dto.response.ProductTagInfo;
import com.roome.roome.be.domain.product.enums.TagType;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.roome.roome.be.domain.product.entity.QProduct.product;
import static com.roome.roome.be.domain.product.entity.QProductImage.productImage;
import static com.roome.roome.be.domain.product.entity.QProductTag.productTag;
import static com.roome.roome.be.domain.product.entity.QTag.tag;
import static com.roome.roome.be.domain.user.entity.QUserScrapProduct.userScrapProduct;

@Repository
@RequiredArgsConstructor
public class UserScrapProductCustomRepositoryImpl implements UserScrapProductCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    SimpleExpression category =
        new CaseBuilder()
            .when(tag.type.eq(TagType.PRODUCT_TYPE))
            .then(tag.name)
            .otherwise((String) null);

    @Override
    public List<CommonProductInfo> findUserScrappedProductListByUserId(Long userId) {
        return jpaQueryFactory
                .from(userScrapProduct)
                .join(userScrapProduct.product, product)
                .leftJoin(product.productImageList, productImage)
                .leftJoin(product.productTagList, productTag)
                .leftJoin(productTag.tag, tag)
                .where(userScrapProduct.user.id.eq(userId))
                .orderBy(userScrapProduct.updatedAt.desc())
                .transform(
                        groupBy(product.id).list(
                                Projections.constructor(CommonProductInfo.class,
                                        product.id,
                                        product.name,
                                        category,
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
