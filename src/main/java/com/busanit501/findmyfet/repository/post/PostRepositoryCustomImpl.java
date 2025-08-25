package com.busanit501.findmyfet.repository.post;

import com.busanit501.findmyfet.domain.post.Post;

import com.busanit501.findmyfet.domain.post.QPost;
import com.busanit501.findmyfet.dto.paging.PageRequestDTO;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import java.util.List;

@RequiredArgsConstructor
public class PostRepositoryCustomImpl implements PostRepositoryCustom{

    private final JPAQueryFactory queryFactory;


    @Override
    public Page<Post> search(PageRequestDTO pageRequestDTO, Pageable pageable) {
        QPost post = QPost.post;
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        // 검색 조건 추가
        if (StringUtils.hasText(pageRequestDTO.getType())) {
            booleanBuilder.and(post.postType.stringValue().eq(pageRequestDTO.getType()));
        }
        if (StringUtils.hasText(pageRequestDTO.getCategory())) {
            booleanBuilder.and(post.animalCategory.eq(pageRequestDTO.getCategory()));
        }
        if (StringUtils.hasText(pageRequestDTO.getRegion())) {
            // 'region' 필드는 Post 엔티티에 없으므로, user.address 기준으로 검색하는 예시
            // 또는 Post 엔티티에 'region' 필드를 추가해야 합니다. 여기서는 user.address로 가정
            booleanBuilder.and(post.user.address.contains(pageRequestDTO.getRegion()));
        }
        if (StringUtils.hasText(pageRequestDTO.getKeyword())) {
            booleanBuilder.and(post.title.contains(pageRequestDTO.getKeyword())
                    .or(post.content.contains(pageRequestDTO.getKeyword())));
        }

        // 쿼리 생성
        JPAQuery<Post> query = queryFactory.selectFrom(post)
                .where(booleanBuilder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(post.createdAt.desc());

        List<Post> content = query.fetch();

        // 전체 카운트 쿼리 (성능을 위해 분리)
        JPAQuery<Long> countQuery = queryFactory.select(post.count())
                .from(post)
                .where(booleanBuilder);

        long total = countQuery.fetchOne();

        return new PageImpl<>(content, pageable, total);
    }
}
